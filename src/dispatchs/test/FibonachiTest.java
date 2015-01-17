package dispatchs.test;

import static dispatchs.inspect.Helper.resolveList;
import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Builder;
import dispatchs.inspect.Helper.Dispatcher;
import dispatchs.inspect.Helper.Pred;
import dispatchs.test.externalDemo.FurnituresLibrary.Chair;
import fj.P2;
import fj.data.List;

public class FibonachiTest {
	
	// Set up method selector
	@SuppressWarnings("unchecked")
	static List<P2<List<Pred>, String>> b = new Builder()
								.objVal(List.list(Integer.class), List.list(1), "First")
								.objVal(List.list(Integer.class), List.list(2), "First")
								.obj   (List.list(Integer.class), "Rest")
								.build();
	
	static public class FibResolver implements Dispatcher {
		public String choose(Object... args) {
			return resolveList(b, args);
		}};	
	
	// Define multimethod 
	@DefMulti(f = FibResolver.class, name = "fib")
	public Integer fib(Integer a) {
		return -1;
	}

	// Define case methods
	@DefMethod(name = "fib", selector = "First")
	public Integer fib1(Integer a) {
		return 1;
	}

	@DefMethod(name = "fib", selector = "Rest")
	public Integer fib2(Integer a) {
		return fib(a - 1) + fib(a - 2);
	}
	
	// Run it 
	public static void main(String[] args) {
    	System.out.println("Fibonacci: " + new FibonachiTest().fib(9));
    }
}

