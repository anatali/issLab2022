package unibo.actor22;

import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import it.unibo.kactor.*;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.events.EventMsgHandler;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public abstract class QakActor22 extends ActorBasic{

protected kotlin.coroutines.Continuation<? super Unit> mycompletion;
String ctx22Name ;
	public QakActor22(@NotNull String name ) {      
		super(name, QakContext.Companion.createScope(), false, true, false, 50);
        if( Qak22Context.getActor(name) == null ) {
        	Qak22Context.addActor( this );
        	ColorsOut.outappl( getName()  + " | CREATED " , ColorsOut.CYAN);
        }
        else ColorsOut.outerr("QakActor22 | WARNING: an actor with name " + name + " already exists");	
	}

	public void setContext22Name( String ctx) {
		ctx22Name = ctx;
	}
	public String getContext22Name( ) {
		return ctx22Name ;
	}
    
//	protected void handleMsg(IApplMessage msg) {
//		if( msg.isDispatch() && msg.msgId().equals(SystemData.activateActorCmd) ) {
//			//forward( SystemData.activateActor("main", "a1") );
//			ColorsOut.out( getName()  + " | ACTIVATED " , ColorsOut.CYAN);
//		}else {
//			//ColorsOut.outerr("QakActor22 | activation message error for" + getName() );
//			elabMsg(msg);
//		}	
//	}
	protected abstract void handleMsg(IApplMessage msg);
	
	@Override
	public Object actorBody(@NotNull IApplMessage msg, @NotNull Continuation<? super Unit> $completion) {
        mycompletion = $completion;
        handleMsg(msg);
        return null;
	}
	
	//Invia la richiesta di elaborazione di un messaggio all'attore
	protected void queueMsg(IApplMessage msg) {
		sendMsgToMyself( msg );
	}
	
/*
 * INVIO MESSAGGI	 
 */
 	protected void sendMsg( IApplMessage msg ){
     	String destActorName=msg.msgReceiver();
		//ColorsOut.out("Qak22Util | sendMsg " + msg  , ColorsOut.GREEN);	  
        QakActor22 dest = Qak22Context.getActor(destActorName);  
        if( dest != null ) { //attore locale
    		//ColorsOut.outappl("QakActor22 | sendMsg " + msg + " to:" + dest.getName() , ColorsOut.YELLOW);
    		dest.queueMsg(msg);
        }else{  
        	sendMsgToRemoteActor(msg);
         }
	}
	
	protected void sendMsgToRemoteActor( IApplMessage msg ) {
		String destActorName = msg.msgReceiver();
		//Occorre un proxy al contesto
		ProxyAsClient pxy    = Qak22Context.getProxy(destActorName);
		//ColorsOut.outappl("QakActor22 | sendMsgToRemoteActor " + msg + " using:" + pxy , ColorsOut.GREEN);
		if( pxy == null ) {
			ColorsOut.outerr( getName() + " sendMsgToRemoteActor: " + msg  + " Perhaps no setActorAsRemote for " + destActorName );
			return;
		}
		pxy.sendMsgOnConnection( msg.toString() ) ;
	}
	
	
	protected void autoMsg( IApplMessage msg ){
    	//WARNING: il sender di msg potrebbe essere qualsiasi
     	if( msg.msgReceiver().equals( getName() )) sendMsg( msg );
    		//sendMessageToActor(msg,msg.msgReceiver()); 
    	else ColorsOut.outerr("QakActor22 | autoMsg wrong receiver");
    }
    
	protected  void forward( IApplMessage msg ){
		//ColorsOut.outappl( "QakActor22 forward:" + msg, ColorsOut.CYAN);
		if( msg.isDispatch() ) sendMsg( msg );  
		else ColorsOut.outerr("QakActor22 | forward requires a dispatch");
	}
 
	protected void request( IApplMessage msg ){
    	if( msg.isRequest() ) sendMsg( msg ); 
    	else ColorsOut.outerr("QakActor22 |  requires a request");
    }
 
	protected void sendReply(IApplMessage msg, IApplMessage reply) {
		QakActor22 dest = Qak22Context.getActor( msg.msgSender() );
        if(dest != null) dest.queueMsg( reply );
        else replyToRemoteCaller(msg,reply);
    }	
	
	protected void replyToRemoteCaller(IApplMessage request, IApplMessage reply) {
    	QakActor22 ar = Qak22Context.getActor(Qak22Context.actorReplyPrefix+request.msgSender()+"_"+request.msgNum());  //thanks Filoni
    	ColorsOut.out( "QakActor22 | replyToRemoteCaller using:" + ar.getName()  , ColorsOut.GREEN);
        if(ar !=null) ar.queueMsg( reply );
        else ColorsOut.outerr("QakActor22 | WARNING: reply " + request + " IMPOSSIBLE");		
	}

//-------------------------------------------------
	//EVENTS
    protected static HashMap<String,String> eventObserverMap = new HashMap<String,String>();  
	
    protected void emit(IApplMessage msg) {
    	if( msg.isEvent() ) {
    		ColorsOut.out( "QakActor22 | emit=" + msg  , ColorsOut.GREEN);
    		Qak22Util.sendAMsg( msg, EventMsgHandler.myName);
    	}   	
    }
    
//    protected void handleEvent(IApplMessage msg) {
//		try {
//		ColorsOut.outappl( "QakActor22 handleEvent:" + msg, ColorsOut.MAGENTA);
//		if( msg.isDispatch() && msg.msgId().equals(Qak22Context.registerForEvent)) {
//			eventObserverMap.put(msg.msgSender(), msg.msgContent());
//		}else if( msg.isEvent()) {
//			eventObserverMap.forEach(
//					( actorName,  evName) -> {
//						ColorsOut.outappl(actorName + " " + evName, ColorsOut.CYAN); 
//						if( evName.equals(msg.msgId()) ) {
//							sendMsg( msg  );
//						}
//			} ) ;
//		}else {
//			ColorsOut.outerr( "QakActor22 handleEvent: msg unknown");
//		}
//		}catch( Exception e) {
//			ColorsOut.outerr( "QakActor22 handleEvent ERROR:" + e.getMessage());
//		}
//	}    

	
}
