package unibo.wenvUsage22.mapper;

import org.json.JSONObject;
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
import unibo.wenvUsage22.common.VRobotMoves;
import unibo.wenvUsage22.basicRobot.prototype0.WsConnApplObserver;


/*
 * Il robot si muove lungo il boundary facendo step pari alla sua lunghezza.
 * Ad ogni step fatto con successo, aggiorna la mappa con "w"
 * Ad una collisione ...
 */
public class RobotMapper extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
  	private int lastX    = 6;
 	private int lastY    = 5; 
	private String CurrentPlannedMove = "";
	private int NumStep   = 0;
	
 	public RobotMapper(String name) {
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
			itunibo.planner.plannerUtil.initAI();
	     	ColorsOut.outappl("INITIAL MAP", ColorsOut.CYAN);
	 		itunibo.planner.plannerUtil.showMap();
			itunibo.planner.plannerUtil.startTimer();  		
 		} catch (Exception e) {
			ColorsOut.outerr(getName() + " in start ERROR:"+e.getMessage());
 		}		
	} 	 
	
	@State( name = "activate", initial=true)
	@Transition( state = "robotStart",   msgId= SystemData.startSysCmdId  )
	protected void activate( IApplMessage msg ) {
		outInfo(""+msg);
		init();
 	}
	
	@State( name = "robotStart" )
	@Transition( state = "detectBoundary"  )
 	protected void robotStart( IApplMessage msg ) {
		initPlanner();
		//itunibo.planner.plannerUtil.planForGoal(""+lastX,""+lastY);		
		itunibo.planner.plannerUtil.showCurrentRobotState();
	}
 
	@State( name = "detectBoundary" )
	@Transition( state = "doAheadMove", guard="roundNotCompleted"    )
	@Transition( state = "endWork",     guard="roundCompleted"    )
	protected void detectBoundary( IApplMessage msg ) {
 		outInfo(""+  msg);
 		NumStep++ ;
 	}
	
	@State( name = "doAheadMove" )
	@Transition( state = "stepDone",   msgId="endMoveOk"     )
	@Transition( state = "stepFailed", msgId="endMoveKo"     )
  	protected void doAheadMove( IApplMessage msg ) {
		outInfo(""+msg);
		VRobotMoves.step(getName(), conn );
	}
	@State( name = "stepDone" )
	@Transition( state = "doAheadMove"  )
  	protected void stepDone( IApplMessage msg ) {
		outInfo(""+msg);
		itunibo.planner.plannerUtil.updateMap(  "w", "stepDone" );
	}
	@State( name = "stepFailed" )
	@Transition( state = "backPosDone", msgId="endMoveOk"   )
  	protected void stepFailed( IApplMessage msg ) {
		outInfo(""+msg);
		ColorsOut.outappl("FOUND A WALL" , ColorsOut.MAGENTA);	
		itunibo.planner.plannerUtil.showMap();
		JSONObject json = new JSONObject(msg.msgContent().replace("'", ""));
		int duration = json.getInt("duration") ;
		ColorsOut.outappl("duration="+duration, ColorsOut.YELLOW);		
 		VRobotMoves.moveBackward( getName(), conn, duration);
	}
	
	@State( name = "backPosDone" )
	@Transition( state = "detectBoundary",  msgId="endMoveOk"  )
	protected void backPosDone( IApplMessage msg ) {
		outInfo(""+msg);
		itunibo.planner.plannerUtil.updateMap(  "l","???" );
		VRobotMoves.turnLeft(getName(), conn);
	}	
	
	@State( name = "execPlannedMoves" )
	@Transition( state = "doMove")
	protected void execPlannedMoves( IApplMessage msg ) {
		CurrentPlannedMove = itunibo.planner.plannerUtil.getNextPlannedMove();
		outInfo(""+ CurrentPlannedMove + " " + msg);
 	}
	
	//TODO: introduce an executor
	@State( name = "doMove" )
	@Transition( state = "doMove",      msgId="endMoveOk", guard="otherMoves"    )
 	@Transition( state = "hitWall",     msgId="endMoveKo"     )
 	@Transition( state = "backToHome",  msgId="endMoveOk", guard="noOtherMoves"  )
	protected void doMove( IApplMessage msg ) {
		outInfo(""+msg);
		CurrentPlannedMove = itunibo.planner.plannerUtil.getNextPlannedMove();
		outInfo("CurrentPlannedMove ==== "+CurrentPlannedMove);
		if( CurrentPlannedMove.equals( "w" ) ){
			itunibo.planner.plannerUtil.updateMap( "w", "doing w" );
			VRobotMoves.step(getName(), conn );
		}else if( CurrentPlannedMove.equals( "l" )  ){
			itunibo.planner.plannerUtil.updateMap( "l", "doing l" );
			VRobotMoves.turnLeft(getName(), conn );
		}else if(  CurrentPlannedMove.equals( "r" )  ){
			itunibo.planner.plannerUtil.updateMap( "r", "doing r" );
			VRobotMoves.turnRight(getName(), conn );
		}else {
			ColorsOut.outappl("doMove terminated", ColorsOut.MAGENTA);	
			VRobotMoves.turnLeft(getName(), conn );  //per completare
			itunibo.planner.plannerUtil.updateMap( "l", "doing l" );
		}
	}
	
	@State( name = "hitWall" )
	@Transition( state = "backDone", msgId="endMoveOk")
	protected void hitWall( IApplMessage msg ) {
		outInfo(""+msg);
		JSONObject json = new JSONObject(msg.msgContent().replace("'", ""));
		int duration = json.getInt("duration");
		ColorsOut.outappl("duration="+duration, ColorsOut.MAGENTA);		
		itunibo.planner.plannerUtil.showMap();
		VRobotMoves.moveBackward( getName(), conn, duration);
	} 
	
	@State( name = "backDone" )
	@Transition( state = "doMove", msgId="endMoveOk"  )
	protected void backDone( IApplMessage msg ) {
		outInfo(""+msg);
		//VRobotMoves.turnLeft(getName(), conn);
	}
 	
	
	@State( name = "backToHome" )
	@Transition( state = "execPlannedMoves", guard="notAtHome"  )
	protected void backToHome( IApplMessage msg ) {
		boolean alreadyAtHome = itunibo.planner.plannerUtil.atHome();
		outInfo("alreadyAtHome="+alreadyAtHome);
		if( ! alreadyAtHome ) itunibo.planner.plannerUtil.planForGoal("0" ,"0" );	
	}	
	
 	@State( name = "endWork" )
 	protected void endWork( IApplMessage msg ) {
 		itunibo.planner.plannerUtil.showMap();
		outInfo("BYE" );	
 		System.exit(0);
 	}

//----------------------------------------------
 
		@TransitionGuard
		protected boolean roundNotCompleted() {
			outInfo( "roundNotCompleted  " + NumStep);
			return NumStep < 5;
		}	
		@TransitionGuard
		protected boolean roundCompleted() {
			outInfo( "roundCompleted  " + NumStep);
			return NumStep == 5;
		}	
 	 
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
 			ColorsOut.outappl("atHome:"+itunibo.planner.plannerUtil.atHome(), ColorsOut.GREEN);	
 			return itunibo.planner.plannerUtil.atHome();
 		}	
 		
 		@TransitionGuard
 		protected boolean notAtHome() {
 			return ! itunibo.planner.plannerUtil.atHome();
 		}	

}
