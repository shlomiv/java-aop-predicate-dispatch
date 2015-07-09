// Fibonachi Demo	
// 
// an example demonstrating selecting an implementation based on type and _value_ of all arguments.
// by using the resolveList function, we simplify the definition of our "switch cases" for the execution, 
// each line is evaluated in the order given. 

package dispatchs.test;

import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.inspector;
import dispatchs.inspect.Helper.Builder;
import dispatchs.inspect.Helper.Resolver;
import fj.F;
import fj.data.Enumerator;
import fj.data.List;
import fj.data.Stream;

public class FibonachiTest2 {
	
	// a helper list to make writing the dispatch more declarative
	@SuppressWarnings("unchecked")
	static F<List<Object>, String>b = new Builder()
			.<Integer>pred(List.list((Integer x)->x>2), "Rest")                // first argument is any integer
			.build();
	
	// set up the specific resolver function
	static public class FibResolver implements Resolver {
		public String choose(Object... args) {
			// use a helper method that uses our list selector as a declarative dispatch function
			return b.f(List.list(args)); 
		}};	
	
	// In this example, we use the default case as the recursion base
	@DefMulti(f = FibResolver.class, name = "fib")
	public Integer fib(Integer a) {
		return 1;
	}

	// Define recursive case method
	@DefMethod(name = "fib", selector = "Rest")
	public Integer fib2(Integer a) {
		return fib(a - 1) + fib(a - 2);
	}
		
	// Run it 
	public static void main(String[] args) {
		System.out.println("Fibonacci: " + new FibonachiTest2().fib(9));
    }
}

