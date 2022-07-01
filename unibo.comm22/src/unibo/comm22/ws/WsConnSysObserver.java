package unibo.comm22.ws;

import java.util.Observable;
import unibo.comm22.interfaces.IObserver;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.SystemTimer;
 

public abstract class WsConnSysObserver implements IObserver{
	protected String ownerActor = null;
	protected SystemTimer timer;
	protected String actionDuration;
		
	public WsConnSysObserver( String ownerActor ) {
		this.ownerActor = ownerActor;
		timer = new SystemTimer();
	}
	
	public void startMoveTime() { //see sendALine di WsConnection
		timer.startTime();
	}

	@Override
	public void update(Observable source, Object data) {
		timer.stopTime();	
		actionDuration = ""+timer.getDuration();
 		ColorsOut.out("WsConnSysObserver update/2 receives:" + data + " duration=" + actionDuration, ColorsOut.CYAN);
		update( data.toString() );
	}
	@Override
	public abstract void update(String data);

}
