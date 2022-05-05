package unibo.wenvUsage22.cleaner.fsm;

/*
 * 
 */

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.VRobotMoves;


public class ActorRobotCleanerFsm extends QakActor22FsmAnnot{
	private Interaction2021 conn;
 
	private int numIter     = 0;
	private int numIterOk   = 5;
	private int turnStep    = 800;   //600 => too fast
 	
	public ActorRobotCleanerFsm(String name) {
		super(name);
	}

	protected void init() {
 		ColorsOut.outappl(getName() + " | ws connecting ...." ,  ColorsOut.BLUE);
		conn = WsConnection.create("localhost:8091" ); 
		IObserver robotMoveObserver = new WsConnApplObserver(getName());
		((WsConnection)conn).addObserver(robotMoveObserver);
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}
	
	@State( name = "start", initial=true)
	@Transition( state = "goingDown",   msgId="endMoveOk"  ) 
	@Transition( state = "endJob",      msgId="endMoveKo"  ) 
	protected void start( IApplMessage msg ) {
		outInfo(""+msg);
		init();
		numIter++;
    	VRobotMoves.step(getName(), conn );  
	}
	
	@State( name = "goingDown" )
	@Transition( state = "goingDown",     msgId="endMoveOk"  ) 
	@Transition( state = "turnGoingDown", msgId="endMoveKo"  ) 
	protected void goingDown( IApplMessage msg ) {
		outInfo(""+msg);
		VRobotMoves.step(getName(), conn );
	}
	
	@State( name = "turnGoingDown" ) //potrebbe collidere col wallRight
	@Transition( state = "goingUp",     msgId="endMoveOk"  ) 
	@Transition( state = "lastColumn",  msgId="endMoveKo"  ) 
	protected void turnGoingDown( IApplMessage msg ) {
		outInfo(""+msg);
		VRobotMoves.turnLeftAndStep(getName(), turnStep, conn);
	}

	@State( name = "goingUp" )
	@Transition( state = "goingUp",     msgId="endMoveOk"  ) 
	@Transition( state = "turnGoingUp", msgId="endMoveKo"  )  //if numIter
	protected void goingUp( IApplMessage msg ) {
		outInfo(""+msg);		
		VRobotMoves.step(getName(), conn );
	}

	@State( name = "turnGoingUp" )   //potrebbe collidere col wallRight
	@Transition( state = "goingDown",   msgId="endMoveOk"  ) 
	@Transition( state = "lastColumn",  msgId="endMoveKo"  )  //if numIter
	protected void turnGoingUp( IApplMessage msg ) {
		outInfo(""+msg);
		numIter++;
		if( numIter == numIterOk ) ColorsOut.outappl(getName() + " | DONE " ,  ColorsOut.BLUE);
		else VRobotMoves.turnRightAndStep(getName(), turnStep, conn);
	}
	
 
	
	@State( name = "lastColumn" )
	@Transition( state = "lastColumn",   msgId="endMoveOk"  ) 
	@Transition( state = "completed",    msgId="endMoveKo"  )   
	protected void lastColumn( IApplMessage msg ) {
		outInfo(""+msg);
		//outInfo("numIter="+numIter);
		VRobotMoves.step(getName(), conn ); 
	}
	
	@State( name = "completed" )
	protected void completed( IApplMessage msg ) {
		outInfo(""+msg);
		numIter++;
		outInfo("numIter="+numIter);
		if( numIter == numIterOk ) ColorsOut.outappl(getName() + " | DONE " ,  ColorsOut.MAGENTA);  
		else ColorsOut.outerr(getName() + " | TOO FAST "  );  
 	}
	
	
	@State( name = "endJob" )
	protected void endJob( IApplMessage msg ) {
		outInfo(""+msg);
		outInfo("numIter="+numIter);
		if( numIter == numIterOk ) ColorsOut.outappl(getName() + " | DONE " ,  ColorsOut.MAGENTA);  
		else ColorsOut.outerr(getName() + " | TOO FAST "  );  
  	}
	
 
	
}


//@State( name = "checkEndmove" )
//protected void checkEndmove( IApplMessage msg ) {
//	outInfo(""+msg);
//	String mc         = msg.msgContent().replace("'","");
//	JSONObject mcJson = new JSONObject(mc);
//	ColorsOut.outappl(getName() + " | mcJson:" + mcJson,  ColorsOut.GREEN);
//	boolean result =  mcJson.has("endmove") && mcJson.getBoolean("endmove");
//	if ( result) {
//        ColorsOut.out(" endStepDown " );			
//		addTransition( "endStepDown", ApplData.resumeCmd );
//		}else {
//			ColorsOut.out(" endJob " );			
//			addTransition( "endJob", ApplData.resumeCmd );
//		}
//	this.sendMsgToMyself(ApplData.resumeCmd(getName(),getName()) );
//}
