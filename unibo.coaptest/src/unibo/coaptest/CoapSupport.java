package unibo.coaptest;

import java.io.IOException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.elements.exception.ConnectorException;

import it.unibo.kactor.MsgUtil;
import unibo.comm22.utils.CommUtils;
import it.unibo.ctxresource1.MainCtxresource1Kt;

class MyHandler implements CoapHandler {
	public MyHandler ( ) {		 
	}
	@Override public void onLoad(CoapResponse response) {
		String content = response.getResponseText();
		MsgUtil.outblue("MyHandler | content= " + content + " code="+response.getCode());
 	}					
	@Override public void onError() {
		MsgUtil.outred("MyHandler  |  FAILED ");
	}
}

public class CoapSupport {
private CoapClient client;
private CoapObserveRelation relation = null;

	public CoapSupport(  ) { 
	}
 	
	public void connectTo(String address, String path, boolean asObserver) {
		String url = "coap://"+address + "/" + path; //"coap://localhost:5683/" + path
		client = new CoapClient( url );
		MsgUtil.outyellow("CoapSupport | STARTS url=" +  url ); //+ " client=" + client );
		client.setTimeout( 1000L );		
		if( asObserver ) observeTheResource( );		
	}
	public String readResource(   ) throws Exception {
		CoapResponse respGet = client.get( );
		if( respGet != null ) {
			ResponseCode rc = respGet.getCode();
			MsgUtil.outgreen("CoapSupport | readResource RESPONSE CODE: " + rc);
			//4.04 means: Not Found
			//Se non trova la risorsa, l'handler ha ricevuto 4.04 e non va più
			//Quindi lo rimuovo e lo rimetto
			//Ovviamente, se non faccio una read, non mi accorgo che l'handler
			if( rc == ResponseCode.NOT_FOUND) {
				removeObserve();
				observeTheResource();
			}
			return respGet.getResponseText();  //if 4.04  return ""
		}
		else return null;
	}

	public void removeObserve() {
		relation.proactiveCancel();	
	}
	public void  observeResource( CoapHandler handler  ) {
		relation = client.observe( handler );
	}
	public void  observeTheResource(    ) {
		relation = client.observe( new MyHandler() );
	}

	public boolean callTheResource( String msg ) throws ConnectorException, IOException {
		//msg must be a dispatch or a request or an event
		CoapResponse resp = client.put(msg, MediaTypeRegistry.TEXT_PLAIN);
 		if( resp != null )
			MsgUtil.outgreen("CoapSupport | updateResource RESPONSE CODE: " + resp.getCode());
 		else MsgUtil.outred("CoapSupport | updateResource FAILS"  );
		return resp != null;
	}

	public void readTheResource() {
		try {
 			String v = readResource();
			MsgUtil.outgreen("CoapSupport | readTheResource reads v=" + v);
			//updateResource("a(23)");
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void startBeforRemoteContextCreated() {
		MsgUtil.outblue("startBeforRemoteContextCreated");
		CoapSupport cs = new CoapSupport();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		cs.readTheResource();
	}
	public static void startBeforRemoteResourceCreated() {
		MsgUtil.outblue("startBeforRemoteResourceCreated");
		new Thread(){
			public void run(){
				MainCtxresource1Kt.main();
			}
		}.start();
		CommUtils.delay(1000);  //Give time the CoapServer of the Appl to start
		CoapSupport cs = new CoapSupport();
		cs.connectTo("localhost:8065","ctxresource1/resourceunknown",true);
		cs.readTheResource();
	}
	public static void startAfterRemoteResourcePerhapsCreated() {
		MsgUtil.outblue("startAfterRemoteResourceCreated");
		CoapSupport cs = new CoapSupport();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		cs.readTheResource();
		//CoapSupport legge null (dopo un pò) come per startBeforRemoteContextCreated
		//CommUtils.delay(3000);  //Se attendo si connette perdendo qualche update
		new Thread(){
			public void run(){
				MainCtxresource1Kt.main();
			}
		}.start();
		//CommUtils.delay(2000);
		/*
		CoapSupport cs = new CoapSupport();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		cs.readTheResource();*/
	}

	public static void main(String[] args) {
		CommUtils.showSystemInfo();
 		//CoapSupport.startBeforRemoteContextCreated();
		CoapSupport.startBeforRemoteResourceCreated();
		//CoapSupport.startAfterRemoteResourcePerhapsCreated();
	}
	
}

/*
 * TEST_A) Faccio partire questo main PRIMA di attivare ctxresource1.
 * TEST_B) Faccio partire questo main PRIMA che l'attivazione di resource1 sia completata,
 *         in modo che CoapServer ( attivato  da QakContext alla istanziazion ) 
 *         riceva una richiesta per una risorsa che non esiste.
 *         Di fatto 
 *    sysUtil.createActor -> QakContext.addactor( actor ) -> resourceCtx.addActorResource( actor )
 */


/*
Log4j by default looks for a file called log4j.properties or log4j.xml on the classpath
*/