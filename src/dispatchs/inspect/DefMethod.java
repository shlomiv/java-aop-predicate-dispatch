package dispatchs.inspect;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME) @Target({METHOD})
public @interface DefMethod {
	String name(); // tag
	String selector(); //when
	boolean external()     default false; 
}
