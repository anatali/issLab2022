package it.unibo.radarSystem22_4.common;

import it.unibo.radarSystem22_4.comm.ApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class NaiveApplHandler extends ApplMsgHandler {

	public NaiveApplHandler(String name) {
		super(name);
	}
 

	@Override
	public void elaborate(IApplMessage message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate " + message + " conn=" + conn, ColorsOut.BLUE);
		//this.sendMsgToClient("answerTo_"+message, conn);  //MODIFIED for udp 
		sendAnswerToClient("answerTo_"+message, conn);
  	}

}
