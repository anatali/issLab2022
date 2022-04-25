package unibo.wenvUsage22.actors.fsm.basic;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;


@Context22(name = "pcCtx", host = "localhost", protocol=ProtocolType.tcp, port = "8083")
@Actor22(name = MainActorFsmBasic.myName, contextName = "pcCtx", implement = ActorWithFsmBasic.class)
public class MainActorFsmBasic {
	
	public static final String myName = "boundaryWalkerFsmbasic";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
 		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
  		Qak22Util.sendAMsg( SystemData.startSysCmd("main",myName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(20000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainActorFsmBasic appl = new MainActorFsmBasic( );
		appl.doJob();
		appl.terminate();
	}

}
