package unibo.wenvUsage22.actors.robot.fsm;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;

@Context22(name="ctx",host="localhost", port="8024" )
@Actor22(name = ApplData.robotName, contextName = "ctx", implement = RobotBoundaryWalkerFsm.class)
public  class MainRobotMoverFsm  {
   	
	public MainRobotMoverFsm(   ) {	
 		Qak22Context.configureTheSystem(this);
		doJob( );
		
	}
	protected void doJob( )  {
		CommSystemConfig.tracing = false;
		Qak22Context.showActorNames();
		Qak22Util.sendAMsg( ApplData.w("main",ApplData.robotName) );
		CommUtils.delay(2000);		
	}
	
	
//---------------------------------------------------------
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		new MainRobotMoverFsm( ).doJob();
		CommUtils.aboutThreads("Before end - ");		
	}
}
