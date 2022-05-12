package unibo.Robots.common;

import java.util.Observable;
import org.json.JSONObject;

import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MsgUtil;
import it.unibo.kactor.QakContext;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnSysObserver;
import unibo.actor22comm.SystemData;
import unibo.actor22.*;

public class WsConnApplObserver extends WsConnSysObserver{  
  	protected boolean endmoveAfterTurn = true;

	public WsConnApplObserver( String ownerActor ) {
		super(   ownerActor );
		ColorsOut.out("WsConnApplObserver CREATED - ownerActor=" + ownerActor );
	}
	public WsConnApplObserver( String ownerActor, boolean endmoveAfterTurn ) {
		super(   ownerActor );
		this.endmoveAfterTurn = endmoveAfterTurn;
		ColorsOut.out("WsConnApplObserver CREATED - ownerActor=" + ownerActor );
	}

	@Override
	public void update(Observable source, Object data) {
		timer.stopTime();	
		actionDuration = ""+timer.getDuration();
		//ColorsOut.outappl("WsConnApplObserver update/2 receives:" + data + " duration=" + actionDuration, ColorsOut.WHITE_BACKGROUND);
		update( data.toString() );		
	}

	@Override
	public void update(String data) {
		ColorsOut.outappl("WsConnApplObserver update receives:" + data + " duration=" + actionDuration, ColorsOut.BLUE);
		JSONObject dJson = new JSONObject(""+data);
		boolean resultMoveOk =  dJson.has("endmove") && dJson.getBoolean("endmove");
		//ColorsOut.out("WsConnApplObserverrrrr update receives:" + data + " duration=" + actionDuration + " resultMoveOk=" + resultMoveOk, ColorsOut.BLUE);
		 
		if( resultMoveOk ) {
			if( ! endmoveAfterTurn && dJson.getString("move").contains("turn")) return; //AVOID TO send info about turn
			else propagateEndMoveOk( dJson.getString("move") );
		}
		else { //endmove false since interrupted or collision
			boolean resultMoveKo = dJson.has("collision")  || ! dJson.getBoolean("endmove") ;
			if( resultMoveKo ) //
				//Qak22Util.sendAMsg(SystemData.endMoveKo(ownerActor,dJson.getString("collision"),actionDuration));
				propagateEndMoveKo(dJson.getString("collision"),actionDuration);
			else ColorsOut.outerr("WsConnApplObserver perhaps sonar data:" + data);
		} 
	}
	
	protected void propagateEndMoveOk( String data ) {
		IApplMessage m;
		if( ownerActor == null ) m = SystemData.endMoveOkEvent( data );
		else m = SystemData.endMoveOk(ownerActor,data);		
		//ColorsOut.outappl("WsConnApplObserver propagateEndMoveOk:" + m , ColorsOut.BLUE);
		//Qak22Util.sendAMsg(m);  //CHANGE FROM 22 to Old
		ActorBasic owner = QakContext.Companion.getActor(ownerActor);
		owner.sendMsgToMyself(m);
		 
	}
	protected void propagateEndMoveKo(String data,String duration) {
		IApplMessage m;
		if( ownerActor == null ) m = SystemData.endMoveKoEvent( data );
			else m = SystemData.endMoveKo(ownerActor,data,duration);
		//Qak22Util.sendAMsg(m);  //CHANGE FROM 22 to Old
		ColorsOut.outappl("WsConnApplObserver propagateEndMoveKo:" + m , ColorsOut.BLUE);
		ActorBasic owner = QakContext.Companion.getActor(ownerActor);
		owner.sendMsgToMyself(m);
	}
	
}//WsConnApplObserver	
		
 


