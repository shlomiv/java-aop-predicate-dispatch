package dispatchs.test;

import dispatchs.inspect.DefMethod;
import dispatchs.test.externalDemo.FurnituresLibrary;
import dispatchs.test.externalDemo.FurnituresLibrary.Furniture;

public class FurnitureTest {
	static public class Sofa extends Furniture{
		@Override public String name() {
			return "Sofa";
		}	
	}
	
	@DefMethod(name="fornitrure-builder", selector="Sofa", external=true)
	public static Boolean builder_chair(FurnituresLibrary t, Furniture f) {
		System.out.println("Building the sofa!!! " + t.someState());
		
		return true;
	}
	
	public static void main(String[] args) {
		new FurnituresLibrary().builder(new Sofa());
	}

}
