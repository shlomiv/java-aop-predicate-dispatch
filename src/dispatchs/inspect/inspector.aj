package dispatchs.inspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import dispatchs.inspect.Helper.Resolver;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.TreeMap;
import fj.function.Effect1;

@DispatchExtension
public aspect inspector {
	Show<List<String>> s = Show.listShow(Show.stringShow);
	static List<Effect1<JoinPoint>> initfs = List.nil();
		
	pointcut init() : staticinitialization(!@DispatchExtension *);
    public static void registerInit(Effect1<JoinPoint> callback) {
    	initfs = initfs.cons(callback);
	}
	
    before() : init()  {
    	List<P2<Method, DefMethod>> mmethods = List.list(thisJoinPoint.getSignature().getDeclaringType().getMethods())
				.map(x->P.p(x,x.getAnnotationsByType(DefMethod.class)))
				.filter(x->x._2().length>0)
				.map(x->P.p(x._1(), (DefMethod)x._2()[0]));		
//					System.out.println(thisJoinPoint.getSignature().getDeclaringType());
		Helper.methodMap     = mmethods.foldLeft ((map, n)->map.set(n._2().name(), map.get(n._2().name()).orSome(TreeMap.empty(Ord.stringOrd)).set(n._2().selector(), n._1())), Helper.methodMap);
	}
    
	Object around(): execution(@DefMulti * *(..)) {
		DefMulti l = ((MethodSignature)thisJoinPoint.getSignature()).getMethod().getAnnotationsByType(DefMulti.class)[0];
		
		try {
			String select = ((Resolver)l.f().newInstance()).choose(thisJoinPoint.getArgs());
			
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
			System.err.println("Error invoking...");
			e.printStackTrace();
		} catch (InvocationTargetException e) {	
			e.printStackTrace();
			return null;
		} catch (Error e) {
			e.printStackTrace();
			System.err.println(" Executing error handler implementation (" + thisJoinPoint.getSignature()+")");
		}
		
		// in case of an error, call the original function, which serves as an error handler
		return proceed();
	}
}

