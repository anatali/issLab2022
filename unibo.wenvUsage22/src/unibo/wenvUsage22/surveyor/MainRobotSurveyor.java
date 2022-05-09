package unibo.wenvUsage22.surveyor;

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
@Actor22(name = MainRobotSurveyor.robotName, contextName = "pcCtx", implement = RobotSurveyor.class)
public class MainRobotSurveyor {
	
	public static final String robotName = "mapper";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
  		Qak22Util.sendAMsg( SystemData.startSysCmd("main",robotName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(60000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainRobotSurveyor appl = new MainRobotSurveyor( );
		appl.doJob();
		appl.terminate();
	}

}
