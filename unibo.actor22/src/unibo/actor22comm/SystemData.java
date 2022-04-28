package unibo.actor22comm;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.utils.CommUtils;

/*
 * MESSAGGI per attori come FSM
*/	
public class SystemData {

	//Usati da WsConnSysObserver
	public static final String wsEventId     = "wsEvent";
	public static final String endMoveOkId   = "endMoveOk";
	public static final String endMoveKoId   = "endMoveKo";
	
	public static final IApplMessage endMoveOkEvent(  String move )   {
		return CommUtils.buildEvent("system", endMoveOkId, move  );
	}
	public static final IApplMessage endMoveKoEvent(  String move )   {
		return CommUtils.buildEvent("system", endMoveKoId, move  );
	}
	public static final IApplMessage endMoveOk( String receiver, String move )   {
		return CommUtils.buildDispatch("system", endMoveOkId, move, receiver );
	}
	public static final IApplMessage endMoveKo( String receiver, String move, String dt )   {
		String result = "{ \"move\":" + move + ", duration:" + dt + "}";
		return CommUtils.buildDispatch("system", endMoveKoId, result, receiver );
	}

	public static final String startSysCmdId = "activate";
	public static final String demoSysId     = "demo";
	public static final String haltSysCmdId  = "halt";
	public static final String emptyMoveMsg  = "emptyMove";
	public static final String fireEventId   = "alarmFire";
	public static final String endAlarmId    = "endAlarm";
	
	//Generali, usati dalla classe-base QakActor22Fsm
	public static final IApplMessage startSysCmd(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, startSysCmdId, "do", receiver );
	}
	public static final IApplMessage haltSysCmd(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, haltSysCmdId, "do", receiver );
	}
	public static final IApplMessage emptyMoveCmd(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, emptyMoveMsg, "do", receiver );
	}
	public static final IApplMessage startSysRequest(String sender, String receiver)   {
		return CommUtils.buildRequest(sender, startSysCmdId, "do", receiver );
	}
	public static final IApplMessage sysRequestRepy(String sender, String receiver)   {
		//CommUtils.prepareReply(requestMsg, receiver)
		return CommUtils.buildReply(sender, startSysCmdId, "done", receiver );
	}
 
	public static final String activateActorCmd = "activateActor";
	public static final  IApplMessage activateActor(String sender, String receiver) {
		return Qak22Util.buildDispatch(sender, activateActorCmd, "do", receiver);
	}
 	
//Utility for demo
	public static final IApplMessage demoSysCmd(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, demoSysId, "do", receiver );
	}
	public static final IApplMessage demoSysRequest(String sender, String receiver)   {
		return CommUtils.buildRequest(sender, demoSysId+"1", "do", receiver );
	}

//Utility for alarms
	public static final IApplMessage fireEvent(    )   {
		return CommUtils.buildEvent("system", fireEventId, "now"  );
	}
	public static final IApplMessage endAlarm(    )   {
		return CommUtils.buildEvent("system", endAlarmId, "now"  );
	}
	
}
