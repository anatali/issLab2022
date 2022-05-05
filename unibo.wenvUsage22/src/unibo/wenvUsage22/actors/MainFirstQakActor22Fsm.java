package unibo.wenvUsage22.actors;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.*;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;

@Context22(name="ctx", host="localhost", port="8022")
@Actor22(name="a1", contextName="ctx", implement=FirstQakActor22Fsm.class)
public  class MainFirstQakActor22Fsm  {
 
 	
	public void doJob( String name ) {
		CommSystemConfig.tracing = false;
		Qak22Context.configureTheSystem(this);
		
		Qak22Context.showActorNames();
 		//Qak22Util.sendAMsg( ApplData.startSysCmd("main", "a1") );
		Qak22Util.sendAMsg( ApplData.moveCmd("main", name, "w" ) );
		//Qak22Util.sendAMsg( haltCmd );
		Qak22Util.sendAMsg( ApplData.moveCmd("main", name, "w" ) );
		Qak22Util.sendAMsg( ApplData.haltSysCmd("main", name) );
		//Qak22Util.sendAMsg( startCmd );
		CommUtils.delay(2000);
	}
	
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new MainFirstQakActor22Fsm(  ).doJob( "a1" );
		CommUtils.aboutThreads("Before end - ");
		
	}
	
}
