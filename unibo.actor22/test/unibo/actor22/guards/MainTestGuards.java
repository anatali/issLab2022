package unibo.actor22.guards;

import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.*;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

@Context22(name = "ctx", host = "localhost", port = "8083")
@Actor22(name = "a1",contextName="ctx",implement = TestGuards.class)
public class MainTestGuards {
 
	protected void configure() throws Exception {
		CommSystemConfig.tracing = true;
 		Qak22Context.configureTheSystem(this);
		CommUtils.delay(1000);  //Give time to start ...
		Qak22Context.showActorNames();
 	}
	
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new MainTestGuards().configure();
  		CommUtils.aboutThreads("At end - ");
	}
}
