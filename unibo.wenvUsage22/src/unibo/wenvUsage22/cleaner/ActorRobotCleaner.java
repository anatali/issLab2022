package unibo.wenvUsage22.cleaner;

import org.eclipse.californium.core.CoapResource;
/*
 * WsConnection è associata a un observer che emette SystemData.wsEventId
 */
import org.json.JSONObject;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.ws.WsConnSysObserver;
import unibo.actor22comm.ws.WsConnection;
import unibo.wenvUsage22.common.ApplData;
import unibo.wenvUsage22.common.VRobotMoves;

public class ActorRobotCleaner extends QakActor22  {
	private Interaction2021 conn;
	private int turnStep    = 600;   //600 => too fast  300 => ok

	private enum State {start, goingDown, goingUp, end };
	private State curState  =  State.start ;
	private int numIter     = 0;
	
	public ActorRobotCleaner(String name) {
		super(name);
		init();
	}

	protected void init() {
//		conn = WsConnectionForActors.create("localhost:8091", getName() ); //con owner
		conn = WsConnection.create("localhost:8091"  ); 
		((WsConnection) conn).addObserver( new WsConnSysObserver(getName()) );
 		ColorsOut.outappl(getName() + " | conn:" + conn,  ColorsOut.BLUE);
	}
	
	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.outappl(getName() + " | handleMsg:" + msg,  ColorsOut.BLUE);
		interpret(msg);
	}
	
	protected void interpret( IApplMessage m ) {
 		if( m.msgId().equals( ApplData.activateId )) {
			//autoMsg(ApplData.moveCmd(getName(),getName(),"p"));
 			fsm("",true);
			return;
		}
		if( m.isEvent() || m.msgId().equals( SystemData.wsEventId ) ) {  //Rimando a uno stato??
			handleWsInfo(m);
			return;
		}
		if( ! m.msgId().equals( ApplData.moveCmdId )) {
			ColorsOut.outappl(getName() + " | sorry, I don't handle :" + m,  ColorsOut.YELLOW);
			return;
		}

	}
	
	protected void fsm(String move, boolean endmove){
		ColorsOut.outappl("fsm " + curState + " move=" + move + " endmove="+endmove , ColorsOut.CYAN);
        switch( curState ) {
        case start: {
//            moves.showRobotMovesRepresentation();
        	VRobotMoves.step(getName(), conn );
            curState = State.goingDown;
            numIter++;
            break;
        }
        case goingDown: {
            if (endmove ) { //move.equals("moveForward") && 
             	VRobotMoves.step(getName(), conn );
             } else {
                VRobotMoves.turnLeftAndStep(getName(), turnStep, conn);
                curState = State.goingUp;
            }
            break;
        }//goingDown

        
        case goingUp : {
            if(  endmove ) {
            	VRobotMoves.step(getName(), conn );
            }else {
            	 numIter++;
            	 if( numIter == 3 ) {
            		 curState = State.end; 
            		 VRobotMoves.turnLeftAndHome(getName(), conn);  
            	 }else {
	                 VRobotMoves.turnRightAndStep(getName(),turnStep, conn);
	                 curState = State.goingDown;     
            	 }
            } break;
        }        
 
        
        
        case end :  {
              	ColorsOut.outappl("END", ColorsOut.MAGENTA);
                //moves.showRobotMovesRepresentation();
            break;
        }
        default: {
        	ColorsOut.outerr("error - curState = " + curState  );
        }
    }
		
	}
	 

    protected void handleWsInfo(IApplMessage m) {
		ColorsOut.outappl(getName() + " | handleWsInfo:" + m,  ColorsOut.GREEN);	
		String msg = m.msgContent().replace("'", "");
		JSONObject d = new JSONObject(""+msg);

		if( d.has("endmove") && d.getBoolean("endmove")) { // && ! d.getString("move").equals("turnLeft")
 			if(  ! d.getString("move").contains("turn"))
 				fsm( d.getString("move"), true);
		}else {
			if( d.has("collision") ) fsm( d.getString("collision"), false);
			else ColorsOut.outerr("error - handleWsInfo " + m);
		}
    }
 
}
