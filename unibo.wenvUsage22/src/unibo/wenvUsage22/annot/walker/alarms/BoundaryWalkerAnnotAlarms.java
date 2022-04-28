package unibo.wenvUsage22.annot.walker.alarms;

import org.json.JSONObject;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.annot.walker.WsConnWEnvObserver;
import unibo.wenvUsage22.common.ApplData;
import unibo.wenvUsage22.common.VRobotMoves;

public class BoundaryWalkerAnnotAlarms extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
 	private int ncorner  = 0;
 	boolean wotkInterrupted = false;
 	String currentMove = "none";
 	
	public BoundaryWalkerAnnotAlarms(String name) {
		super(name);
		//EventHandler come attore  protrebbe avere riterdi nella registrazione
		Qak22Context.registerAsEventObserver(ApplData.robotName,SystemData.fireEventId);
		Qak22Context.registerAsEventObserver(ApplData.robotName,SystemData.endAlarmId);
 	}
	
	@State( name = "robotStart", initial=true)
	@Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId )
	@Transition( state = "alarm" ,        msgId = SystemData.fireEventId )
	@Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
	protected void robotStart( IApplMessage msg ) {
		outInfo(""+msg + " connecting (blocking all the actors ) ... ");	
 		conn = WsConnection.create("localhost:8091" ); 	 
 		outInfo("connected "+conn);	
   		((WsConnection)conn).addObserver( new WsConnWEnvObserver(getName()) );
   		new Sentinel().start();
  		VRobotMoves.step(getName(),conn);
  		currentMove="step";
	}
	
 	@State( name = "robotMoving" )
	@Transition( state = "alarm" ,        msgId = SystemData.fireEventId )
 	@Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId)
  	@Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )  //potrebbe non incrementare ncorner
	protected void robotMoving( IApplMessage msg ) {
		outInfo("ncorner="+ ncorner + " " + msg);	
		VRobotMoves.step(getName(),conn);
		currentMove="step";
 	}
 	
 	@State( name = "alarm" )
 	@Transition( state = "endalarm" , msgId = SystemData.endAlarmId )
	protected void alarm( IApplMessage msg ) { 
 		wotkInterrupted = true;
 		//ColorsOut.outappl( "alarm while doing:"+ currentMove + " " + msg , ColorsOut.RED);
 	}
 	
 	@State( name = "endalarm" )
  	@Transition( state = "continueWork" , msgId = SystemData.endMoveOkId )  //potrebbe essere interrotto moveLeft
	//@Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
 	protected void endalarm( IApplMessage msg ) {
 		outInfo(""+msg);
 	}
 	
 	@State( name = "continueWork" )
 	@Transition( state = "robotMoving" ,   msgId = SystemData.endMoveOkId)
 	@Transition( state = "wallDetected" ,  msgId = SystemData.endMoveKoId)
 	protected void continueWork( IApplMessage msg ) {
 		outInfo(""+msg);
 		//JSONObject move = new JSONObject( msg.msgContent());
 		if( msg.msgContent().equals("turnLeft") ) { 
  			ncorner++; 
// 			VRobotMoves.step(getName(),conn);
// 			currentMove="step"; 
 		}  
			VRobotMoves.step(getName(),conn);
			currentMove="step"; 		
 	}
 	
 	
 	/* 
 	 * Transizioni condizionate (con guardie)
 	 */
 	@State( name = "wallDetected" )
	@Transition( state = "alarm" ,   msgId = SystemData.fireEventId )
 	protected void wallDetected( IApplMessage msg ) {
		outInfo("ncorner="+ ncorner + " " + msg);	
		ncorner++;
		//Parte aggiunta al termine, per definire le transizioni
 		if( ncorner == 4 ) {
 			addTransition("endWork", null); //empty move
  		}else {
  			currentMove="turnLeft";
  			VRobotMoves.turnLeft(getName(), conn);
  			addTransition("robotMoving",  SystemData.endMoveOkId);
  		}
 	}
 	
 
 	
 	@State( name = "endWork" )
 	protected void endWork( IApplMessage msg ) {
 		VRobotMoves.turnLeft(getName(), conn);
		outInfo("BYE" );	
 		System.exit(0);
 	}
 	

}
