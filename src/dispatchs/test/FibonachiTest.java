// Fibonachi Demo	
// 
// an example demonstrating selecting an implementation based on type and _value_ of all arguments.
// by using the resolveList function, we simplify the definition of our "switch cases" for the execution, 
// each line is evaluated in the order given. 

package dispatchs.test;

import static dispatchs.inspect.Helper.resolveList;
import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Builder;
import dispatchs.inspect.Helper.Pred;
import dispatchs.inspect.Helper.Resolver;
import fj.P2;
import fj.data.List;

public class FibonachiTest {

	// a helper list to make writing the dispatch more declarative
	@SuppressWarnings("unchecked")
	static List<P2<List<Pred>, String>> b = new Builder()
			.objVal(List.list(Integer.class), List.list(1), "First") // first argument is 1
			.objVal(List.list(Integer.class), List.list(2), "First") // first argument is 2
			.obj   (List.list(Integer.class), "Rest")                // first argument is any integer
			.build();
	
	// set up the specific resolver function
	static public class FibResolver implements Resolver {
		public String choose(Object... args) {
			// use a helper method that uses our list selector as a declarative dispatch function
			return resolveList(b, args); 
		}};	
	
	// Define multimethod, the body of this function will only execute in case of an error in the selector
	@DefMulti(f = FibResolver.class, name = "fib")
	public Integer fib(Integer a) {
		return -1;
	}

	// Define base case method
	@DefMethod(name = "fib", selector = "First")
	public Integer fib1(Integer a) {
		return 1;
	}

	// Define recursive case method
	@DefMethod(name = "fib", selector = "Rest")
	public Integer fib2(Integer a) {
		return fib(a - 1) + fib(a - 2);
	}
	
	// Run it 
	public static void main(String[] args) {
    	System.out.println("Fibonacci: " + new FibonachiTest().fib(9));
    }
}

