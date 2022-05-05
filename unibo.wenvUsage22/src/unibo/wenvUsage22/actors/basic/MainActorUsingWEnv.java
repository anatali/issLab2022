package unibo.wenvUsage22.actors.basic;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.AnnotUtil;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;


@Context22(name = "pcCtx", host = "localhost", protocol=ProtocolType.tcp, port = "8083")
@Actor22(name = MainActorUsingWEnv.myName, contextName = "pcCtx", implement = ActorWithObserverUsingWEnv.class)
//@Actor22(name = MainActorUsingWEnv.myName, contextName = "pcCtx", implement = ActorAsObserverUsingWEnv.class)
public class MainActorUsingWEnv {
	
	public static final String myName = "wenvUse";
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		//AnnotUtil.handleRepeatableActorDeclaration(this);
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
  		//Qak22Util.sendAMsg( ApplData.startSysCmd("main",myName) );
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
		MainActorUsingWEnv appl = new MainActorUsingWEnv( );
		appl.doJob();
		appl.terminate();
	}

}
