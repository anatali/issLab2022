package unibo.actor22comm.coap;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;


public class CoapApplObserver implements CoapHandler{
	
	public CoapApplObserver( String hostAddr, String resourceUri ) {
		ColorsOut.outappl("CoapApplObserver | resourceUri= " + resourceUri, ColorsOut.YELLOW_BACKGROUND);

	}
	

	@Override
	public void onLoad(CoapResponse response) {
		ColorsOut.outappl("CoapApplObserver | value=" +  response.getResponseText(), ColorsOut.YELLOW_BACKGROUND);
//		if( response.getResponseText().equals("BYE")) {
//		try {
//			ColorsOut.outappl("CoapApplObserver | finalize=" +  response.getResponseText(), ColorsOut.YELLOW_BACKGROUND	);
//			this.onError();
//			//this.finalize();
//		} catch (Throwable e) {
// 			e.printStackTrace();
//		}}else {
//			//applHandler.elaborate(response.getResponseText(), null);
//			IApplMessage msg = CommUtils.buildDispatch("coap", "id", response.getResponseText(), "any" );
//			//if( applHandler != null ) applHandler.elaborate(msg, null);
//		}
	}
	@Override
	public void onError() {
		//If a request timeouts or the server rejects it
		ColorsOut.outerr("CoapApplObserver | ERROR " );	
		 
	}
	
	
	
 
}
