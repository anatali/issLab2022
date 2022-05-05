package unibo.wenvUsage22.actors.robot;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.ActorLocal;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.context.EnablerContextForActors;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
 

@ActorLocal(
		name =      { "sceneObserver" }, 
		implement = { SceneObserver.class })
public class SceneObserverMain {
	//private EnablerContextForActors ctx;
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		EnablerContextForActors.create( "ctx",8030,ProtocolType.tcp);
		Qak22Context.handleLocalActorDecl(this);
		//ctx.activate();
 		CommUtils.delay(6000000);
		/*
		IApplMessage req = CommUtils.buildRequest("main", "cmd", "start", "sceneObserver");
		String answer    = Qak22Util.requestSynch(req); //blocca
		ColorsOut.outappl("SceneObserverMain answer="+answer, ColorsOut.MAGENTA);
		*/
	}
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new SceneObserverMain().doJob();
		CommUtils.aboutThreads("Before end - ");
	}

}
