package it.unibo.radarSystem22_4.comm.context;

import java.util.HashMap;

 
import it.unibo.radarSystem22_4.comm.ApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IContextMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

/*
  * Il ContextMsgHandler viene invocato dal TcpContextServer (un singleton).
  * Esso gestisce messaggi della forma 'estesa':
 *   msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM ) e 
 *  e ridirige al RECEIVER il CONTENT con la connessione
 *  il RECEIVER elabora il messaggio e invia un msg di risposta sulla connessione 
 *  per i messaggi che costituiscono richieste
 *  
 *  Il ContextMsgHandler potrebbe inviare al RECEIVER  il messaggio in forma estesa
 *  ma il RECEIVER non sarebbe più quello usato nella versione precedente.
 */

public class ContextMsgHandler extends ApplMsgHandler implements IContextMsgHandler{
	protected HashMap<String,IApplMsgHandler> handlerMap = new HashMap<String,IApplMsgHandler>();

 	
	public ContextMsgHandler(String name) {
		super(name);
 	}

	@Override
	public void elaborate( IApplMessage msg, Interaction2021 conn ) {
		ColorsOut.out(name+" | elaborateeeeee ApplMessage:" + msg + " conn=" + conn, ColorsOut.BLUE);
		//msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
 		String dest  = msg.msgReceiver();
		//ColorsOut.out(name +  " | elaborate " + msg.msgContent() + " dest="+dest, ColorsOut.ANSI_PURPLE);
		ColorsOut.out(name +  " | elaborate  dest="+dest, ColorsOut.BLUE );
		IApplMsgHandler h    = handlerMap.get(dest);
		//ColorsOut.out(name +  " | elaborate  h="+h, ColorsOut.BLUE );
		ColorsOut.out(name +  " | elaborate " + msg.msgContent() + " redirect to handler="+h.getName() + " since dest="+dest, ColorsOut.BLUE );
		if( dest != null && (! msg.isReply()) ) h.elaborate(msg,conn);			
	}

 
	public void addComponent( String devname, IApplMsgHandler h) {
		ColorsOut.outappl(name +  " | added:" + h + " to:"+devname, ColorsOut.BLUE);
		handlerMap.put(devname, h);
	}
	public void removeComponent( String devname ) {
		ColorsOut.outappl(name +  " | removed:" + devname, ColorsOut.BLUE);
		handlerMap.remove( devname );
	}

	@Override
	public IApplMsgHandler getHandler(String name) {
		return handlerMap.get(name);
	}


}
