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

public class CoapInteractionTest {
private CoapClient client;
private CoapObserveRelation relation = null;

	public CoapInteractionTest(  ) { 
	}
 	
	public void connectTo(String address, String path, boolean asObserver) {
		String url = "coap://"+address + "/" + path; //"coap://localhost:5683/" + path
		client = new CoapClient( url );
		MsgUtil.outyellow("CoapInteractionTest | STARTS url=" +  url + " client=" + client);  
		client.setTimeout( 1000L );		
		if( asObserver ) observeTheResource( );		
	}
	public String readResource(   ) throws Exception {
		CoapResponse respGet = client.get( );
		if( respGet != null ) {
			ResponseCode rc = respGet.getCode();
			MsgUtil.outgreen("CoapInteractionTest | readResource RESPONSE CODE: " + rc);
			//4.04 means: Not Found
			//Se non trova la risorsa, l'handler ha ricevuto 4.04 e non va pi√π
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
			MsgUtil.outgreen("CoapInteractionTest | updateResource RESPONSE CODE: " + resp.getCode());
 		else MsgUtil.outred("CoapInteractionTest | updateResource FAILS"  );
		return resp != null;
	}

	public void readTheResource() {
		try {
 			String v = readResource();
			MsgUtil.outgreen("CoapInteractionTest | readTheResource reads v=" + v);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void startBeforRemoteContextCreated() { //TEST_A
		MsgUtil.outblue("startBeforRemoteContextCreated");
		CoapInteractionTest cs = new CoapInteractionTest();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		for( int i=1; i<=3; i++){
			cs.readTheResource();
			CommUtils.delay(1000);
		}
	}
	public static void startForResourceNotexisting() { //TEST_B
		MsgUtil.outblue("startForResourceNotexisting");
		new Thread(){
			public void run(){
				MainCtxresource1Kt.main();
			}
		}.start();
		CommUtils.delay(1000);  //Give time the CoapServer of the Appl to start
		CoapInteractionTest cs = new CoapInteractionTest();
		cs.connectTo("localhost:8065","ctxresource1/resourceunknown",true);
		cs.readTheResource();
	}
	public static void startAfterRemoteResourcePerhapsCreated() { //TEST_C
		MsgUtil.outblue("startAfterRemoteResourceCreated");
		//TEST STARTS BEFORE APP

		CoapInteractionTest cs = new CoapInteractionTest();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		cs.readTheResource(); 	//qui readTheResource reads v=null
		//CoapInteractionTest legge null (dopo un po) come per startBeforRemoteContextCreated
		//INSERENDO il delay riesce a connettersi,  perdendo qualche update
		//CommUtils.delay(3000);

		new Thread(){
			public void run(){
				MainCtxresource1Kt.main();
			}
		}.start();


		//TEST STARTS AFTER APP
		//Se non faccio TEST STARTS BEFORE APP e decommento la parte che segue 
		//sono sicuro che prima creo l'app, poi faccio il test
/*
        CommUtils.delay(2000); 		
		CoapInteractionTest cs = new CoapInteractionTest();
		cs.connectTo("localhost:8065","ctxresource1/resource1",true);
		for( int i=1; i<=3; i++){
			cs.readTheResource();
			CommUtils.delay(1000);
		}
*/
	}

	
	/*
	 * TEST_A) Faccio partire questo main PRIMA di attivare ctxresource1.
	 *         Output:
	 * 			 CoapInteractionTest | readTheResource reads v=null
	 * TEST_B) Faccio partire questo main DOPO attivato ctxresource1 ma per una risorsa che non c'Ë
	 *         Output:
	 * 	         CoapInteractionTest | readResource RESPONSE CODE: 4.04
     *           CoapInteractionTest | readTheResource reads v=
     *           MyHandler | content=  code=4.04
	 * TEST_C) Faccio partire questo main DOPO attivato ctxresource1 ma
	 *         PRIMA che l'attivazione di resource1 sia completata,
	 *         in modo che CoapServer ( attivato  da QakContext alla sua istanziazione ) 
	 *         POTREBBE ricevere una richiesta per una risorsa che ancora non esiste.
	 *         
	 *    sysUtil.createActor -> QakContext.addactor( actor ) -> resourceCtx.addActorResource( actor )
	 */

	public static void main(String[] args) {
		CommUtils.showSystemInfo();
		//CoapInteractionTest.startBeforRemoteContextCreated();  //TEST_A
		//CoapInteractionTest.startForResourceNotexisting();   //TEST_B
		CoapInteractionTest.startAfterRemoteResourcePerhapsCreated();  //TEST_C
	}
	
}



/*
Log4j by default looks for a file called log4j.properties or log4j.xml on the classpath
*/