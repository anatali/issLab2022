package unibo.Robots.cleaner;


/*
 * 
 */

import it.unibo.kactor.IApplMessage;

import unibo.Robots.common.VRobotMoves;
import unibo.Robots.common.WsConnApplObserver;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;


public class RobotCleaner extends QakActor22FsmAnnot{
	private Interaction2021 conn;

	private int numIter     = 0;
	private int numIterOk   = 5;
	private int turnStep    = 800;   //600 => too fast
 	
	public RobotCleaner(String name) {
		super(name);
	}

	protected void init() {
 		ColorsOut.outappl(getName() + " | ws connecting ...." ,  ColorsOut.BLUE);
		conn = WsConnection.create("localhost:8091" ); 
		IObserver robotMoveObserver = new WsConnApplObserver(getName(), false);

		((WsConnection)conn).addObserver(robotMoveObserver);
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}

	@State( name = "activate", initial=true)
	@Transition( state = "start",   msgId= SystemData.startSysCmdId  )
	protected void activate( IApplMessage msg ) {
		outInfo(""+msg);
		init();
		numIter++;
		//VRobotMoves.step(getName(), conn );
	}

	@State( name = "start" )
	@Transition( state = "stoppedDown", msgId= SystemData.stopSysCmdId  )
	@Transition( state = "goingDown",   msgId="endMoveOk"  )
	@Transition( state = "endJob",      msgId="endMoveKo"  )
	protected void start( IApplMessage msg ) {
		outInfo(""+msg);
     	VRobotMoves.step(getName(), conn );
	}
	
	@State( name = "goingDown" )
	@Transition( state = "stoppedDown",   msgId= SystemData.stopSysCmdId  )
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
	@Transition( state = "stoppedUp",   msgId= SystemData.stopSysCmdId  )
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
	@Transition( state = "stoppedLast",  msgId= SystemData.stopSysCmdId  )
	@Transition( state = "lastColumn",   msgId="endMoveOk"  )
	@Transition( state = "completed",    msgId="endMoveKo"  )
	//@Transition( state = "stopped",     msgId= SystemData.stopSysCmdId  )
	protected void lastColumn( IApplMessage msg ) {
		outInfo(""+msg);
		//outInfo("numIter="+numIter);
		VRobotMoves.step(getName(), conn ); 
	}
	
	@State( name = "completed" )
	@Transition( state = "endJob",    msgId="endMoveOk"  )
	@Transition( state = "endJob",    msgId="endMoveKo"  )
	protected void completed( IApplMessage msg ) {
		outInfo(""+msg);
		numIter++;
		outInfo("numIter="+numIter);
		if( numIter == numIterOk ) ColorsOut.outappl(getName() + " | DONE " ,  ColorsOut.MAGENTA);  
		else ColorsOut.outerr(getName() + " | COMPLETED TOO FAST "  );  
		VRobotMoves.turnLeftAndHome(getName(), conn ); 
	}
	
	
	@State( name = "endJob" )
	protected void endJob( IApplMessage msg ) {
		outInfo("BYE" );
		VRobotMoves.turnLeft(getName(), conn);
   	}

  	//--------------------------------------------------

	@State( name = "stoppedDown" )
	@Transition( state = "resumedDown",  msgId= SystemData.resumeSysCmdId  )
 	protected void stoppedDown( IApplMessage msg ) {
		outInfo("" + msg);
	}

	@State( name = "resumedDown" )
	@Transition( state = "goingDown",     msgId="endMoveOk"  )
	@Transition( state = "turnGoingDown", msgId="endMoveKo"  )
	protected void resumedDown( IApplMessage msg ) {
		outInfo("" + msg);
	}

	@State( name = "stoppedUp" )
	@Transition( state = "resumedUp",  msgId= SystemData.resumeSysCmdId  )
	protected void stoppedUp( IApplMessage msg ) {
		outInfo("" + msg);
	}

	@State( name = "resumedUp" )
	@Transition( state = "goingUp",     msgId="endMoveOk"  )
	@Transition( state = "turnGoingUp", msgId="endMoveKo"  )
	protected void resumedUp( IApplMessage msg ) {
		outInfo("" + msg);
	}

	@State( name = "stoppedLast" )
	@Transition( state = "resumedLast",  msgId= SystemData.resumeSysCmdId  )
	protected void stoppedLast( IApplMessage msg ) {
		outInfo("" + msg);
	}
	@State( name = "resumedLast" )
	@Transition( state = "lastColumn",   msgId="endMoveOk"  )
	@Transition( state = "completed",    msgId="endMoveKo"  )
	protected void resumedLast( IApplMessage msg ) {
		outInfo("" + msg);
	}

}


 
