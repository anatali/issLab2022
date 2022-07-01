package unibo.comm22.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import unibo.comm22.utils.ColorsOut;

public class CoapObserver {
	private CoapObserveRelation relation = null;
	private CoapClient client = null;
	
	public CoapObserver(String uri){
		client = new CoapClient("coap://"+uri); //"coap://localhost:8022/actors/a1"
	}
	
	public void  observe( ) {
		relation = client.observe(
				new CoapHandler() {
					@Override public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						ColorsOut.outappl("ActorObserver | value=" + content, ColorsOut.GREEN);
					}					
					@Override public void onError() {
						ColorsOut.outerr("OBSERVING FAILED (press enter to exit)");
					}
				});		
	}
 }
