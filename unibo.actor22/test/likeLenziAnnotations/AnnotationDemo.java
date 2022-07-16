package likeLenziAnnotations;
 
import unibo.actor22.annotations.Actor;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22.annotations.Context;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;

@Context(name = "raspiCtx", host = "locahost", port = "8080")
@Actor(name = "raspi", local = false, contextName = "raspiCtx")
public class AnnotationDemo {
    public AnnotationDemo() {
        CommSystemConfig.tracing = true;
        AnnotUtil.handleRepeatableActorDeclaration(this);
    }

    public static void main(String[] args) {
        new AnnotationDemo();
    }
}