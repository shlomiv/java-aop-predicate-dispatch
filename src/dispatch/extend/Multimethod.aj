package dispatch.extend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dispatchs.inspect.DispatchExtension;
import dispatchs.inspect.Helper;
import dispatchs.inspect.Helper.Builder;
import fj.F;
import fj.Ord;
import fj.Ordering;
import fj.P;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.TreeMap;

@DispatchExtension
public aspect Multimethod {
	static Show<List<String>> s = Show.listShow(Show.stringShow);
	static TreeMap<String, List<List<Class>>> multipleDispatch = TreeMap.empty(Ord.stringOrd); 
	
	pointcut init() : staticinitialization(!@DispatchExtension *);
	before() : init()  {
		// Multiple dispatch support
		List<P2<Method, MultipleDispatch>> multipleDispatchs = List.list(thisJoinPoint.getSignature().getDeclaringType().getMethods())
				.map(x->P.p(x,x.getAnnotationsByType(MultipleDispatch.class)))
				.filter(x->x._2().length>0)
				.map(x->P.p(x._1(), (MultipleDispatch)x._2()[0]));		

		multipleDispatch = multipleDispatchs.foldLeft((map, n)->map.set(n._1().getName(), map.get(n._1().getName()).orSome(List.<List<Class>>nil()).cons(List.list(n._1().getParameterTypes()))) , multipleDispatch); 				
		
		Helper.methodMap = multipleDispatchs.foldLeft((map, n)->map.set(n._1().getName(), map.get(n._1().getName()).orSome(TreeMap.empty(Ord.stringOrd)).set(s.showS(List.list(n._1().getParameterTypes()).map(x->x.getName())), n._1())), Helper.methodMap); 
	}
	
	Object invoker(Method m, Object This, Object[] args) {
		try {
			return m.invoke(This, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		} 
	}	
	
	Object around(): execution(@MultipleDispatch * *(..)) && !cflowbelow(call(* Multimethod.invoker(..))) 
	{	
		String name = thisJoinPoint.getSignature().getName();
			
		// order of methods is defined by checking if a signature is made up entirely of superclasses of other 
		// signatures. this is important so that most-generic implementation would not overtake everything else.
		Ord<List<Class>> or = Ord.<List<Class>>ord(a->b->a.zip(b).forall(x->x._2().isAssignableFrom(x._1()))?Ordering.LT:Ordering.EQ);
			
		// get the list of methods by that order
		List<List<Class>> l = multipleDispatch.get(name).valueE("NONE").sort(or);
							
		// construct our declarative predicate list, for use with the resolveList function
		F<List<Object>, String> b = l.foldLeft(builder->option->builder.obj(option, s.showS(option.map(x->x.getName()))), new Builder()).build();
			
		// user resolveList function with the dispatch list to obtain the correct method implementation
		String select = b.f(List.list(thisJoinPoint.getArgs()));
			
		try {
		  return Helper.methodMap.get(name).bind(t->t.get(select).map(m->invoker(m, thisJoinPoint.getThis(), thisJoinPoint.getArgs()))).valueE("Error invoking multimethod");
		} catch (Error r) {
			r.printStackTrace();
			return null;
		}
	}
}
