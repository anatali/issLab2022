package unibo.actor22comm;

import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.comm22.utils.CommUtils;

public class Timer22 extends Thread{
	private int interval = 0;
 	private String eventId = "";
	
	public Timer22(int interval, String eventId) {
		this.interval=interval;
 		this.eventId = eventId;    		
		start();
 	}
	
	public void run(){
		CommUtils.delay(interval);
		IApplMessage timeEvent = Qak22Util.buildEvent("timer", eventId, "expired" );
		ColorsOut.outappl("Timer22 emits " + eventId, ColorsOut.YELLOW);
		Qak22Util.emitEvent(timeEvent);
//		CommUtils.delay(500);
//		Qak22Context.unregisterAsEventObserver(owner, eventId);
	}

}
