package unibo.actor22.coap;

import it.unibo.kactor.sysUtil;
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.*;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;

@Context22(name = "ctx", host = "localhost", port = "8022")
@Actor22(name = "a1",contextName="ctx",implement = ActorCoapDemo.class)
public class MainDemoCoap {
 
	
	protected void configure() throws Exception {
		CommSystemConfig.tracing = false;
		//sysUtil.INSTANCE.setTrace(true);
 		Qak22Context.configureTheSystem(this);
		CommUtils.delay(1000);  //Give time to start ...
		Qak22Context.showActorNames();
		
		new ActorObserver().observe();
		//CommUtils.delay(3000);  //Give time to observe ...
 
/*		
		
		new Thread() {
		public void run() {
			CoapSupport cs       = new CoapSupport("coap://localhost:8083","actor/a1");
			CoapApplObserver obs = new CoapApplObserver("localhost:8083", "actor/a1");
			cs.observeResource( obs );
			//CommUtils.delay(1000);
			ColorsOut.outappl( "main connect .... "  , ColorsOut.YELLOW_BACKGROUND );
			Interaction2021 coapConn = new CoapConnection("localhost:8083", "actors/a1");
			ColorsOut.outappl( "main coapConn=" + coapConn, ColorsOut.YELLOW_BACKGROUND );
			try {
				coapConn.forward( "hello") ;
				CommUtils.delay(1000);
				String answer = coapConn.request( "");
				ColorsOut.outappl( "main answer=" + answer, ColorsOut.YELLOW_BACKGROUND );
				
			} catch (Exception e) {
				ColorsOut.outerr(""+e.getMessage() );
				//e.printStackTrace();
			} //
	}
}.start();	
	*/
		
 	}
	
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new MainDemoCoap().configure();
  		CommUtils.aboutThreads("At end - ");
	}
}
