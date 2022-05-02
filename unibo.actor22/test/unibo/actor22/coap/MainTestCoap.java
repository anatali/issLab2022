package unibo.actor22.coap;

import it.unibo.kactor.sysUtil;
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.*;
import unibo.actor22comm.coap.CoapApplObserver;
import unibo.actor22comm.coap.CoapConnection;
import unibo.actor22comm.coap.CoapSupport;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

@Context22(name = "ctx", host = "localhost", port = "8083")
@Actor22(name = "a1",contextName="ctx",implement = TestCoap.class)
public class MainTestCoap {
 
	
	protected void configure() throws Exception {
		CommSystemConfig.tracing = true;
		sysUtil.INSTANCE.setTrace(true);
 		Qak22Context.configureTheSystem(this);
		CommUtils.delay(1000);  //Give time to start ...
		Qak22Context.showActorNames();
		
		new ActorObserver().observe();
 
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
 		new MainTestCoap().configure();
  		CommUtils.aboutThreads("At end - ");
	}
}
