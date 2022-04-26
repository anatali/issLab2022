package unibo.wenvUsage22.actors.demofirst;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public class A2Actor22OnRasp extends QakActor22{

	public A2Actor22OnRasp(String name) {
		super(name);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		if( msg.isDispatch() && msg.msgId().equals(SystemData.activateActorCmd) ) {
	 		ColorsOut.outappl( getName()  + " | ACTIVATED " , ColorsOut.YELLOW);
	 		CommUtils.delay(1000);  //a3Rasp should be not activated ...
	 		forward( SystemData.demoSysCmd(getName(),"a3Rasp") );
		}else {
	 		elabMsg(msg);
		}	
	}
	protected void elabMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabMsggg " + msg, ColorsOut.CYAN);	
		if( msg.isReply() ) {
			ColorsOut.outappl( getName()  + " | reply= " + msg.msgContent() + " from " + msg.msgSender(), ColorsOut.GREEN);	
		}
		else request( SystemData.demoSysRequest(getName(),"a3Pc") );
	}

}
