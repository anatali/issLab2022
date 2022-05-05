package unibo.wenvUsage22.cleaner;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;


@Context22(name = "pcCtx", host = "localhost", port = "8075")
//@Actor22(name = MainActorCleaner.myName, contextName = "pcCtx", implement = ActorRobotCleaner.class)
//@Actor22(name = MainActorCleaner.myName, contextName = "pcCtx", implement = RobotCleanerStartStop.class)
//@Actor22(name = MainActorCleaner.robotName, contextName = "pcCtx", implement = RobotCleanerInterrupt.class)
@Actor22(name = MainActorCleaner.robotName, contextName = "pcCtx", implement = RobotCleanerProject.class)
public class MainActorCleaner {
	
	public static final String robotName = "cleaner";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		//AnnotUtil.handleRepeatableActorDeclaration(this);
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
  		Qak22Util.sendAMsg( SystemData.startSysCmd("main",robotName) );

  		for( int i = 1; i<=4; i++) {
			CommUtils.delay(2000);  
			ColorsOut.outerr("STOPPING_"+i);
			Qak22Util.sendAMsg( SystemData.stopSysCmd("main",robotName) );
			CommUtils.delay(2000);  
			Qak22Util.sendAMsg( SystemData.resumeSysCmd("main",robotName) );
			ColorsOut.outerr("RESUMING_"+i);
  		}
	
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(60000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainActorCleaner appl = new MainActorCleaner( );
		appl.doJob();
		appl.terminate();
	}

}
