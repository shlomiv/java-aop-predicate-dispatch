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
	
	@MultipleDispatch
	public int f(C2 a, C1 b) {	
		return 1;
	}
	
	@MultipleDispatch
	public int f(D a, C1 b) {	
		return 4;
	}
	
	@MultipleDispatch
	public int f(A a, A b) {
		return 3;
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
