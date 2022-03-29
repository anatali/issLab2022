package it.unibo.radarSystem22_4.comm.utils;

import it.unibo.radarSystem22_4.comm.ApplMessage;
import it.unibo.radarSystem22_4.comm.ApplMessageType;
import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;

public class CommUtils {
 	
	public static boolean isCoap() {
		return CommSystemConfig.protcolType==ProtocolType.coap ;
	}
	public static boolean isMqtt() {
		return CommSystemConfig.protcolType==ProtocolType.mqtt ;
	}
	public static boolean isTcp() {
		return CommSystemConfig.protcolType==ProtocolType.tcp ;
	}
	
	public static String getContent( String msg ) {
		String result = "";
		try {
			ApplMessage m = new ApplMessage(msg);
			result        = m.msgContent();
		}catch( Exception e) {
			result = msg;
		}
		return result;	
	}
	
	//String MSGID, String MSGTYPE, String SENDER, String RECEIVER, String CONTENT, String SEQNUM
	private static int msgNum=0;	

	public static IApplMessage buildDispatch(String sender, String msgId, String payload, String dest) {
		try {
			return new ApplMessage(msgId, ApplMessageType.dispatch.toString(),sender,dest,payload,""+(msgNum++));
		} catch (Exception e) {
			ColorsOut.outerr("buildDispatch ERROR:"+ e.getMessage());
			return null;
		}
	}
	
	public static IApplMessage buildRequest(String sender, String msgId, String payload, String dest) {
		try {
			return new ApplMessage(msgId, ApplMessageType.request.toString(),sender,dest,payload,""+(msgNum++));
		} catch (Exception e) {
			ColorsOut.outerr("buildRequest ERROR:"+ e.getMessage());
			return null;
		}
	}
	public static IApplMessage buildReply(String sender, String msgId, String payload, String dest) {
		try {
			return new ApplMessage(msgId, ApplMessageType.reply.toString(),sender,dest,payload,""+(msgNum++));
		} catch (Exception e) {
			ColorsOut.outerr("buildRequest ERROR:"+ e.getMessage());
			return null;
		}
	}	
	public static IApplMessage prepareReply(IApplMessage requestMsg, String answer) {
		String sender  = requestMsg.msgSender();
		String receiver= requestMsg.msgReceiver();
		String reqId   = requestMsg.msgId();
		IApplMessage reply = null;
		if( requestMsg.isRequest() ) { //DEFENSIVE
			//The msgId of the reply must be the id of the request !!!!
 			reply = buildReply(receiver, reqId, answer, sender) ;
		}else { 
			ColorsOut.outerr( "Utils | prepareReply ERROR: message not a request");
		}
		return reply;
    }

}
