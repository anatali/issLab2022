package unibo.actor22.annotations;

import java.lang.annotation.*;


 
@Target (ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Transitions.class)
public @interface Transition {
	String name() default "t0";
	String state()  ;
	String msgId()  ;
	//String guard() default "";  //nome di una funzione che restituisce boolean
}
