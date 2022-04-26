package unibo.wenvUsage22.actors.robot.fsm;
import org.json.JSONObject;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.interfaces.StateActionFun;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnSysObserver;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;
import unibo.wenvUsage22.common.VRobotMoves;

public  class RobotBoundaryWalkerFsm extends QakActor22FsmAnnot {  
	private Interaction2021 conn;
	
	public RobotBoundaryWalkerFsm(String name) {
		super(name);
 	}
	
  	 
	@Override
	protected void declareTheStates( ) {
		
		
		declareState("start", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
				//Inizializzo la connessione con WEnv
				conn = WsConnection.create("localhost:8091" );				 
				//Aggiungo un observer dei messaggi inviati da WEnv  
				((WsConnection)conn).addObserver(new WsConnSysObserver(getName()));
				addTransition( "goingAhead", ApplData.robotCmdId );
				nextState();
			}			
		});
		declareState("goingAhead", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
 				VRobotMoves.step(getName(),conn);
				addTransition( "checkResult",  "wsEvent" );
 				nextState();
			}			
		});
		declareState("checkResult", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
				String payload = msg.msgContent().replaceAll("'","");//remove ' ' 
				JSONObject json = new JSONObject(payload);
				outInfo(""+json);
				boolean b = false;
				if( json.has("endmove") )  b = json.getBoolean("endmove");
				if(b) {
					VRobotMoves.step(getName(),conn);
					addTransition( "checkResult",  "wsEvent" );
				}else {
					outInfo("collision?" ); 
				}
 			}			
		});
		
		
		declareState("turnedLeft", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);
				outInfo("BYE" );
				addTransition( "turnedLeft", ApplData.haltSysCmdId );
  			}			
		});
	}
	
	@Override
	protected void setTheInitialState( ) {
		declareAsInitialState( "start" );
	}
	
	protected void doMove(String move) {
		try {
 			conn.forward( move );
		}catch( Exception e) {
			ColorsOut.outerr("robotMOve ERROR:" + e.getMessage() );
		}
	}

 

}
