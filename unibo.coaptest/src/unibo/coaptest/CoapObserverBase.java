package unibo.coaptest;

 
import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;


import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;

public abstract class CoapObserverBase implements CoapHandler{
	private CoapObserveRelation relation = null;
	private CoapClient client = null;
	
	public CoapObserverBase(String uri){
		client = new CoapClient("coap://"+uri); //"coap://localhost:8022/ctxactors/a1"
		CoapObserverBase myself = this;
		new Thread() {
			public void run() {				
				client.observe(myself);
			}
		}.start();
		try {
			ColorsOut.outappl("CoapObserverBase | STARTED " + uri, ColorsOut.GREEN);
 			System.in.read(); //to avoid exit
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	protected abstract void outMsg( String content );
	
	@Override public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		ColorsOut.outappl("CoapObserverBase | value=" + content, ColorsOut.GREEN);
		outMsg(content);
	}					
	@Override public void onError() {
		ColorsOut.outerr("CoapObserverBase | FAILED (press enter to exit)");
	}

//	public void  observe( ) {
//		relation = client.observe(
//				new CoapHandler() {
//					@Override public void onLoad(CoapResponse response) {
//						String content = response.getResponseText();
//						ColorsOut.outappl("ActorObserver | value=" + content, ColorsOut.GREEN);
//					}					
//					@Override public void onError() {
//						ColorsOut.outerr("OBSERVING FAILED (press enter to exit)");
//					}
// 
//				});		
//	}
 }
