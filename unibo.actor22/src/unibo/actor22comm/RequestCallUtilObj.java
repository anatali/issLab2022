package unibo.actor22comm;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.ActionFun;
import unibo.actor22comm.utils.ColorsOut;

public class RequestCallUtilObj {
	String answer = null;
	
	public RequestCallUtilObj(IApplMessage msg) {
		doRequest(msg);
	}
	
	protected synchronized void doRequest(IApplMessage msg) {
    	ActorForRequest caller = new ActorForRequest("caller_"+msg.msgSender());
	    ActionFun endFun = (answer) -> { 
	    	System.out.println(answer); 
	    	this.answer = answer;
	    };
	    ColorsOut.out("RequestCallUtilObj | doRequest " + msg  , ColorsOut.GREEN);
     	caller.doRequest(msg, endFun);
  	}
	
	public synchronized String getAnswer( ) {
		try {			
			while( answer == null ) wait();
 		} catch (InterruptedException e) {
 			e.printStackTrace();
		}
		return answer;
	}
}
