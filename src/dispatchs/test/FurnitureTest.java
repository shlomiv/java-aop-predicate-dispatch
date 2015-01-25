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
	// Extend some data-structure we want to dispatch on
	static public class Sofa extends Furniture{
		@Override public String name() {
			return "Sofa";
		}	
	}
	
	// Extend the library's multimethod to now support our own extension. 
	// Extensions method must be static, and receives the original object reference (this)
	// as the first parameter
	@DefMethod(name="fornitrure-builder", selector="Sofa", external=true)
	public static Boolean builder_chair(FurnituresLibrary _this, Furniture f) {
		System.out.println("Building the sofa!!! " + _this.someState());
		
		return true;
	}
	
	// call the original library method with our extended data-structure
	public static void main(String[] args) {
		new FurnituresLibrary().builder(new Sofa());
	}
}
