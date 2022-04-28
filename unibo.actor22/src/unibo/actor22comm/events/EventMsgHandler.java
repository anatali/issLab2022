package unibo.actor22comm.events;

import java.util.HashMap;
import java.util.Vector;

import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.IApplMessage;
import kotlin.Pair;
import unibo.actor22.*;
import unibo.actor22comm.utils.ColorsOut;

/*
 * Gestisce gli eventi generati da emit locali o remote
 * Viene creato dalla prima chiamata a  Qak22Context.registerAsEventObserver
 */
public class EventMsgHandler extends QakActor22{
public static final String myName = "eventhandler";
protected static EventMsgHandler evmsgHandler = null;

//public static void  register(String actorName, String eventId ) {
//	getEvMsgHandler().register(actorName,eventId);
//}
//public static void  unregister(String actorName, String eventId ) {
//	getEvMsgHandler().unregister(actorName,eventId);
//}

public static EventMsgHandler getEvMsgHandler() {
	if( Qak22Context.getActor(myName) == null ) {
		new EventMsgHandler();
	}
	return  (EventMsgHandler) Qak22Context.getActor(myName);
}

protected Vector<Pair<String,String>> eventObservers = new Vector<Pair<String,String>>();
 	
	public EventMsgHandler( ) {
		super(myName);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		//ColorsOut.outappl(myName + " handles:" + msg + " ", ColorsOut.YELLOW_BACKGROUND);
		if( msg.isDispatch() && msg.msgId().equals(Qak22Context.registerForEvent)) {
//			ColorsOut.outappl(myName + " register:" + msg.msgSender() + " for "+ msg.msgContent(), ColorsOut.MAGENTA);
//			eventObservers.add(new Pair( msg.msgSender(),msg.msgContent() ) );			
//			//eventObserverMap.put(msg.msgSender(), msg.msgContent()); //REPLACED!
			register(msg.msgSender(), msg.msgContent());
		}else if( msg.isDispatch() && msg.msgId().equals(Qak22Context.unregisterForEvent)) {
			unregister(msg.msgSender(), msg.msgContent());
		}else if( msg.isEvent() ) {
 			updateTheObservers( msg );
		}else {
			ColorsOut.outerr(myName + " msg unknown");
		}
	}  

	public synchronized void  register(String actorName, String eventId ) {
		ColorsOut.outappl(myName + " register:" + actorName + " for "+ eventId, ColorsOut.MAGENTA);
		eventObservers.add(new Pair( actorName , eventId ) );			
	}
	
	public synchronized void unregister(String actorName, String eventId ) {
		ColorsOut.outappl(myName + " unregister (TODO):" + actorName + " for "+ eventId, ColorsOut.MAGENTA);
		//TODO	
	}
	protected void updateTheObservers(IApplMessage msg) {
		ColorsOut.out("updateTheObservers:" + msg + " eventObservers:" + eventObservers.size(), ColorsOut.MAGENTA); 
		
		eventObservers.forEach(
			 obs  -> {
				String actorName  = obs.getFirst();
				String evName     = obs.getSecond();
			 
				ColorsOut.out("updateTheObservers:" +actorName + " evName:" + evName, ColorsOut.MAGENTA); 
				if( evName.equals( msg.msgId()) ) {
					IApplMessage m = Qak22Util.buildEvent(msg.msgSender(), msg.msgId(), msg.msgContent(), actorName ) ;
					Qak22Util.sendAMsg( m );
					//Warning: we must declare a remote observer
				
				}
			}
		);	
	}
		
//		eventObserverMap.forEach(
//				( String actorName,  String evName) -> {
//					ColorsOut.out("updateTheObservers:" + actorName + " evName:" + evName, ColorsOut.MAGENTA); 
//					if( evName.equals( msg.msgId()) ) {
//						IApplMessage m = Qak22Util.buildEvent(msg.msgSender(), msg.msgId(), msg.msgContent(), actorName ) ;
//						Qak22Util.sendAMsg( m );
//						//Warning: we must declare a remote observer
//					}
//		} ) ;
//	}
}
