package unibo.actor22.distrib.annot;

import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.ActorLocal;
import unibo.actor22.annotations.ActorRemote;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.EventObserver;
import unibo.comm22.ProtocolType;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;
 
 
 
/*
 * Questo sistema ipotizza che led e sonar siano attori 
 * con cui interagire  a scambio di messaggi
 * ATTIVARE LedActorOnRasp
 */
@ActorLocal(name =     {"controller" }, 
           implement = {unibo.actor22.common.ControllerForLedActor.class })
@ActorRemote(name =   {"led","sonar"}, 
             host=    {ApplData.raspAddr,ApplData.raspAddr}, 
             port=    { ""+ApplData.ctxPort, ""+ApplData.ctxPort}, 
             protocol={ "TCP" , "TCP" })
public class UsingActorsWithAnnotOnPc {
	
 
	public void doJob() {
		ColorsOut.outappl("UsingActorsWithAnnotOnPc | Start", ColorsOut.BLUE);
		configure();
		CommUtils.aboutThreads("Before execute - ");
		//CommUtils.waitTheUser();
		execute();
		//terminate();
	}
	
	protected void configure() {
		DomainSystemConfig.tracing      = false;			
 		CommSystemConfig.protcolType    = ProtocolType.tcp;
		CommSystemConfig.tracing        = false;
		ProtocolType protocol 		    = CommSystemConfig.protcolType;
		
//		new EventObserver(ApplData.observerName);
//		Qak22Context.registerAsEventObserver(ApplData.observerName, ApplData.evEndWork);
		
		Qak22Context.handleLocalActorDecl(this);
 		Qak22Context.handleRemoteActorDecl(this);
 	}
	
	protected void execute() {
		ColorsOut.outappl("UsingActorsWithAnnotOnPc | execute", ColorsOut.MAGENTA);
//		for( int i=1; i<=2; i++) {
//			Qak22Util.sendAMsg( ApplData.turnOnLed  );
//	 	    CommUtils.delay(500);
//	 	    Qak22Util.sendAMsg( ApplData.turnOffLed  );
//	 	    CommUtils.delay(500);
//		}
//		
//
		Qak22Util.sendAMsg( ApplData.activateCrtl );
	} 
	
	public void terminate() {
		System.exit(0);
	}
	
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new UsingActorsWithAnnotOnPc().doJob();
		CommUtils.aboutThreads("Before end - ");
	}

}

/*
 * Threads:
 */
