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
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.VRobotMoves;
import unibo.wenvUsage22.basicRobot.prototype0.WsConnApplObserver;

public class RobotMapperPlans extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
  	private int lastX    = 6;
 	private int lastY    = 5; 
	private String CurrentPlannedMove = "";
 	
 	public RobotMapperPlans(String name) {
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
	@Transition( state = "doMove"  )
 	protected void robotStart( IApplMessage msg ) {
		initPlanner();
		itunibo.planner.plannerUtil.planForGoal(""+lastX,""+lastY);		
	}
 

	
	//TODO: introduce an executor
	@State( name = "doMove" )
	@Transition( state = "doMove",      msgId="endMoveOk", guard="otherMoves"    )
 	@Transition( state = "hitWall",     msgId="endMoveKo"     )
 	//@Transition( state = "backToHome",  msgId="endMoveOk", guard="noOtherMoves"  )
 	@Transition( state = "backToHome",  guard="noOtherMoves"  )
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
			//itunibo.planner.plannerUtil.showMap();
			itunibo.planner.plannerUtil.showCurrentRobotState();
		}
		CommUtils.delay(500);
	}
	
	@State( name = "hitWall" )
	//@Transition( state = "backDone", msgId="endMoveOk")
	@Transition( state = "doMove")
	protected void hitWall( IApplMessage msg ) {
		outInfo(""+msg);
		ColorsOut.outerr("hitWall not expected"  );		
	} 
	

 	
	
	@State( name = "backToHome" )
	@Transition( state = "doMove",  guard="notAtHome"  )
	@Transition( state = "endWork", guard="atHome"  )
	protected void backToHome( IApplMessage msg ) {
		boolean alreadyAtHome = itunibo.planner.plannerUtil.atHome();
		outInfo("alreadyAtHome="+alreadyAtHome);
		if( ! alreadyAtHome ) itunibo.planner.plannerUtil.planForGoal("0" ,"0" );	
		//CommUtils.waitTheUser("going to home");
	}	
	
 	@State( name = "endWork" )
 	protected void endWork( IApplMessage msg ) {
 		//VRobotMoves.turnLeft(getName(), conn);
		outInfo("BYE" );	
 		System.exit(0);
 	}

//----------------------------------------------
 
 	 
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
