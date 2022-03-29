package it.unibo.radarSystem22_4.appl.handler;

 
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22_4.appl.interpreter.SonarApplInterpreter;
import it.unibo.radarSystem22_4.comm.ApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplInterpreter;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

 

public class SonarApplHandler extends ApplMsgHandler  {
 
private IApplInterpreter sonarIntepr;

public static IApplMsgHandler create(String name, ISonar sonar) {
	return new SonarApplHandler(name, sonar); 
}
		public SonarApplHandler(String name, ISonar sonar) {
			super(name);
			sonarIntepr = new SonarApplInterpreter(sonar);
			ColorsOut.out(name+ " | SonarApplHandler CREATED with sonar= " + sonar, ColorsOut.MAGENTA);
	 	}
 
 		
 		@Override
		public void elaborate(IApplMessage message, Interaction2021 conn) {
 			ColorsOut.out(name+ " | elaborate " + message + " conn=" + conn, ColorsOut.MAGENTA);
 			if( message.isRequest() ) {
 				String answer = sonarIntepr.elaborate(message);
 				sendAnswerToClient( answer, conn );
   			}else sonarIntepr.elaborate(message);
 		}
}
