package unibo.wenvUsage22.spiral;

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
@Actor22(name = MainRobotExplorer.robotName, contextName = "pcCtx", implement = SpiralWalker.class)
public class MainRobotExplorer {
	
	public static final String robotName = "explorer";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		//AnnotUtil.handleRepeatableActorDeclaration(this);
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
		MainRobotExplorer appl = new MainRobotExplorer( );
		appl.doJob();
		appl.terminate();
	}

}
