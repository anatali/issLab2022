package unibo.wenvUsage22.surveyor;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.basicRobot.prototype0.WsConnApplObserver;
import unibo.wenvUsage22.common.VRobotMoves;

public class RobotSurveyor extends QakActor22FsmAnnot  {
	private Interaction2021 conn;
	private int stepXCounter = 0 ;
	private int stepYCounter = 0 ;

	public RobotSurveyor(String name) {
		super(name);
 	}
	
	protected void init() {
 		ColorsOut.outappl(getName() + " | ws connecting ...." ,  ColorsOut.BLUE);
		conn = WsConnection.create("localhost:8091" ); 
		IObserver robotMoveObserver = new WsConnApplObserver(getName(), true);  //endMOve after turn
		((WsConnection)conn).addObserver(robotMoveObserver);
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}	
	@State( name = "activate", initial=true)
	@Transition( state = "start",   msgId= SystemData.startSysCmdId  )
	protected void activate( IApplMessage msg ) {
		outInfo(""+msg);
		init();
 	}
	
	@State( name = "start" )
	@Transition( state = "alongY")
	protected void start( IApplMessage msg ) {
		outInfo(""+msg);
 	}
	
	
	@State( name = "alongY" )
 	@Transition( state = "alongY" ,           msgId = SystemData.endMoveOkId )
	@Transition( state = "wallDownDetected" , msgId = SystemData.endMoveKoId )
	protected void alongY( IApplMessage msg ) {
		outInfo(""+msg);
		stepYCounter++;
		VRobotMoves.step(getName(), conn );
 	}	

	@State( name = "wallDownDetected" )
	@Transition( state = "alongX", msgId = SystemData.endMoveOkId )
	protected void wallDownDetected( IApplMessage msg ) {
		outInfo(""+msg);
		ColorsOut.outappl("stepYCounter="+stepYCounter, ColorsOut.MAGENTA);
		stepXCounter=1;
		VRobotMoves.turnLeft( getName(), conn);
 	}
	
	@State( name = "alongX" )
	@Transition( state = "alongX" , msgId = SystemData.endMoveOkId)
	@Transition( state = "wallRightDetected" , msgId = SystemData.endMoveKoId )
	protected void alongX( IApplMessage msg ) {
		outInfo(""+msg);
		stepXCounter++;
		VRobotMoves.step(getName(), conn );
 	}

	@State( name = "wallRightDetected" )
 	protected void wallRightDetected( IApplMessage msg ) {
		outInfo(""+msg);
		ColorsOut.outappl("stepXCounter="+stepXCounter, ColorsOut.MAGENTA);
  	}
}
