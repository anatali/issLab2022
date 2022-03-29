package it.unibo.radarSystem22_4.appl.handler;


import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22_4.appl.interpreter.LedApplInterpreter;
import it.unibo.radarSystem22_4.comm.ApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplInterpreter;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;

 
public class LedApplHandler extends ApplMsgHandler {
private IApplInterpreter ledInterpr;

	public static IApplMsgHandler create(String name, ILed led) {
		return new LedApplHandler(name,led);
	}
 
	public LedApplHandler(String name, ILed led) {
		super(name);
		ledInterpr = new LedApplInterpreter(led) ;
 	}
	

 	
 	@Override
	public void elaborate(IApplMessage message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate message=" + message + " conn=" + conn , ColorsOut.GREEN);
 		if( message.isRequest() ) 
 			sendAnswerToClient( ledInterpr.elaborate(message), conn );
 		else ledInterpr.elaborate(message);
	}

}
