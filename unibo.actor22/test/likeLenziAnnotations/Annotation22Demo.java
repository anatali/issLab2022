package likeLenziAnnotations;
 

import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

@Context22(name = "ctx", host = "localhost", port = "8080")
@Actor22(name = "raspi", contextName = "ctx", implement=A3Actor22.class)
public class Annotation22Demo {
    public Annotation22Demo() {
        CommSystemConfig.tracing = true;
        Qak22Context.configureTheSystem(this);
    }

    public static void main(String[] args) {
        new Annotation22Demo();
    }
}