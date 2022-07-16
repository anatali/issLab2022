package unibo.actor22.interrupts;

import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.*;
import unibo.actor22comm.SystemData;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;

@Context22(name = "ctx", host = "localhost", port = "8078")
@Actor22(name = "a1",contextName="ctx",implement = TestInterrupts.class)
public class MainTestInterrupts {
 
	protected void configure() throws Exception {
		CommSystemConfig.tracing = true;
 		Qak22Context.configureTheSystem(this);
		CommUtils.delay(1000);  //Give time to start ...
		Qak22Context.showActorNames();
		Qak22Util.sendAMsg( SystemData.startSysCmd("main","a1") );
		CommUtils.delay(500);  
		Qak22Util.sendAMsg( SystemData.stopSysCmd("main","a1") );
		CommUtils.delay(500);  
		Qak22Util.sendAMsg( SystemData.stopSysCmd("main","a1") );
 	}
	
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new MainTestInterrupts().configure();
  		CommUtils.aboutThreads("At end - ");
	}
}
