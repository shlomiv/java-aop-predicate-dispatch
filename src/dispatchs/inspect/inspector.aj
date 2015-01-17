package dispatchs.inspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.reflect.MethodSignature;

import dispatchs.inspect.Helper.Builder;
import dispatchs.inspect.Helper.Resolver;
import dispatchs.inspect.Helper.Pred;
import fj.Ord;
import fj.Ordering;
import fj.P;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.TreeMap;


public aspect inspector {
	Show<List<String>> s = Show.listShow(Show.stringShow);
	
	pointcut init() : staticinitialization(!dispatchs.inspect.*);

	// This portion happens on a class initialization, it is responsible for registering multimethod implementations
	// and supports our multiple dispatch add-on
	before() : init()  {

		// multimethod support
		List<P2<Method, DefMethod>> mmethods = List.list(thisJoinPoint.getSignature().getDeclaringType().getMethods())
				.map(x->P.p(x,x.getAnnotationsByType(DefMethod.class)))
				.filter(x->x._2().length>0)
				.map(x->P.p(x._1(), (DefMethod)x._2()[0]));		
					
		Helper.methodMap     = mmethods.foldLeft ((map, n)->map.set(n._2().name(), map.get(n._2().name()).orSome(TreeMap.empty(Ord.stringOrd)).set(n._2().selector(), n._1())), Helper.methodMap);
		
//		System.out.println("Inspecting " + thisJoinPoint.getStaticPart().getSignature().getDeclaringType().getMethods());
//		System.out.println(Show.listShow(Show.p2Show(Show.stringShow.<Method>comap(f->f.getName()), Show.stringShow.<DefMethod>comap(f->f.toString()))).showS(mmethods));
//		System.out.println("All methods: " + Show.listShow(Show.stringShow).showS(Helper.methodMap.keys()));
		
		
		// Multiple dispatch support
		List<P2<Method, MultipleDispatch>> multipleDispatchs = List.list(thisJoinPoint.getSignature().getDeclaringType().getMethods())
				.map(x->P.p(x,x.getAnnotationsByType(MultipleDispatch.class)))
				.filter(x->x._2().length>0)
				.map(x->P.p(x._1(), (MultipleDispatch)x._2()[0]));		
		
		Helper.multipleDispatch = multipleDispatchs.foldLeft ((map, n)->map.set(n._1().getName(), map.get(n._1().getName()).orSome(List.<List<Class>>nil()).cons(List.list(n._1().getParameterTypes()))) , Helper.multipleDispatch); 
				
		Helper.methodMap     = multipleDispatchs.foldLeft ((map, n)->map.set(n._1().getName(), map.get(n._1().getName()).orSome(TreeMap.empty(Ord.stringOrd)).set(s.showS(List.list(n._1().getParameterTypes()).map(x->x.getName())), n._1())), Helper.methodMap); 
	}
	
	// This section handles a call to a multimethod
	Object around():
		execution(@DefMulti * *(..)) 
		{
		DefMulti l = ((MethodSignature)thisJoinPoint.getSignature()).getMethod().getAnnotationsByType(DefMulti.class)[0];
		
		try {
			String select = ((Resolver)l.f().newInstance()).choose(thisJoinPoint.getArgs());
			
			//System.out.println("Got: " + select + " : " + thisJoinPoint.getArgs()[0]);

			// get the required method from the registry
			Method m = Helper.methodMap.get(l.name()).valueE("No implementations for " + l.name()).get(select).valueE("No matching implementation for " + select);
			
			// in case we are extending from an external location, add this as the first parameter
			if (m.getAnnotationsByType(DefMethod.class)[0].external()) {
				Object [] args = thisJoinPoint.getArgs();
				Object [] na = new Object[args.length+1];
				na[0] = thisJoinPoint.getThis();
				System.arraycopy(args, 0, na, 1, args.length);
				return m.invoke(null, na);		
			}
			else {
				// if we are extending internally, this is already correct, so just invoke it
				return m.invoke(thisJoinPoint.getThis(), thisJoinPoint.getArgs());
			}
						
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException e) {
			System.out.println("Error invoking...");
			e.printStackTrace();
		} catch (InvocationTargetException e) {	
			e.printStackTrace();
			return null;
		}
		
		// in case of an error, call the original function, which serves as an error handler
		return proceed();
	}
	
	// the following section is responsible for handling the implicit multiple dispatch
	
	// this is a neat trick, to know not to recurse our advice
	Object invoker(Method m, Object This, Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return m.invoke(This, args);
	}	
	
	Object around():
		execution(@MultipleDispatch * *(..)) && !cflowbelow(call(* inspector.invoker(..))) 
		{
		
		String name = thisJoinPoint.getSignature().getName();
		
		// order of methods is defined by checking if a signature is made up entirely of superclasses of other 
		// signatures. this is important so that most-generic implementation would not overtake everything else.
		Ord<List<Class>> or = Ord.<List<Class>>ord(a->b->a.zip(b).forall(x->x._2().isAssignableFrom(x._1()))?Ordering.LT:Ordering.EQ);
		
		// get the list of methods by that order
		List<List<Class>> l = Helper.multipleDispatch.get(name).valueE("NONE").sort(or);
		
//		Show<List<Class>> s1 = Show.listShow(Show.stringShow.<Class>comap(f->f.getName()));
//		System.out.println("MD - order");
//		for (List<Class> list : l) {
//			System.out.println("  " + s1.showS(list));
//		}
		
		// construct our declarative predicate list, for use with the resolveList function
		List<P2<List<Pred>, String>> b = l.foldLeft(builder->option->builder.obj(option, s.showS(option.map(x->x.getName()))), new Builder()).build();
//		System.out.println("Trying ");
//		for (Object i : thisJoinPoint.getArgs()) {
//			System.out.println("   " + i);
//		}
		
		// user resolveList function with the dispatch list to obtain the correct method implementation
		String select = Helper.resolveList(b, thisJoinPoint.getArgs());
//		System.out.println("Got: " + select);		
		
		try {			
			Method m = Helper.methodMap.get(name).valueE("No implementations for " + name).get(select).valueE("No matching implementation for " + select);
			
			// invoke that method using the trick to eliminate advice recursion
			return invoker(m, thisJoinPoint.getThis(), thisJoinPoint.getArgs());
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return proceed();
		
//		System.out.println(Show.listShow(Show.stringShow).showS(GlobalData.methodMap.keys()));
//		System.out.println("got " + thisJoinPoint.getSignature().getName() + " "+ GlobalData.methodMap.get(thisJoinPoint.getSignature().getName()));
	}
}

