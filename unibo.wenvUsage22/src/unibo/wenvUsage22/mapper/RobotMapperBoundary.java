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
public class RobotMapperBoundary extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
 	private int NumStep   = 0;
	
 	public RobotMapperBoundary(String name) {
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
 	 
  		
 
}
