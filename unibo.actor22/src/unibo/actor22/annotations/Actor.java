package unibo.actor22.annotations;
 
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Actors.class)
public @interface Actor {
	String name();
	boolean local() default true;
	@SuppressWarnings("rawtypes")
	Class implement() default void.class;
	String contextName() default "";
}