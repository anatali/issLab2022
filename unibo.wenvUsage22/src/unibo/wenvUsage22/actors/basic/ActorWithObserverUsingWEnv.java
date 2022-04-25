package unibo.wenvUsage22.actors.basic;

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

public class ActorWithObserverUsingWEnv extends QakActor22  {
	private Interaction2021 conn;
	private int n = 0;
	
	public ActorWithObserverUsingWEnv(String name) {
		super(name);
		init();
	}

	protected void init() {
		conn = WsConnection.create("localhost:8091" );
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
			autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
			return;
		}
		if( m.isEvent() || m.msgId().equals( SystemData.wsEventId ) ) {
			handleWsInfo(m);
			return;
		}
		if( ! m.msgId().equals( ApplData.moveCmdId )) {
			ColorsOut.outappl(getName() + " | sorry, I don't handle :" + m,  ColorsOut.YELLOW);
 		}else {
			//ColorsOut.outappl(getName() + " | interpret:" + m.msgContent(),  ColorsOut.BLUE);
			switch( m.msgContent() ) {
				case "w" : VRobotMoves.moveForward(getName(),conn,2300);break;
				case "a" : VRobotMoves.turnLeft(getName(),conn);break;
				default: break;
			}
 		}
	}

    protected void handleWsInfo(IApplMessage m) {
		ColorsOut.outappl(getName() + " | handleWsInfo:" + m,  ColorsOut.GREEN);	
		String msg = m.msgContent().replace("'", "");
		JSONObject d = new JSONObject(""+msg);
		if( d.has("collision")) {
			n++;
			autoMsg(ApplData.moveCmd(getName(),getName(),"a"));
			//sendMsg(ApplData.moveCmd(getName(),getName(),"a"));
		}
		if( d.has("endmove") && d.getBoolean("endmove") && n < 4) 
			//autoMsg(ApplData.moveCmd(getName(),getName(),"w"));
			sendMsg(ApplData.moveCmd(getName(),getName(),"w"));
   	
    }
 
}
