package unibo.actor22comm;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.QakActor22;
import unibo.comm22.interfaces.IApplMsgHandler;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;
 

public class ActorForReply extends QakActor22{
private IApplMsgHandler h;
private Interaction2021 conn;

	public ActorForReply(String name, IApplMsgHandler h, Interaction2021 conn) {
		super(name);
		this.h = h;
		this.conn = conn;		 
	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		//BasicUtils.aboutThreads(getName()  + " |  Before doJob - ");
		//ColorsOut.outappl( getName()  + " | handleMsg " + msg, ColorsOut.BLUE);
		if( msg.isReply() ) h.sendAnswerToClient(msg.toString(), conn);		
		Qak22Context.removeActor(this);
	}

}
