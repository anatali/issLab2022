package javacode;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import alice.tuprolog.Struct;
import alice.tuprolog.Term;
import it.unibo.kactor.ApplMessage;

class DataHandler implements CoapHandler {
	protected String handleAsApplMessage(String content) {
		//content = msg(sonar,event,sonarOnRaspCoap,none,sonar(V),N)
		ApplMessage msg = new ApplMessage(content);
		System.out.println("ResourceObserverRadar | observes: " + msg.msgContent()  );
		Struct data     = (Struct) Term.createTerm( msg.msgContent() );
		String value    = data.getArg(0).toString();
		System.out.println("ResourceObserverRadar | observes: " + value );
		return value;
	}

	@Override public void onLoad(CoapResponse response) {
		String content  = handleAsApplMessage(response.getResponseText());	 //later ...
		System.out.println("DataHandler | observes: " + content );
		radarPojo.radarSupport.update(content, "0");
	}					
	@Override public void onError() {
		System.out.println("DataHandler |  FAILED (press enter to exit)");
	}
}

public class ResourceObserverRadar {
	private CoapSupport coapSupport = new CoapSupport("coap://localhost:5683");
	
	public ResourceObserverRadar(){
		//client = new CoapClient("coap://localhost:5683/robot/sonar");
		System.out.println("   ResourceObserverRadar | STARTS ");
		radarPojo.radarSupport.setUpRadarGui();
		coapSupport.observeResource( new DataHandler() );
	}
	
 	
	
	public static void main(String[] args) {
  		ResourceObserverRadar rco = new ResourceObserverRadar();
	}

}
