package unibo.actor22.annotations;

import unibo.comm22.ProtocolType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable( Contexts.class )
public @interface  Context {
    String name();
    String host();
    String port();
    ProtocolType protocol() default ProtocolType.tcp;
}
