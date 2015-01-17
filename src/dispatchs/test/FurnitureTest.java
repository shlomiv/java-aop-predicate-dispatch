// Furniture Demo
// 	
// This demo shows how we can implement handlers for multimethods outside the original scope.
// This allows us to decouple implementation from definition, and gives another convenient way for
// extending functionality other than inheritance.
//
// Assume here that we have no access to FurnitureLibrary source, which is also declared final, 
// and want to add functionality that inheritance would be impossible (in this case) or cumbersome (in most cases).

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
