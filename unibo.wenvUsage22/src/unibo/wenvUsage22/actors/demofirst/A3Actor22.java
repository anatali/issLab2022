package unibo.wenvUsage22.actors.demofirst;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public class A3Actor22 extends QakActor22{

	public A3Actor22(String name) {
		super(name);
 	}
 
	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | handleMsg " + msg, ColorsOut.CYAN);	
		if( msg.isDispatch() && msg.msgId().equals(SystemData.activateActorCmd) ) {
			//CommUtils.delay(1000);
			ColorsOut.outappl( getName()  + " | ACTIVATED " , ColorsOut.YELLOW);
		}else {
 			elabMsg(msg);
		}	
	}

	protected void elabMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabMsg " + msg, ColorsOut.CYAN);	
		if( msg.isRequest() ) {
			IApplMessage reply = Qak22Util.prepareReply(msg, "ok!");
			ColorsOut.outappl( getName()  + " | sendReply " + reply, ColorsOut.GREEN);	
			sendReply(msg, reply);
		}		
	}

}
