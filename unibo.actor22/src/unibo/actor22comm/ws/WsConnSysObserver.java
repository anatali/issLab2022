package unibo.actor22comm.ws;

import java.util.Observable;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.SystemTimer;
import unibo.actor22comm.SystemData;

public class WsConnSysObserver implements IObserver{
	protected String ownerActor = null;
	protected SystemTimer timer;
	protected String actionDuration;
		
	public WsConnSysObserver( String ownerActor ) {
		this.ownerActor = ownerActor;
		timer = new SystemTimer();
	}
	
	public void startMoveTime() {
		timer.startTime();
	}

	@Override
	public void update(Observable source, Object data) {
		timer.stopTime();	
		actionDuration = ""+timer.getDuration();
 		ColorsOut.out("WsConnSysObserver update/2 receives:" + data + " duration=" + actionDuration, ColorsOut.BLUE);
		update( data.toString() );
		
	}
	@Override
	public void update(String data) {
//		timer.stopTime();	
//		actionDuration = ""+timer.getDuration();
		ColorsOut.out("WsConnSysObserver update receives:" + data + actionDuration, ColorsOut.BLUE);
		//data sono definiti a livello applicativo. Qui non sappiamo dare semantica
		//String duration = ""+timer.getDuration();
		if( ownerActor == null ) {
			IApplMessage ev = Qak22Util.buildEvent( "wsconn", SystemData.wsEventId, data  );
			Qak22Util.emitEvent( ev );
		}else { //invio l'evento direttamente a ownerActor 
			IApplMessage evMsg = Qak22Util.buildDispatch( "wsconn", SystemData.wsEventId, data, ownerActor  );
			Qak22Util.sendAMsg(evMsg);
		}
//		JSONObject d = new JSONObject(""+data);
//		ColorsOut.outappl("ClientUsingWs update/2 collision=" + d.has("collision"), ColorsOut.MAGENTA);
	}	

}
