package unibo.wenvUsage22.actors.robot.annotated;
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.Actor;
import unibo.actor22.annotations.ActorLocal;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.actors.robot.RobotBoundaryWalkerFsm;
import unibo.wenvUsage22.common.ApplData;

@Actor(name = ApplData.robotName,      implement = RobotMoverFsmAnnotated.class)
@Actor(name = ApplData.controllerName, implement = RobotController.class)
public  class MainRobotMoverFsmAnnotated  {
	
	public void configure() {
//		new RobotMoverFsmAnnotated(ApplData.robotName);  //first, since must connect ...
//		new RobotController( ApplData.controllerName );		
		AnnotUtil.handleRepeatableActorDeclaration(this);
		Qak22Context.showActorNames();
	}
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		new MainRobotMoverFsmAnnotated().configure();
		CommUtils.delay(2000);
		CommUtils.aboutThreads("Before end - ");		
	}
}
