package unibo.comm22;

import it.unibo.kactor.IApplMessage;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;

public class NaiveApplHandler extends ApplMsgHandler {

	public NaiveApplHandler(String name) {
		super(name);
	}
 

	@Override
	public void elaborate(String message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate " + message + " conn=" + conn);
		//this.sendMsgToClient("answerTo_"+message, conn);  //MODIFIED for udp 
		sendAnswerToClient("answerTo_"+message, conn);
  	}


	@Override
	public void elaborate(IApplMessage message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate IApplMessage " + message + " conn=" + conn);
		
	}

}
