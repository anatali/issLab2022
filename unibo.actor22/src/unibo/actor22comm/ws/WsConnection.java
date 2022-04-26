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

public class WsConnection extends WebSocketListener implements Interaction2021, IObservable{
	private static HashMap<String,WsConnection> connMap=
            				new HashMap<String,WsConnection>();
	private Vector<IObserver> observers  = new Vector<IObserver>();
    private OkHttpClient okHttpClient  = new OkHttpClient();
    final MediaType JSON_MediaType     = MediaType.get("application/json; charset=utf-8");
    private WebSocket myWs;
    private boolean opened = false;
	
 
	public static Interaction2021 create(String addr ){
        //ColorsOut.outappl("WsConnection | wsconnect addr=" + addr + " " + connMap.containsKey(addr), ColorsOut.GREEN );
		if( ! connMap.containsKey(addr)){
			connMap.put(addr, new WsConnection( addr ) );
		}else {
		    ColorsOut.outappl("WsConnection | ALREADY connected to addr=" + addr, ColorsOut.YELLOW );			
		}
		return connMap.get(addr);
	}	
	
	
	public WsConnection(String addr  ) {
        //ColorsOut.outappl("WsConnection | constructor addr=" + addr + " " + connMap.containsKey(addr), ColorsOut.GREEN );
		wsconnect(addr);
 	}
	
//Since IObservable	 
	
    protected void updateObservers( String msg ){
        //ColorsOut.out("WsConnection | updateObservers " + observers.size() );
        observers.forEach( v -> v.update(null, msg) );
    }	

//Since inherits from IConnInteraction
	@Override
	public void sendALine(String msg) throws Exception {
		observers.forEach( v -> ((WsConnSysObserver)v).startMoveTime() );
        myWs.send(msg);
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
        //myWs.send(msg);
		sendALine(msg);
 	}

	@Override
	public String request(String msg) throws Exception {
		return "SORRY: not connected for HTTP";
	}

	@Override
	public void reply(String msgJson) throws Exception {
		//myWs.send(msgJson); //non faccio sendALine perchè non interessa la durata
		sendALine(msgJson);
	}

	@Override
	public String receiveMsg() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
	     boolean gracefulShutdown = myWs.close(1000, "close");
	     ColorsOut.out("WsConnection | close gracefulShutdown=" + gracefulShutdown);
	}
	
// Since extends WebSocketListener
    @Override
    public void onOpen(WebSocket webSocket, Response response  ) {
        ColorsOut.out("WsConnection | onOpen ", ColorsOut.GREEN );
        opened = true;
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason  ) {
        ColorsOut.out("WsConnection | onClosing ", ColorsOut.GREEN );
        try {
			close();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    @Override
    public void onMessage(WebSocket webSocket, String msg  ) {
        ColorsOut.out("WsConnection | onMessage " + msg, ColorsOut.GREEN );
        updateObservers( msg );
    }


	
//----------------------------------------------------------------------
    protected void wsconnect(String wsAddr){    // localhost:8091
         //ColorsOut.outappl("WsConnection | wsconnect wsAddr=" + wsAddr, ColorsOut.GREEN );
        Request request = new Request.Builder()
                .url( "ws://"+wsAddr )
                .build() ;
        myWs = okHttpClient.newWebSocket(request, this);
        //ColorsOut.out("WsConnection | wsconnect myWs=" + myWs, ColorsOut.GREEN );
    }

     
//Since IObservable    
	@Override
	public void addObserver(IObserver obs) {
        ColorsOut.out("WsConnection | addObserver " + obs, ColorsOut.GREEN );
		observers.add( obs);		
	}
	@Override
	public void deleteObserver(IObserver obs) {
		observers.remove( obs);
	}
 }
