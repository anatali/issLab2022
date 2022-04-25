package unibo.actor22comm.wshttp;

import java.util.HashMap;
import java.util.Vector;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.internal.http.RealResponseBody;
import unibo.actor22comm.interfaces.IObservable;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;

public class WsHttpConnection extends WebSocketListener implements Interaction2021, IObservable{
	private static HashMap<String,WsHttpConnection> connMap=
            				new HashMap<String,WsHttpConnection>();
	private Vector<IObserver> observers  = new Vector<IObserver>();
	
	private boolean connectForWs       = true;
    private OkHttpClient okHttpClient  = new OkHttpClient();
    final MediaType JSON_MediaType     = MediaType.get("application/json; charset=utf-8");

    private String httpAddr ; 
    private WebSocket myWs;
    private String wsAddr ;
    private boolean opened = false;
	
	public static Interaction2021 createForHttp(String addr ){
		if( ! connMap.containsKey(addr)){
			connMap.put(addr, new WsHttpConnection(addr,false) );
		}
		return connMap.get(addr);
	}
	public static Interaction2021 createForWs(String addr ){
		if( ! connMap.containsKey(addr)){
			connMap.put(addr, new WsHttpConnection(addr,true) );
		}
		return connMap.get(addr);
	}	
	
	
	public WsHttpConnection(String addr, boolean wsconn ) {
		connectForWs = wsconn;
        if( wsconn ) wsconnect(addr);
        else httpconnect(addr);		
	}
	
//Since IObservable	 
	
    protected void updateObservers( String msg ){
        ColorsOut.out("WsHttpConnection | updateObservers " + observers.size() );
        //observers.forEach( v -> v.send( msg ) );
        observers.forEach( v -> v.update(null, msg) );
    }	

//Since inherits from IConnInteraction
	@Override
	public void sendALine(String msg) throws Exception {
        if(connectForWs) myWs.send(msg);
        else ColorsOut.out("SORRY: not connected for ws");
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
	
	
	
	
//Since inherits from Interaction2021 firstpart
	@Override
	public void forward( String msg) throws Exception {
        if( connectForWs)  myWs.send(msg);
        else {
        	String answer = sendHttp( msg );
        	ColorsOut.out("WsHttpConnection | forward answer:" + answer + " DISCARDED");
        }
	}

	@Override
	public String request(String msg) throws Exception {
        if( ! connectForWs)  return sendHttp( msg );
        else { //invio su ws e apsetto reply da onMessage
        	return "SORRY: not connected for HTTP";
        }
	}

	@Override
	public void reply(String msgJson) throws Exception {
        if(connectForWs) myWs.send(msgJson);
        else ColorsOut.out("SORRY: not connected for ws");
	}

	@Override
	public String receiveMsg() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		if( myWs != null ){
	       boolean gracefulShutdown = myWs.close(1000, "close");
	       ColorsOut.out("WsHttpConnection | close gracefulShutdown=" + gracefulShutdown);
		}
	}
	
// Since extends WebSocketListener
    @Override
    public void onOpen(WebSocket webSocket, Response response  ) {
        ColorsOut.out("WsHttpConnection | onOpen ");
        opened = true;
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason  ) {
        ColorsOut.out("WsHttpConnection | onClosing ");
        try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void onMessage(WebSocket webSocket, String msg  ) {
        ColorsOut.out("WsHttpConnection | onMessage " + msg );
//        IApplMessage m = new ApplMessage(msg);
//        if( m.isReply() ) {
//        	//resume waiting for reply
//        }
        updateObservers( msg );
    }


	
//----------------------------------------------------------------------
    public void wsconnect(String wsAddr){    // localhost:8091
        this.wsAddr = wsAddr;
        Request request = new Request.Builder()
                .url( "ws://"+wsAddr )
                .build() ;
        myWs = okHttpClient.newWebSocket(request, this);
        ColorsOut.out("WsHttpConnection | wsconnect myWs=" + myWs);
    }

    public void httpconnect(String httpaddr){    //localhost:8090/api/move
        this.httpAddr = httpaddr; /*
        try {
        Request request = new Request.Builder()
                .url( "http://"+httpaddr )
                .build() ;
        Response response = okHttpClient.newCall(request).execute(); //a stream
        String answer     = ((RealResponseBody) response.body()).string();
        ColorsOut.out("WsHttpConnection | httpconnect answer=" + answer);
        }catch(Exception e){
             e.printStackTrace();
        }*/
    }

    public String sendHttp( String msgJson){
        try {
            ColorsOut.out("WsHttpConnection | sendHttp httpAddr=" + httpAddr);
            RequestBody body = RequestBody.create(JSON_MediaType, msgJson);
            Request request = new Request.Builder()
                    .url( "http://"+httpAddr+"/api/move" )   //TODO
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute(); //a stream
            String answer     = ((RealResponseBody) response.body()).string();
            ColorsOut.out("WsHttpConnection | response body=" + answer);
            return answer;
        }catch(Exception e){
            return "";
        }
    }
    
//Since IObservable    
	@Override
	public void addObserver(IObserver obs) {
        ColorsOut.out("WsHttpConnection | addObserver " + obs);
		observers.add( obs);		
	}
	@Override
	public void deleteObserver(IObserver obs) {
		observers.remove( obs);
	}
 }
