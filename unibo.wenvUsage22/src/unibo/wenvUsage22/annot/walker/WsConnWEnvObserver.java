package unibo.wenvUsage22.annot.walker;

import java.util.Observable;
import org.json.JSONObject;
import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnSysObserver;
import unibo.actor22comm.SystemData;
import unibo.actor22.*;

/*
 * Trasforma dati ricevuti su WS in SystemData.endMoveOk o in SystemData.endMoveKo
 */
public class WsConnWEnvObserver extends WsConnSysObserver{  
  	
	public WsConnWEnvObserver( String ownerActor ) {
		super(   ownerActor );
	}

	@Override
	public void update(Observable source, Object data) {
		timer.stopTime();	
		actionDuration = ""+timer.getDuration();
 		ColorsOut.out("WsConnWEnvObserver update/2 receives:" + data + " duration=" + actionDuration, ColorsOut.BLUE);
		update( data.toString() );		
	}

	@Override
	public void update(String data) {		 
		ColorsOut.outappl("WsConnWEnvObserver update receives:" + data + " duration=" + actionDuration, ColorsOut.YELLOW);
		JSONObject dJson = new JSONObject(""+data);
		boolean resultMoveOk =  dJson.has("endmove") && dJson.getBoolean("endmove");
		 
		if( resultMoveOk ) {
			//if( dJson.getString("move").contains("turn")) return; //AVOID TO send info about turn
			propagateEndMoveOk( dJson.getString("move") );
		}
		else { //endmove false since interrupted or collision
			boolean resultMoveKo = dJson.has("collision")  || ! dJson.getBoolean("endmove") ;
			if( resultMoveKo ) //
				Qak22Util.sendAMsg(SystemData.endMoveKo(ownerActor,dJson.getString("collision"),actionDuration));
			else ColorsOut.outerr("WsConnWEnvObserver perhaps sonar data:" + data);
		} 
	}
	
	protected void propagateEndMoveOk( String data ) {
		IApplMessage m;
		if( ownerActor == null ) m = SystemData.endMoveOkEvent( data );
		else m = SystemData.endMoveOk(ownerActor,data);		
		Qak22Util.sendAMsg(m);
	}
	protected void propagateEndMoveKo(String data,String duration) {
		IApplMessage m;
		if( ownerActor == null ) m = SystemData.endMoveKoEvent( data );
		else m = SystemData.endMoveKo(ownerActor,data,duration);		
		Qak22Util.sendAMsg(m);
	}
	
}//WsConnWEnvObserver	
		
 


