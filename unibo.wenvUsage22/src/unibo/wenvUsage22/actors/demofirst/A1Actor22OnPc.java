package unibo.wenvUsage22.actors.demofirst;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public class A1Actor22OnPc extends QakActor22{

	public A1Actor22OnPc(String name) {
		super(name);
		//Inviare un msg in fase di creazione NON E' BUONA PRATICA in quanto
		//il sistema potrebbe non essere ancora completamente costruito
		//forward( SystemData.demoSysCmd(getName(),"a2") );
 	}
 
	@Override
	protected void handleMsg(IApplMessage msg) {
		if( msg.isDispatch() && msg.msgId().equals(SystemData.activateActorCmd) ) {
			CommUtils.delay(1000);
			ColorsOut.outappl( getName()  + " | ACTIVATED " , ColorsOut.YELLOW);
			forward( SystemData.demoSysCmd(getName(),"a2") );
		}else {
 			elabMsg(msg);
		}	
	}

	protected void elabMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabMsg " + msg + " ? " + msg.isRequest(), ColorsOut.GREEN);	
		if( msg.isRequest() ) {
			IApplMessage reply = Qak22Util.prepareReply(msg, "ok!");
			ColorsOut.outappl( getName()  + " | sendReply " + reply, ColorsOut.GREEN);	
			sendReply(msg, reply);
		}
		
	}

}
