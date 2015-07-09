package dispatchs.inspect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import fj.F;
import fj.F1Functions;
import fj.Function;
import fj.Ord;
import fj.P;
import fj.P2;
import fj.Show;
import fj.data.List;
import fj.data.Option;
import fj.data.Set;
import fj.data.TreeMap;

public class Helper {	
	// the main resolver interface
	static public interface Resolver {
		String choose(Object... args);
	}
	
	// some global state 
	public static TreeMap<String, TreeMap<String, Method>> methodMap = TreeMap.empty(Ord.stringOrd);
		
	// a neat dsl for defining method dispatch predicates
	static public class Builder {
			List<P2<List<F<Object, Boolean>>, String>> l = List.nil();
			public Builder obj (List<Class>  cls, String s) {l = l.cons(P.p(cls.map(c->o->c.isAssignableFrom(o.getClass())),s));return this;}
			public Builder val (List<Object> obs, String s) {l = l.cons(P.p(obs.map(val->o->val.getClass().isAssignableFrom(o.getClass()) && val.equals(o)), s)); return this;}
			public Builder any1 (List<F<Object, Boolean>>fs, String s) { l=l.cons(P.p(fs,s));return this;}
			public <T> Builder  pred (List<F<T, Boolean>>fs, String s) { l=l.cons(P.p(fs.<F<Object, Boolean>>map(t->(F<Object, Boolean>)t),s));return this;}
			public F<List<Object>, String> build() {
				return (List<Object> args)->l.reverse().find(x->x._1().zip(args).forall(y->y._1().f(y._2()))).map(P2.__2()).orSome("Error");
			}
		}	
}
