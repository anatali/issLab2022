package unibo.actor22.coap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;

import unibo.actor22comm.utils.ColorsOut;

public class ActorObserver {
	private CoapObserveRelation relation = null;
	private CoapClient client = null;
	
	public ActorObserver(){
		client = new CoapClient("coap://localhost:8083/actors/a1");
	}
	
	public void  observe( ) {
		relation = client.observe(
				new CoapHandler() {
					@Override public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						ColorsOut.outappl("ResourceObserver | value=" + content, ColorsOut.GREEN);
					}					
					@Override public void onError() {
						ColorsOut.outerr("OBSERVING FAILED (press enter to exit)");
					}
				});		
	}
	
	public void waitUserEnd() {
 		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
 		System.out.println("ResourceObserver | press enter to end ...");		
		try { br.readLine(); } catch (IOException e) { }		
		System.out.println("ResourceObserver | CANCELLATION");		
		relation.proactiveCancel();		
	}
	
	public static void main(String[] args) {
  		ActorObserver rco = new ActorObserver();
		rco.observe( );
		rco.waitUserEnd();
	}

}
