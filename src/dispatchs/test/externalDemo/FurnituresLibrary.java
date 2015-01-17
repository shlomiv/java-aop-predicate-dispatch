package dispatchs.test.externalDemo;

import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Dispatcher;

public class FurnituresLibrary {
	public static class Furniture{
		public String name() {return "None";}
	};
	public static class Chair extends Furniture{
		@Override public String name() {
			return "Chair";
		}
	};
	
	public String someState() {
		return "{state in furniture library}";
	}
		
	static public class FurnitureResolver implements Dispatcher {
		@Override
		public String choose(Object... args) {
			return ((Furniture)args[0]).name();
		}
	}
	
	@DefMulti(f = FurnitureResolver.class, name="fornitrure-builder")
	public Boolean builder(Furniture f) {
		return false;
	}
	
	@DefMethod(name="fornitrure-builder", selector="Chair")
	public Boolean builder_chair(Furniture f) {
		System.out.println("Building the chair!!!");
		return true;
	}
}
