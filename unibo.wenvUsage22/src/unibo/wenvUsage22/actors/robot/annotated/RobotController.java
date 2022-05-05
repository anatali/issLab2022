package unibo.wenvUsage22.actors.robot.annotated;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.wenvUsage22.common.ApplData;

public class RobotController extends QakActor22FsmAnnot{

	public RobotController(String name) {
		super(name);
	}

	@State( name = "s0", initial=true )
//	@Transition( state = { "s0" }, 
//	             msgId = {  ApplData.haltSysCmdId })
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg);
		Qak22Context.showActorNames();
 		Qak22Util.sendAMsg( ApplData.w(getName() , ApplData.robotName ) );
 		Qak22Util.sendAMsg( ApplData.haltSysCmd(getName(), ApplData.robotName) );
 	}
	
	
	
//	@Override
//	public void handleAsObserver(String data) {
//		outInfo( "handleAsObserver: "  + data);
//	}
 
}

 
