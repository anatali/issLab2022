package unibo.wenvUsage22.actors.fsm;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;
 
 
@Context22(name = "pcCtx", host = "localhost", port = "8083")
@Actor22(name = "a1", contextName = "pcCtx", implement = ActorFsm1.class)

public class MainActorFsm {
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
	}
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainActorFsm appl = new MainActorFsm( );
		appl.doJob();
		CommUtils.delay(2000);
		System.exit(0);
	}
}
