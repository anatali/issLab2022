package unibo.actor22.annotations;
 
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Actors22.class)
public @interface Actor22 {
	String name();
 	Class implement() default void.class;
	String contextName() default "";
}