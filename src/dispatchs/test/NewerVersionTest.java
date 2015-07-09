package dispatchs.test;

import dispatchs.inspect.DefMethod;
import dispatchs.test.externalDemo.VersionedLibrary;

public class NewerVersionTest {
	@DefMethod(name="check-users", selector="1.2", external=true)
	public static String show_int(VersionedLibrary this_, Integer a,Integer m, Integer b) {
		return "Better! version 1.2 " + b + " some state " + this_.someState();
	}

	@DefMethod(name="check-users", selector="0.2", external=true)
	public static String show_(VersionedLibrary this_, Integer a, Integer m, Integer b) {
		return "Too old 0.2: " + b;
	}
	
	public static void main(String[] args) {
		System.out.println(new VersionedLibrary().checkUsers(1, 3, 1));
	}
}
