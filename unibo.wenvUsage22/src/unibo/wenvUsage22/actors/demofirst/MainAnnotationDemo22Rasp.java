package unibo.wenvUsage22.actors.demofirst;
 
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.*;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

/*
 * TestPlan: l'attore a1 deve ricevere il messaggio msg(demo,dispatch,a2,a3Pc,do,1)
 */

@Context22(name = "pcCtx", host = "127.0.0.1", port = "8080")
@Context22(name = "pcCtx1", host = "127.0.0.1", port = "8081")
@Context22(name = "raspCtx",   host = "localhost",    port = "8082")
@Actor22(name = "a1",      contextName = "pcCtx" )
@Actor22(name = "a3Pc",    contextName = "pcCtx1"  )
@Actor22(name = "a2",      contextName = "raspCtx", implement=A2Actor22OnRasp.class )
@Actor22(name = "a3Rasp",  contextName = "raspCtx", implement=A3Actor22.class )

public class MainAnnotationDemo22Rasp {
    
	public MainAnnotationDemo22Rasp() {
        CommSystemConfig.tracing = false;
        Qak22Context.configureTheSystem(this);
        Qak22Context.showActorNames();
    }
    

    public static void main(String[] args) {
        new MainAnnotationDemo22Rasp();
        CommUtils.delay(1000);
        
    }
}