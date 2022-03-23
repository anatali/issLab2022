package it.unibo.comm2022.common;

import it.unibo.comm2022.ApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.ColorsOut;

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

}
