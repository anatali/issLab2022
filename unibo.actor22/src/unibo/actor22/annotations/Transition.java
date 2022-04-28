package unibo.actor22.annotations;

import java.lang.annotation.*;

/*
 * Le annotazioni Java accettano solo attributi di tipo primitivo o Class
 */
 
@Target (ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Transitions.class)
public @interface Transition {
	String name() default "t0";
	String state()  ;
	String msgId()  ;
	Class guard() default GuardAlwaysTrue.class; 
}
