package unibo.actor22.annotations;

 
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
 

//@Target( value = {ElementType.CONSTRUCTOR,ElementType.METHOD, ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
public @interface ActorLocal {
	String[] name();
	@SuppressWarnings("rawtypes")
	Class[]  implement();
}
