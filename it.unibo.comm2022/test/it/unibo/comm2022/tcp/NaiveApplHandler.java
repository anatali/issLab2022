package it.unibo.comm2022.tcp;

import it.unibo.comm2022.ApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;

public class NaiveApplHandler extends ApplMsgHandler {

	public NaiveApplHandler(String name) {
		super(name);
	}
 

	@Override
	public void elaborate(String message, Interaction2021 conn) {
		System.out.println(name + " | elaborate " + message + " conn=" + conn);
		this.sendMsgToClient("answerTo_"+message, conn);
  	}

}
