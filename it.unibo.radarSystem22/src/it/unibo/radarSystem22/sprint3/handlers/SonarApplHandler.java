package it.unibo.radarSystem22.sprint3.handlers;

 
import it.unibo.comm2022.ApplMsgHandler;
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.sprint3.interpreters.IApplInterpreter;
import it.unibo.radarSystem22.sprint3.interpreters.SonarApplInterpreter;
 

public class SonarApplHandler extends ApplMsgHandler  {
 
private IApplInterpreter sonarIntepr;

public static IApplMsgHandler create(String name, ISonar sonar) {
		return new SonarApplHandler(name, sonar);	 
}
		public SonarApplHandler(String name, ISonar sonar) {
			super(name);
			sonarIntepr = new SonarApplInterpreter(sonar);
			ColorsOut.out(name+ " | SonarApplHandler CREATED with sonar= " + sonar, ColorsOut.BLUE);
	 	}
 
 		
 		@Override
		public void elaborate(String message, Interaction2021 conn) {
 			ColorsOut.out(name+ " | elaborate " + message + " conn=" + conn, ColorsOut.BLUE);
 			if( message.equals("getDistance") || message.equals("isActive")  ) {
 				sendMsgToClient( sonarIntepr.elaborate(message), conn );
   			}else sonarIntepr.elaborate(message);
 		}
}
