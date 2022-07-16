package unibo.actor22comm.context;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22comm.ActorForReply;
import unibo.actor22comm.ApplMsgHandler;
import unibo.comm22.interfaces.IApplMsgHandler;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;



/*
 */

public class ContextMsgHandler extends ApplMsgHandler implements IApplMsgHandler{ 
 	
	public ContextMsgHandler(String name) {
		super(name);
 	}

	@Override
	public void elaborate( IApplMessage msg, Interaction2021 conn ) {
		ColorsOut.out(name+" | elaborateee" + msg + " conn=" + conn, ColorsOut.GREEN);
		//msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )		
     	if( msg.isRequest() ) elabRequest(msg,conn);
     	else  elabNonRequest(msg,conn);
	}
	
	protected void elabRequest( IApplMessage msg, Interaction2021 conn ) {
		String senderName = msg.msgSender();
		//Attivo un attore per ricevere la risposta
		String actorRepyName = Qak22Context.actorReplyPrefix+senderName+"_"+msg.msgNum(); //thanks Filoni
		if( Qak22Context.getActor(actorRepyName) == null ) { //non esiste gi�
			ColorsOut.out(name + " | CREATE ACTOR " + actorRepyName + " FOR REPLY " + msg, ColorsOut.GREEN);
			new ActorForReply(actorRepyName, this, conn);
		}		
		elabNonRequest(msg,conn);
 	}
	protected void elabNonRequest( IApplMessage msg, Interaction2021 conn ) {
		String receiver = msg.msgReceiver();
		QakActor22 a = Qak22Context.getActor(receiver);
		if( a != null ) { Qak22Util.sendAMsg( msg ); }		
		else ColorsOut.outerr(name + " | actor " + receiver + " non local (I should not be here) ");
	}
	
}
