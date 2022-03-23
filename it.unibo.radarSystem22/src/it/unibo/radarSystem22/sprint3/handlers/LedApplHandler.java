package it.unibo.radarSystem22.sprint3.handlers;

import it.unibo.comm2022.ApplMsgHandler;
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.sprint3.interpreters.IApplInterpreter;
import it.unibo.radarSystem22.sprint3.interpreters.LedApplInterpreter;
 
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
	public void elaborate(String message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate message=" + message + " conn=" + conn , ColorsOut.GREEN);
 		if( message.equals("getState") ) 
 			sendMsgToClient( ledInterpr.elaborate(message), conn );
 		else ledInterpr.elaborate(message);
	}

}
