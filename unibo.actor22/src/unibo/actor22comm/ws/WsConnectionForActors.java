package unibo.actor22comm.ws;

import java.util.HashMap;
import java.util.Vector;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import unibo.actor22comm.interfaces.IObservable;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;

public class WsConnectionForActors extends WebSocketListener implements Interaction2021, IObservable{
	private static HashMap<String,WsConnectionForActors> connMap=
            				new HashMap<String,WsConnectionForActors>();
	private Vector<IObserver> observers  = new Vector<IObserver>();
    private OkHttpClient okHttpClient  = new OkHttpClient();
    final MediaType JSON_MediaType     = MediaType.get("application/json; charset=utf-8");
    private WebSocket myWs;
    private boolean opened = false;
	
 
	public static Interaction2021 create(String addr, String ownerActor  ){
        //ColorsOut.outappl("WsConnectionForActors | wsconnect addr=" + addr + " " + connMap.containsKey(addr), ColorsOut.GREEN );
		if( ! connMap.containsKey(addr)){
			connMap.put(addr, new WsConnectionForActors( addr,ownerActor ) );
		}else {
		    ColorsOut.outappl("WsConnectionForActors | ALREADY connected to addr=" + addr, ColorsOut.YELLOW );			
		}
		return connMap.get(addr);
	}	
	
/*
 * Constructor	
 */
	public WsConnectionForActors(String addr, String ownerActor  ) {
        //ColorsOut.outappl("WsConnectionForActors | constructor addr=" + addr + " " + connMap.containsKey(addr), ColorsOut.GREEN );
		wsconnect(addr);
		addObserver( new WsConnSysObserver(ownerActor) );
 	}
	
//Since IObservable	 	
    protected void updateObservers( String msg ){
        //ColorsOut.out("WsConnectionForActors | updateObservers " + observers.size() );
        observers.forEach( v -> v.update(null, msg) );
    }	

//Since inherits from IConnInteraction
	@Override
	public void sendALine(String msg) throws Exception {
         myWs.send(msg);
	}
	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
	}
	@Override
	public String receiveALine() throws Exception {
		return null;
	}
	@Override
	public void closeConnection() throws Exception {
	}
	
	
	
	
//Since inherits from Interaction2021 firstpart
	@Override
	public void forward( String msg) throws Exception {
        myWs.send(msg);
 	}

	@Override
	public String request(String msg) throws Exception {
		return "SORRY: not connected for HTTP";
	}

	@Override
	public void reply(String msgJson) throws Exception {
		myWs.send(msgJson);
	}

	@Override
	public String receiveMsg() throws Exception {
		return null;
	}

	@Override
	public void close() throws Exception {
	     boolean gracefulShutdown = myWs.close(1000, "close");
	     ColorsOut.out("WsConnectionForActors | close gracefulShutdown=" + gracefulShutdown);
	}
	
// Since extends WebSocketListener
    @Override
    public void onOpen(WebSocket webSocket, Response response  ) {
        ColorsOut.out("WsConnectionForActors | onOpen ", ColorsOut.GREEN );
        opened = true;
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason  ) {
        ColorsOut.out("WsConnectionForActors | onClosing ", ColorsOut.GREEN );
        try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void onMessage(WebSocket webSocket, String msg  ) {
        ColorsOut.out("WsConnectionForActors | onMessage " + msg, ColorsOut.GREEN );
        updateObservers( msg );
    }


	
//----------------------------------------------------------------------
    protected void wsconnect(String wsAddr){    // localhost:8091
         //ColorsOut.outappl("WsConnectionForActors | wsconnect wsAddr=" + wsAddr, ColorsOut.GREEN );
        Request request = new Request.Builder()
                .url( "ws://"+wsAddr )
                .build() ;
        myWs = okHttpClient.newWebSocket(request, this);
        //ColorsOut.out("WsConnectionForActors | wsconnect myWs=" + myWs, ColorsOut.GREEN );
    }

     
//Since IObservable    
	@Override
	public void addObserver(IObserver obs) {
        ColorsOut.out("WsConnectionForActors | addObserver " + obs, ColorsOut.GREEN );
		observers.add( obs);		
	}
	@Override
	public void deleteObserver(IObserver obs) {
		observers.remove( obs);
	}
 }
