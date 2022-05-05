package unibo.wenvUsage22.cleaner.fsm;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;


@Context22(name = "pcCtx", host = "localhost", port = "8083")
@Actor22(name = MainActorCleanerFsm.myName, contextName = "pcCtx", implement = ActorRobotCleanerFsm.class)
public class MainActorCleanerFsm {
	
	public static final String myName = "cleanerFsm";
	
	public void doJob() {
		CommSystemConfig.tracing = true;
		Qak22Context.configureTheSystem(this);
		//CommUtils.delay(2000); //Give time ...
		Qak22Context.showActorNames();
  		//Qak22Util.sendAMsg( SystemData.startSysCmd("main",myName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(60000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainActorCleanerFsm appl = new MainActorCleanerFsm( );
		appl.doJob();
		appl.terminate();
	}

}
