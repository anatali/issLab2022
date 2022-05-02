package unibo.actor22comm.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.ApplMessageType;
import unibo.actor22comm.utils.CommUtils;


class MyHandler implements CoapHandler {
	public MyHandler ( ) {		 
	}
	@Override public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		System.out.println("MyHandler | NOTIFICATION: " + content);
 	}					
	@Override public void onError() {
		System.err.println("MyHandler  |  FAILED (press enter to exit)");
	}
}

public class CoapSupport {
private CoapClient client;
private CoapObserveRelation relation = null;

	public CoapSupport( String addressWithPort, String path) { //"coap://localhost:5683/" + path
		String url = addressWithPort + "/" + path;
		client = new CoapClient( url );
		System.out.println("CoapSupport | STARTS url=" +  url  ); //+ " client=" + client
		client.setTimeout( 1000L );		 
	}
//	public CoapSupport( String address ) {  
//		this(address, Resource.path);
//	}
	
	public String readResource(   ) {
		CoapResponse respGet = client.get( );
		if( respGet == null ) return "CoapSupport : sorry no respGet";
		System.out.println("CoapSupport | readResource RESPONSE CODE: " + respGet.getCode());		
		return respGet.getResponseText();
	}

	public void removeObserve() {
		relation.proactiveCancel();	
	}
	public void  observeResource( CoapHandler handler  ) {
		relation = client.observe( handler );
	}

	public boolean updateResource( String msg ) {
		/*
     	ApplMessage m = new ApplMessage(
	        "sonar", ApplMessageType.event.toString(),
        	"sonarRasp", "none", "sonar("+msg+")", "1" , null);
*/
		CoapResponse resp = client.put(msg, MediaTypeRegistry.TEXT_PLAIN);
//			if( resp != null ) System.out.println("CoapSupport | updateResource RESPONSE CODE: " + resp.getCode());	
//			else System.out.println("CoapSupport | updateResource FAILURE: "  );
		return resp != null;
	}
	
	
	public boolean updateResourceWithValue( String data ) {
//     	ApplMessage m = new ApplMessage(
//    	        "sonar", ApplMessageType.event.toString(),
//            	"sonarRasp", "none", "sonar("+data+")", "1" , null);
//		return updateResource(m.toString());
		return true;
	}
	
	public void test() {
		String v = readResource();
		System.out.println("CoapSupport | v=" + v);
//		updateResourceWithValue("55");
// 		v = readResource();
// 		System.out.println("CoapSupport | v=" + v);		
		CommUtils.delay(300000);
	}
	
	public static void main(String[] args) {
		//CoapSupport cs = new CoapSupport("coap://localhost:5683","robot/sonar");
		CoapSupport cs = new CoapSupport("coap://localhost:8083","actors/a1");
		cs.test();		
	}
	
}
/*
Log4j by default looks for a file called log4j.properties or log4j.xml on the classpath
*/