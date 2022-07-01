package unibo.comm22.utils;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.ApplMessageType;
import it.unibo.kactor.IApplMessage;
import unibo.comm22.ProtocolType;

public class CommUtils {
	public static void showSystemInfo(){

		System.out.println(
			"COMPUTER | memory="+ Runtime.getRuntime().totalMemory() +
					" num of processors=" +  Runtime.getRuntime().availableProcessors());
		System.out.println(
			"AT START | num of threads="+ Thread.activeCount() +" currentThread=" + Thread.currentThread() );
	}

	public static String getContent( String msg ) {
		String result = "";
		try {
			IApplMessage m = new ApplMessage(msg);
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
			ColorsOut.outerr("buildReply ERROR:"+ e.getMessage());
			return null;
		}
	}
	public static IApplMessage buildEvent( String emitter, String msgId, String payload ) {
		try {
			return new ApplMessage(msgId, ApplMessageType.event.toString(),emitter,"ANY",payload,""+(msgNum++));
		} catch (Exception e) {
			ColorsOut.outerr("buildEvent ERROR:"+ e.getMessage());
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

	
	public static boolean isCoap() {
		return CommSystemConfig.protcolType==ProtocolType.coap ;
	}
	public static boolean isMqtt() {
		return CommSystemConfig.protcolType==ProtocolType.mqtt ;
	}
	public static boolean isTcp() {
		return CommSystemConfig.protcolType==ProtocolType.tcp ;
	}
	public static boolean isUdp() {
		return CommSystemConfig.protcolType==ProtocolType.udp ;
	}

	
	public static void delay(int n) {
		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void aboutThreads(String msg)   { 
		String tname    = Thread.currentThread().getName();
		String nThreads = ""+Thread.activeCount() ;
		ColorsOut.outappl( msg + " curthread=T n=N".replace("T", tname).replace("N", nThreads), ColorsOut.YELLOW );
	}
	
	
	public static void waitTheUser(String msg) {
		try {
			ColorsOut.outappl(msg, ColorsOut.ANSI_PURPLE);
			System.in.read();
		} catch (Exception e) {
				e.printStackTrace();
		}
	}
	
}
