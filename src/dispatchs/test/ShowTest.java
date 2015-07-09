package dispatchs.test;

import java.util.ArrayList;
import java.util.List;

import dispatchs.inspect.DefMethod;
import dispatchs.test.externalDemo.Show;


public class ShowTest {

	@DefMethod(name="show", selector="java.util.ArrayList", external=true)
	public static String show_int(Show this_, List<Integer> f) {
		return "list: " + f.toString();
	}

	public static void main(String[] args) {
		List<Integer> l = new ArrayList<Integer>();
		l.add(3);
		l.add(5);
		System.out.println(new Show().show(l));
	}
}
