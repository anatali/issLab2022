package unibo.wenvUsage22.annot.walker;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.VRobotMoves;

public class BoundaryWalkerAnnot extends QakActor22FsmAnnot  {
	private Interaction2021 conn;	
 	private int ncorner  = 0;

 	public BoundaryWalkerAnnot(String name) {
		super(name);
 	}
	
	@State( name = "robotStart", initial=true)
	@Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId )
	@Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
	protected void robotStart( IApplMessage msg ) {
		outInfo(""+msg + " connecting (blocking all the actors ) ... ");	
 		conn = WsConnection.create("localhost:8091" ); 	 
 		outInfo("connected "+conn);	
   		((WsConnection)conn).addObserver( new WsConnWEnvObserver(getName()) );
  		VRobotMoves.step(getName(),conn);
	}
	
 	@State( name = "robotMoving" )
 	@Transition( state = "robotMoving" ,  msgId = SystemData.endMoveOkId)
  	@Transition( state = "wallDetected" , msgId = SystemData.endMoveKoId )
	protected void robotMoving( IApplMessage msg ) {
		//outInfo(""+msg);	
		VRobotMoves.step(getName(),conn);
 	}
 	
 	@State( name = "wallDetected" )
	@Transition( state = "robotMoving" , 
		msgId = SystemData.endMoveOkId,guard=GuardContinueWork.class   )
 	@Transition( state = "endWork" ,     
 		msgId = SystemData.endMoveOkId,guard=GuardEndOfWork.class )
	protected void wallDetected( IApplMessage msg ) {
		outInfo("ncorner="+ ncorner + " " + msg);	
		ncorner++;
		GuardContinueWork.setValue(ncorner);
		GuardEndOfWork.setValue(ncorner);
		VRobotMoves.turnLeft(getName(), conn);
 	}

 	/*
 	 * Transizioni condizionate (con guardie)
 	 */
// 	@State( name = "wallDetected" )
// 	protected void wallDetected( IApplMessage msg ) {
//		outInfo("ncorner="+ ncorner + " " + msg);	
//		ncorner++;
//		//Parte aggiunta al termine, per definire le transizioni
// 		if( ncorner == 4 ) {
// 			addTransition("endWork", null); //empty move
//  		}else {
//  			VRobotMoves.turnLeft(getName(), conn);
//  			addTransition("robotMoving",  SystemData.endMoveOkId);
//  		}
// 	}
 	
 	@State( name = "endWork" )
 	protected void endWork( IApplMessage msg ) {
 		//VRobotMoves.turnLeft(getName(), conn);
		outInfo("BYE" );	
 		System.exit(0);
 	}
 	

}
