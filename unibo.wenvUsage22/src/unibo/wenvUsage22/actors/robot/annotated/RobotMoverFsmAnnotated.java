package unibo.wenvUsage22.actors.robot.annotated;
 
import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;


public  class RobotMoverFsmAnnotated extends QakActor22FsmAnnot  {
	private Interaction2021 conn;
	public RobotMoverFsmAnnotated(String name) {
		super(name);
 	}
	
	@State( name = "robotStart", initial=true)
//	@Transition( state = {"robotMoving" }, 
//	             msgId = { ApplData.robotCmdId })
	@Transition( state = "robotMoving", msgId=ApplData.robotCmdId )
	protected void robotStart( IApplMessage msg ) {
		outInfo(""+msg + " connecting (blocking all the actors ) ... ");	
		//Inizializzo la connessione con WEnv
 		conn = new WsConnection("localhost:8091");//WsConnection.create("localhost:8091" );				 
 		outInfo("connected "+conn);	
		//Aggiungo l'attore come observer dei messaggi inviati da WEnv (vedi update)
		((WsConnection)conn).addObserver((IObserver) myself);
	}
	
	//Muovo il robot in funzione del comando che mi arriva
	@State( name = "robotMoving" )
//	@Transition( state = {"robotMoving", "s3" }, 
//	             msgId = { ApplData.robotCmdId, ApplData.haltSysCmdId })
	@Transition( state = "robotMoving", msgId=ApplData.robotCmdId )
	@Transition( state = "endWork",     msgId=ApplData.haltSysCmdId )
	protected void robotMoving( IApplMessage msg ) {
		outInfo(""+msg);	
		doMove( msg.msgContent().replaceAll("'","") ); //remove ' ' 		
	}
	
	@State( name = "endWork" )
	protected void endWork( IApplMessage msg ) {
		outInfo("BYE" );
	}
	
	protected void doMove(String move) {
		try {
			outInfo("doMove:"+move);	
			conn.forward( move );
		}catch( Exception e) {
			ColorsOut.outerr("robotMOve ERROR:" + e.getMessage() );
		}
	}

 	
//	@Override
//	public void handleAsObserver(String data) {
//		ColorsOut.outappl(getName() + " |  asobserver receives:" + data, ColorsOut.MAGENTA);
//	}	


}
