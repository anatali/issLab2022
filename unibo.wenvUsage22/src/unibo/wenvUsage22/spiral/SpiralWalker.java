package unibo.wenvUsage22.spiral;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22.annotations.TransitionGuard;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.kotlin.planner22Util;
import unibo.wenvUsage22.basicRobot.prototype0.WsConnApplObserver;
import unibo.wenvUsage22.common.VRobotMoves;

public class SpiralWalker extends QakActor22FsmAnnot{
	private Interaction2021 conn;
	private int stepCounter;
	private String CurrentPlannedMove = "";
	private int  maxNumSteps          = 4;
	
	public SpiralWalker(String name) {
		super(name);
 	}

	protected void init() {
 		ColorsOut.outappl(getName() + " | ws connecting ...." ,  ColorsOut.BLUE);
		conn = WsConnection.create("localhost:8091" ); 
		IObserver robotMoveObserver = new WsConnApplObserver(getName(), true);  //endMOve after turn
		((WsConnection)conn).addObserver(robotMoveObserver);
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}
	
	protected void initPlanner() {
		try {
			planner22Util.initAI();
	     	ColorsOut.outappl("INITIAL MAP", ColorsOut.CYAN);
	 		planner22Util.showMap();
			planner22Util.startTimer();  		
	     	//VRobotMoves.step(getName(), conn );
		} catch (Exception e) {
			ColorsOut.outerr(getName() + " in start ERROR:"+e.getMessage());
 		}		
	}

	@State( name = "activate", initial=true)
	@Transition( state = "start",   msgId= SystemData.startSysCmdId  )
	protected void activate( IApplMessage msg ) {
		outInfo(""+msg);
		init();
 	}
	
	@State( name = "start" )
	@Transition( state = "exploreStep")
	protected void start( IApplMessage msg ) {
		outInfo(""+msg);
 		initPlanner();
	}

	@State( name = "exploreStep" )
	@Transition( state = "execPlannedMoves" )
	protected void exploreStep( IApplMessage msg ) {
		outInfo(""+msg);
		stepCounter++;
		planner22Util.planForGoal(""+stepCounter,""+stepCounter);		
	}
	
	@State( name = "execPlannedMoves" )
	@Transition( state = "doMove")
	protected void execPlannedMoves( IApplMessage msg ) {
		outInfo(""+msg);
		//CurrentPlannedMove = planner22Util.getNextPlannedMove();
 	}
	
	@State( name = "doMove" )
	@Transition( state = "doMove",        msgId="endMoveOk", guard="otherMoves"    )
	@Transition( state = "hitWall",       msgId="endMoveKo"     )
	@Transition( state = "testIfAtHome",  msgId="endMoveOk", guard="noOtherMoves"  )
	protected void doMove( IApplMessage msg ) {
		outInfo(""+msg);
		CurrentPlannedMove = planner22Util.getNextPlannedMove();
		outInfo("CurrentPlannedMove ==== "+CurrentPlannedMove);
		if( CurrentPlannedMove.equals( "w" ) ){
			planner22Util.updateMap( "w", "doing w" );
			VRobotMoves.step(getName(), conn );
		}else if( CurrentPlannedMove.equals( "l" )  ){
			planner22Util.updateMap( "l", "doing l" );
			VRobotMoves.turnLeft(getName(), conn );
		}else if(  CurrentPlannedMove.equals( "r" )  ){
			planner22Util.updateMap( "r", "doing r" );
			VRobotMoves.turnRight(getName(), conn );
		}else {
			ColorsOut.outappl("doMove terminated", ColorsOut.MAGENTA);	
			VRobotMoves.turnLeft(getName(), conn );
			planner22Util.updateMap( "l", "doing l" );
		}
	}
	
	@State( name = "hitWall" )
	@Transition( state = "doMove"  )
	protected void hitWall( IApplMessage msg ) {
		outInfo(""+msg);
	}
	
	
	@State( name = "testIfAtHome" )
	@Transition( state = "backToHome",   guard="notAtHome"    )	
	@Transition( state = "continueJob", guard="atHome"    )	
	protected void testIfAtHome( IApplMessage msg ) {
		outInfo(""+msg);
	}
	
	@State( name = "backToHome" )
	@Transition( state = "execPlannedMoves"  )	
	protected void backToHome( IApplMessage msg ) {
		planner22Util.planForGoal("0","0");
	}	
	
	@State( name = "continueJob" )
	@Transition( state = "exploreStep", guard="otherSteps"  )	
	@Transition( state = "endOfJob",   guard="noOtherSteps"  )	
	protected void continueJob( IApplMessage msg ) {
		outInfo( "MAP AFTER BACK TO HOME " + stepCounter);
		planner22Util.showMap();		
		//planner22Util.saveRoomMap(mapname)		
	}	

	@State( name = "endOfJob" ) 
	protected void endOfJob( IApplMessage msg ) {
		outInfo("BYE" );
	}
//-------------------------------------------
 
	@TransitionGuard
	protected boolean otherMoves() {
		outInfo( "otherMoves  " + CurrentPlannedMove.length());
		return CurrentPlannedMove.length() > 0;
	}	
	
	@TransitionGuard
	protected boolean noOtherMoves() {
		outInfo( "noOtherMoves  " + CurrentPlannedMove.length());
		return CurrentPlannedMove.length() == 0;
	}	
	
	@TransitionGuard
	protected boolean atHome() {
		ColorsOut.outappl("atHome:"+planner22Util.atHome(), ColorsOut.GREEN);	
		return planner22Util.atHome();
	}	
	
	@TransitionGuard
	protected boolean notAtHome() {
		return ! planner22Util.atHome();
	}	

	@TransitionGuard
	protected boolean otherSteps() {
		return stepCounter < maxNumSteps;
	}	
	
	@TransitionGuard
	protected boolean noOtherSteps() {
		return stepCounter == maxNumSteps;
	}	
}
