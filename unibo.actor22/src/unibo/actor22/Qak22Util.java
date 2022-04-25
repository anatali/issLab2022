package unibo.actor22;

 
import it.unibo.kactor.*;
import unibo.actor22comm.RequestCallUtilObj;
import unibo.actor22comm.events.EventMsgHandler;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
 

public  class Qak22Util   {

 
    public static void emitEvent( IApplMessage msg ){
    	if( msg.isEvent() ) {
    		if( Qak22Context.getActor(EventMsgHandler.myName) == null )  new EventMsgHandler(); 
    		sendAMsg( msg, EventMsgHandler.myName);
    	}else {
    		ColorsOut.outerr("Qak22Util | emitEvent: not an event:"+ msg); 
    	}
    }
     
    //Usabile da Java: Distingue tra locale e remoto
    public static void sendAMsg( IApplMessage msg ){
    	sendAMsg( msg, msg.msgReceiver() );
    }
   
    public static void sendAMsg(IApplMessage msg, String destActorName){
		//ColorsOut.out("Qak22Util | sendAMsg " + msg  , ColorsOut.GREEN);	  
        QakActor22 dest = Qak22Context.getActor(destActorName);  
        if( dest != null ) { //attore locale
    		//ColorsOut.out("Qak22Util | sendAMsg " + msg + " to:" + dest.getName() , ColorsOut.GREEN);
    		dest.queueMsg(msg);
        }else{ //invio di un msg ad un attore non locale : cerco in proxyMap
        	//ColorsOut.out("Qak22Util | send to a non local actor  " + msg, ColorsOut.GREEN  );
    		//CommUtils.aboutThreads("Qak22Util Before doRequest - ");
    		ProxyAsClient pxy    = Qak22Context.getProxy(destActorName);
    		//ColorsOut.out("Qak22Util | sendAMsg " + msg + " using:" + pxy , ColorsOut.GREEN);
    		if( pxy == null ) {
    			ColorsOut.outerr("Qak22Util | Perhaps no setActorAsRemote for " + destActorName );
    			return;
    		}
    		//ColorsOut.outappl( "Qak22Util  | doRequest " + msg + " pxy=" + pxy, ColorsOut.WHITE_BACKGROUND  );
    		pxy.sendMsgOnConnection( msg.toString()) ;
     		//CommUtils.aboutThreads("Qak22Util After doRequest  - ");
        }
	}

    public static String requestSynch(IApplMessage msg) {
    	if( !  msg.isRequest()) {
    		ColorsOut.outerr("Qak22Util | requestSynch: no request: " + msg);
    		return null;
    	}
    	String destActorName = msg.msgReceiver();
    	QakActor22 dest      = Qak22Context.getActor(destActorName);  
        if( dest != null ) { //attore locale
        	RequestCallUtilObj waiter = new RequestCallUtilObj(msg);  
        	return waiter.getAnswer();
        }else {
        	ColorsOut.outerr("Qak22Util | requestSynch: no local dest in:"+ msg); 
    		return null;      	
        }   	
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
	public static IApplMessage buildEvent( String emitter, String msgId, String payload, String dest ) {
		try {
			return new ApplMessage(msgId, ApplMessageType.event.toString(),emitter,dest,payload,""+(msgNum++));
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
 
    
 }
