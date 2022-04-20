package unibo.wenvUsage22.common;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.utils.CommUtils;

public class ApplData {
	
	public static final String robotName      = "robot";
	public static final String controllerName = "robotCtrl";
	
	public static IApplMessage startEv  = CommUtils.buildEvent("main", "maincmd", "activate" );

	/*
	 * MESSAGGI in cril
	*/	
	protected static String crilCmd(String move, int time){
		String crilCmd  = "{\"robotmove\":\"" + move + "\" , \"time\": " + time + "}";
		//ColorsOut.out( "ClientNaiveUsingPost |  buildCrilCmd:" + crilCmd );
		return crilCmd;
	}
	public static final String moveForward(int duration)  { return crilCmd("moveForward", duration) ; }
	public static final String moveBackward(int duration) { return crilCmd("moveBackward", duration); }
	public static final String turnLeft(int duration)     { return crilCmd("turnLeft", duration);     }
	public static final String turnRight(int duration)    { return crilCmd("turnRight", duration);    }
	public static final String stop(int duration)         { return crilCmd("alarm", duration);        }
	public static final String stop( )                    { return crilCmd("alarm", 10);        }

	public static final String activate( )                { return crilCmd("alarm", 10);        }
	
	//Per prove
	public static final String moveCmdId = "move";
	public static final IApplMessage moveCmd(String sender, String receiver, String payload)   {
		return CommUtils.buildDispatch(sender, moveCmdId, payload, receiver );
	}
 
	//Per WEnv VirtualRobot
	public final static String robotCmdId = "move";
	
	public static final IApplMessage w(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender,robotCmdId,moveForward(300),receiver);
	}
	public static final IApplMessage a(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender,robotCmdId,turnLeft(300),receiver);
	}
	
	//Per Robot 
	public final static String wallDetectedId = "wallDetected";
	public static final IApplMessage wallDetected ( String sender, String wallName )   { 
		return CommUtils.buildDispatch(sender,wallDetectedId,wallName,robotName);
	}
}
