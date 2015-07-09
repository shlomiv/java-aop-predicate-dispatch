package dispatchs.test.externalDemo;

import dispatchs.inspect.DefMethod;
import dispatchs.inspect.DefMulti;
import dispatchs.inspect.Helper.Resolver;

public class VersionedLibrary {
	// resolve function simply grabs the value of name() method from a piece of
	// furniture
	static public class VersionResolver implements Resolver {
		@Override
		public String choose(Object... args) {
			return args[0].toString() + "." + args[1].toString();
		}
	}

	// some state of the library
	public String someState() {
		return "{state in the versioned library}";
	}

	@DefMulti(f = VersionResolver.class, name = "check-users")
	public String checkUsers(int major, int minor, int param) {
		minor--;
		if (minor < 0) {
			minor = 9;
			major--;
		}

		if (major >= 0 && minor >= 0)
			return checkUsers(major, minor, param);
		return "Not implemented!";
	}

	@DefMethod(name = "check-users", selector = "1.1")
	public String checkUserd11(int j, int m, int b) {
		return "Version 1.1 " + b;
	}

	public static void main(String[] args) {
		System.out.println(new VersionedLibrary().checkUsers(1, 5, 56));
	}
}
