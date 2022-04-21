package unibo.actor22comm.ws;

import java.util.Observable;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.SystemData;

public class WsConnSysObserver implements IObserver{
	private String ownerActor = null;
	
	public WsConnSysObserver( String ownerActor ) {
		this.ownerActor = ownerActor;
	}
	@Override
	public void update(Observable source, Object data) {
//		ColorsOut.out("WsConnSysObserver update/2 receives:" + data);
		update( data.toString() );
		
	}
	@Override
	public void update(String data) {
		ColorsOut.out("WsConnSysObserver update receives:" + data, ColorsOut.BLUE);
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
