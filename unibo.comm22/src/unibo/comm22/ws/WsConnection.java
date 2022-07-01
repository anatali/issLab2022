package unibo.comm22.ws;

import java.util.HashMap;
import java.util.Vector;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import unibo.comm22.interfaces.IObservable;
import unibo.comm22.interfaces.IObserver;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;

public class WsConnection extends WebSocketListener implements Interaction2021, IObservable{
	private static HashMap<String,WsConnection> connMap=new HashMap<String,WsConnection>();
	private Vector<IObserver> observers= new Vector<IObserver>();
    private OkHttpClient okHttpClient  = new OkHttpClient();
    final MediaType JSON_MediaType     = MediaType.get("application/json; charset=utf-8");
    private WebSocket myWs;
    private boolean opened = false;
	
 
	public static WsConnection create(String addr ){
        //ColorsOut.outappl("WsConnection | wsconnect addr=" + addr + " " + connMap.containsKey(addr), ColorsOut.GREEN );
		if( ! connMap.containsKey(addr)){
			connMap.put(addr, new WsConnection( addr ) );
		}else {
		    ColorsOut.outappl("WsConnection | ALREADY connected to addr=" + addr, ColorsOut.YELLOW );			
		}
		return connMap.get(addr);
	}	
	
	
	public WsConnection(String addr  ) {
		wsconnect(addr);
 	}
	
    protected void wsconnect(String wsAddr){    // localhost:8091
        //ColorsOut.outappl("WsConnection | wsconnect wsAddr=" + wsAddr, ColorsOut.YELLOW );
        Request request = new Request.Builder()
                .url( "ws://"+wsAddr )
                .build() ;
        myWs = okHttpClient.newWebSocket(request, this);
        ColorsOut.outappl("WsConnection | wsconnected to wsAddr=" + wsAddr, ColorsOut.YELLOW );
    } 
	

//Since Interaction2021  
	@Override
	public void forward( String msg) throws Exception {
    	try {
		    ColorsOut.out("WsConnection | sendALine "  + msg, ColorsOut.CYAN);
	        myWs.send(msg);
			observers.forEach( v -> {
				if( v instanceof WsConnSysObserver) ((WsConnSysObserver)v).startMoveTime() ;
			});
    	}catch( Exception e ) {
    	    ColorsOut.outerr("WsConnection | ERROR "  + e.getMessage()  );  		
    	}
 	}

	@Override
	public String request(String msg) throws Exception {
		//We could call forward(msg) but the answer? It is is related to updateObservers
		return "SORRY: not connected for HTTP";
	}

	@Override
	public void reply(String msgJson) throws Exception {
		forward(msgJson);
	}

	@Override
	public String receiveMsg() throws Exception {
 	     ColorsOut.out("WsConnection | receiveMsg, perhaps onMessage"  , ColorsOut.MAGENTA);
		return null;
	}

	@Override
	public void close() throws Exception {
	     boolean gracefulShutdown = myWs.close(1000, "close");
	     ColorsOut.out("WsConnection | close gracefulShutdown=" + gracefulShutdown, ColorsOut.YELLOW);
	}
	
// Since extends WebSocketListener
    @Override
    public void onOpen(WebSocket webSocket, Response response  ) {
        ColorsOut.out("WsConnection | onOpen ", ColorsOut.YELLOW );
        opened = true;
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason  ) {
        ColorsOut.out("WsConnection | onClosing ", ColorsOut.YELLOW );
        try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void onMessage(WebSocket webSocket, String msg  ) {
        ColorsOut.out("WsConnection | onMessage " + msg, ColorsOut.YELLOW );
        updateObservers( msg );
    }

 
     
//Since IObservable    
	@Override
	public void addObserver(IObserver obs) {
        ColorsOut.out("WsConnection | addObserver " + obs, ColorsOut.YELLOW );
		observers.add( obs);		
	}
	@Override
	public void deleteObserver(IObserver obs) {
		observers.remove( obs);
	}
    protected void updateObservers( String msg ){
        ColorsOut.out("WsConnection | updateObservers " + observers.size(), ColorsOut.YELLOW );
        observers.forEach( v -> v.update(null, msg) );
    }	

 }
