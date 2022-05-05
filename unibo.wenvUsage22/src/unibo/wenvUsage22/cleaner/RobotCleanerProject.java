package unibo.wenvUsage22.cleaner;


/*
 * 
 */

import it.unibo.kactor.IApplMessage;
 
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.basicRobot.prototype0.WsConnApplObserver;
import unibo.wenvUsage22.common.VRobotMoves;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;


public class RobotCleanerProject extends QakActor22FsmAnnot{
	private Interaction2021 conn;

	private int numIter     = 0;
	private int numIterOk   = 5;
	private int turnStep    = 800;   //600 => too fast
 	private boolean goingDown = true;
 
	public RobotCleanerProject(String name) {
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
	}

	@State( name = "start" )
	@Transition( state = "stopped",     msgId= SystemData.stopSysCmdId, interrupt = true   )
	@Transition( state = "coverColumn", msgId="endMoveOk"  )
	@Transition( state = "endJob",      msgId="endMoveKo"  )
	protected void start( IApplMessage msg ) {
		outInfo(""+msg);
		goingDown=true;
     	VRobotMoves.step(getName(), conn );
	}
	
	@State( name = "coverColumn" )
	@Transition( state = "stopped",     msgId= SystemData.stopSysCmdId, interrupt = true  )
	@Transition( state = "coverColumn", msgId="endMoveOk"  )
	@Transition( state = "turn",        msgId="endMoveKo"  )
	protected void coverColumn( IApplMessage msg ) {
		outInfo(""+msg);
		VRobotMoves.step(getName(), conn );
	}
	
	@State( name = "turn" ) //potrebbe collidere col wallRight
	@Transition( state = "coverColumn", msgId="endMoveOk"  )
	@Transition( state = "lastColumn",  msgId="endMoveKo"  )
 	protected void turn( IApplMessage msg ) {
		outInfo(""+msg + " goingDown=" + goingDown);
		if( goingDown ) VRobotMoves.turnLeftAndStep(getName(), turnStep, conn);
		else VRobotMoves.turnRightAndStep(getName(), turnStep, conn);
		goingDown = !goingDown;
		//VRobotMoves.step(getName(), conn );
	}


	@State( name = "lastColumn" )
	@Transition( state = "stopped",  msgId= SystemData.stopSysCmdId, interrupt = true  )
	@Transition( state = "lastColumn",   msgId="endMoveOk"  )
	@Transition( state = "completed",    msgId="endMoveKo"  )
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
		if(goingDown) ColorsOut.outerr(getName() + " | todo gotHome from opposite corner"  );  
		else VRobotMoves.turnLeftAndHome(getName(), conn ); 
	}
	
	
	@State( name = "endJob" )
	protected void endJob( IApplMessage msg ) {
		outInfo("BYE" );
		VRobotMoves.turnLeft(getName(), conn);
   	}

  	//--------------------------------------------------

	@State( name = "stopped" )
	@Transition( state = "resume",  msgId= SystemData.resumeSysCmdId  )
 	protected void stopped( IApplMessage msg ) {
		outInfo("" + msg);
	}

	@State( name = "resume" )
 	protected void resume( IApplMessage msg ) {
		outInfo("" + msg);
		resume();
	}

 
 
}


 
