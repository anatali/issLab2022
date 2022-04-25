package unibo.actor22.annotations;

import unibo.actor22comm.ProtocolType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable( Contexts22.class )
public @interface  Context22 {
    String name();
    String host();
    String port();
    ProtocolType protocol() default ProtocolType.tcp;
}
