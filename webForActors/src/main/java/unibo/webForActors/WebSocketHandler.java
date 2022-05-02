package unibo.webForActors;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import org.json.JSONObject;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import unibo.Robots.common.IWsHandler;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/*
In Spring we can create a customized handler by extends abstract class
AbstractWebSocketHandler or one of it's subclass,
either TextWebSocketHandler or BinaryWebSocketHandler:
 */
public class WebSocketHandler extends AbstractWebSocketHandler implements IWsHandler {
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        ColorsOut.out("WebSocketHandler | Added the session:" + session, ColorsOut.BLUE);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        ColorsOut.out("WebSocketHandler | Removed the session:" + session, ColorsOut.BLUE);
        super.afterConnectionClosed(session, status);
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println("WebSocketHandler | New Text Message Received: " + message);
        //session.sendMessage(message);
        //Send to all the connected clients
        sendToAll(message);
    }
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        System.out.println("WebSocketHandler | New Binary Message Received " );
        //session.sendMessage(message);
        //Send to all the connected clients
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            iter.next().sendMessage(message);
        }
    }

    public void sendToAll(String message)  {
        try{
            ColorsOut.outappl("WebSocketHandler | sendToAll:" + message, ColorsOut.BLUE);
            //JSONObject jsm = new JSONObject(message);
            //IApplMessage mm = new ApplMessage(message);
            //String mstr    = mm.msgContent();//.replace("'","");
            sendToAll( new TextMessage(message)) ;
        }catch( Exception e ){
            ColorsOut.outerr("WebSocketHandler | sendToAll ERROR:"+e.getMessage());
        }

    }
    public void sendToAll(TextMessage message) throws IOException{
        while( sessions.size() == 0 ) {
            ColorsOut.outappl("WebSocketHandler | sendToAll sessions:" + sessions.size(), ColorsOut.BLUE);
            CommUtils.delay(100);
        }
        Iterator<WebSocketSession> iter = sessions.iterator();
        while( iter.hasNext() ){
            iter.next().sendMessage(message);
        }
    }

}
