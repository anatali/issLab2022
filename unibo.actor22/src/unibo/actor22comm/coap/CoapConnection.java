package unibo.actor22comm.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
  
 
public class CoapConnection implements Interaction2021  {
private CoapClient client;
private String url;
private String name = "CoapSprt";

//	public CoapConnection( String name, String address, String path) {  
// 		this.name = name;
// 		setCoapClient(address,path);
// 	}
	public CoapConnection( String address, String path) { //"coap://localhost:5683/" + path
 		setCoapClient(address,path);
	}
	
	protected void setCoapClient(String addressWithPort, String path) {
		//ColorsOut.outappl(name + " | setCoapClient addressWithPort=" +  addressWithPort,ColorsOut.ANSI_YELLOW  ); //+ " client=" + client );
		//url            = "coap://"+address + ":5683/"+ path;
		url            = "coap://"+addressWithPort + "/"+ path;
		client          = new CoapClient( url );
 		client.useExecutor(); //To be shutdown
		ColorsOut.outappl(name + " | STARTS client url=" +  url,ColorsOut.YELLOW_BACKGROUND ); //+ " client=" + client );
		client.setTimeout( 1000L );		 		
	}
 	
	public void removeObserve(CoapObserveRelation relation) {
		relation.proactiveCancel();	
 		ColorsOut.out(name + " | removeObserve !!!!!!!!!!!!!!!" + relation ,ColorsOut.ANSI_YELLOW  );
	}
	public CoapObserveRelation observeResource( CoapHandler handler  ) {
		CoapObserveRelation relation = client.observe( handler ); 
		ColorsOut.outappl(name + " | added " + handler + " relation=" + relation + relation,ColorsOut.ANSI_YELLOW  );
 		return relation;
	}


	
//From Interaction2021 
	@Override
	public void forward(String msg)   {
		ColorsOut.outappl(name + " | forward " + url + " msg=" + msg,ColorsOut.ANSI_YELLOW); 
		if( client != null ) {
			CoapResponse resp = client.put(msg, MediaTypeRegistry.TEXT_PLAIN); //Blocking!
 			if( resp != null ) ColorsOut.outappl(name + " | forward " + msg + " resp=" + resp.getCode(),ColorsOut.ANSI_YELLOW  );
		    else { ColorsOut.outerr(name + " | forward - resp null "   ); }  //?????
		} 
	}

	
	@Override
	public String request(String query)   {
  		ColorsOut.outappl(name + " | request query=" + query + " url="+url, ColorsOut.ANSI_YELLOW );
		String param = query.isEmpty() ? "" :  "?q="+query;
  		ColorsOut.out(name + " | param=" + param );
		client.setURI(url+param);
		CoapResponse respGet = client.get(  );
		if( respGet != null ) {
	 		ColorsOut.out(name + " | request=" + query 
	 				+" RESPONSE CODE: " + respGet.getCode() + " answer=" + respGet.getResponseText(),ColorsOut.ANSI_YELLOW);
			return respGet.getResponseText();
		}else {
	 		ColorsOut.out(name + " | request=" + query +" RESPONSE NULL ",ColorsOut.RED);
			return "0";
		}
	}
	
	//https://phoenixnap.com/kb/install-java-raspberry-pi
	
	@Override
	public void reply(String reqid) throws Exception {
		throw new Exception(name + " | reply not allowed");
	} 

	@Override
	public String receiveMsg() throws Exception {
 		throw new Exception(name + " | receiveMsg not allowed");
	}

	@Override
	public void close()  {
		ColorsOut.out(name + " | client shutdown=" + client);		
		client.shutdown();	
	}

	@Override
	public void sendALine(String msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receiveALine() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnection() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
/*
Log4j by default looks for a file called log4j.properties or log4j.xml on the classpath
*/