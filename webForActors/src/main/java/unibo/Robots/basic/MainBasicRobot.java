package unibo.Robots.basic;

import unibo.Robots.common.ActorObserver;
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.webForActors.WebSocketConfiguration;

@Context22(name = "ctx", host = "localhost", port = "8083")
@Actor22(name = MainBasicRobot.myName, contextName = "ctx", implement = BasicRobotActor.class)
public class MainBasicRobot {
	
	public static final String myName = "basicRobot";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();

		ActorObserver obs = new ActorObserver("8083",myName);
		obs.setWebSocketHandler(WebSocketConfiguration.wshandler);
  		//Qak22Util.sendAMsg( SystemData.startSysCmd("main",myName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(600000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
/*
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainBasicRobot appl = new MainBasicRobot( );
		appl.doJob();
		appl.terminate();
	}
*/
}
