// an example of some closed sourced and even final (eww) class
// that exposes a multimethod, thus allowing thus allowing third-parties
// to extend on its functionality without much hassle

package dispatchs.test.externalDemo;

import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Resolver;

final public class FurnituresLibrary {
	
	// some class hierarchy, can be defined anywhere
	public static class Furniture{
		public String name() {return "None";}
	};
	public static class Chair extends Furniture{
		@Override public String name() {
			return "Chair";
		}
	};
	
	// some state of the library
	public String someState() {
		return "{state in furniture library}";
	}
	
	// resolve function simply grabs the value of name() method from a piece of furniture
	static public class FurnitureResolver implements Resolver {
		@Override
		public String choose(Object... args) {
			return ((Furniture)args[0]).name();
		}
	}
	
	// define the multimethod using our resolver
	@DefMulti(f = FurnitureResolver.class, name="fornitrure-builder")
	public Boolean builder(Furniture f) {
		return false;
	}
	
	// define an example first implementation for it
	@DefMethod(name="fornitrure-builder", selector="Chair")
	public Boolean builder_chair(Furniture f) {
		System.out.println("Building the chair!!!");
		return true;
	}
}
