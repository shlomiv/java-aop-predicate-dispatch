package dispatchs.test.externalDemo;

import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Resolver;

public class Show {
	// resolve function simply grabs the value of name() method from a piece of furniture
		static public class ShowResolver implements Resolver {
			@Override
			public String choose(Object... args) {
				System.out.println(args[0].getClass().getSimpleName());
				return (args[0].getClass().getName());
			}
		}
		
		@DefMulti(f = ShowResolver.class, name="show")
		public String show(Object o) {
			return "cant show";
		}
		
		@DefMethod(name="show", selector="Integer")
		public String show_int(Integer f) {
			return "int: " + f.toString();
		}
		
		public static void main(String[] args) {
			System.out.println(new Show().show(5));
		}
}
