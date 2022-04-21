package unibo.actor22comm;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.Qak22Context;
import unibo.actor22.QakActor22;
import unibo.actor22comm.interfaces.ActionFun;
import unibo.actor22comm.utils.ColorsOut;
 
/*
 * Offre ai POJO un metodo per  inviare una richiesta 
 * e poi fare una callback per dare la relativa risposta
 */
public class ActorForRequest extends QakActor22{
private ActionFun callback;

	public ActorForRequest(String name ) {
		super(name);
	    ColorsOut.out("ActorForRequest | created " + name  , ColorsOut.GREEN);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		//BasicUtils.aboutThreads(getName()  + " |  Before doJob - ");
		//ColorsOut.outappl( getName()  + " | handleMsg " + msg, ColorsOut.BLUE);
//		if( msg.isRequest() ) {
//			doRequest(msg);
//		}
//		else 
		if( msg.isReply() ) {
			sendAnswer( msg );	
		}
	}
	
	public void doRequest(IApplMessage msg, ActionFun callback) {
		this.callback = callback;
	    ColorsOut.out("ActorForRequest | doing request " + msg  , ColorsOut.GREEN);
		this.request(msg);
	}
	
	protected void sendAnswer(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | sendAnswer " + msg, ColorsOut.BLUE);
		callback.run(msg.toString() );
		Qak22Context.removeActor(this);
	}

}
