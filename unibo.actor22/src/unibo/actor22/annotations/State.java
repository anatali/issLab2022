package unibo.actor22.annotations;

import java.lang.annotation.*;


@Target (ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface State {
	String name() default "s0";
	boolean initial() default  false;
}
