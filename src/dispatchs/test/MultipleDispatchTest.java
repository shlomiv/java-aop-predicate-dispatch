// MultipleDispatch Demo
//
// Using the generic ability to supply a selector function as a function of the 
// arguments (on both types and values), I was able to derive a mechanism for implicitly
// handling multiple dispatch. This example demonstrates calling the method f, with runtime
// variable types, so at compile time it is unknown which implementation to pick.
//
// Note that the order of implementation is NOT significant, which means that an order between
// parameter list declarations had to be defined, thus making sure that the most general case
// does not take over some or all other cases. 

package dispatchs.test;

import dispatchs.inspect.MultipleDispatch;

public class MultipleDispatchTest {
	static class A{};
	static class C1 extends A{};
	static class C2 extends A{};
	static class D  extends C1{};

	@MultipleDispatch
	public int f(C1 a, C2 b) {
		return 2;
	}
	
	// Most general case, does not take over, try to move this method around
	@MultipleDispatch
	public int f(A a, A b) {
		return 3;
	}

	@MultipleDispatch
	public int f(C2 a, C1 b) {	
		return 1;
	}

	@MultipleDispatch
	public int f(D a, C1 b) {	
		return 4;
	}

	
	public static void main(String[] args) {
		A[] a = new A[3];
		a[0] = new C2();
		a[1] = new D();
		System.out.println(new MultipleDispatchTest().f(a[0], a[1]) + " => C2, D>C1   : 1");
		
		a[0] = new D();
		a[1] = new C2();
		System.out.println(new MultipleDispatchTest().f(a[0], a[1]) + " => D>C1, C2   : 2");
		
		a[0] = new C1();
		a[1] = new C2();
		System.out.println(new MultipleDispatchTest().f(a[0], a[1]) + " => C1, C2     : 2");
		
		a[0] = new D();
		a[1] = new C1();
		System.out.println(new MultipleDispatchTest().f(a[0], a[1]) + " => D, C1      : 4");
				
		a[0] = new C2();
		a[1] = new C2();
		System.out.println(new MultipleDispatchTest().f(a[0], a[1]) + " => C2>A, C2>A : 3");
	}
}
