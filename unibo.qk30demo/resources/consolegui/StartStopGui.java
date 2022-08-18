package consolegui;

import java.util.Observable;
import java.util.Observer;
import org.eclipse.paho.client.mqttv3.MqttException;

import common.ConnQakTcp;
import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MqttUtils;
import it.unibo.kactor.MsgUtil;
import unibo.comm22.interfaces.Interaction2021;
 

/*
 * This Java code makes reference to Kotlin code
 * However Messages.forward is more difficult to use since it is a suspend function
 * that includes a Continuation as its last argument.
 * Fir the sake of simplicity, we redefine here the forward operation.
 */

public class StartStopGui implements  Observer{
private String[] buttonLabels  = new String[] {"init", "stop", "resume", "exit" };   
private Interaction2021 conn;
private String destName;

	public StartStopGui( String destName, int port) {
		ButtonAsGui concreteButton = ButtonAsGui.createButtons( "", buttonLabels );
		concreteButton.addObserver( this );
		this.destName = destName;
		conn = new ConnQakTcp().createConnection("localhost", port);
 	}
	
 	
	public void update( Observable o , Object arg ) {
		try {
			String move = arg.toString();
			System.out.println("GUI input move=" + move);
			if( move.equals("exit")) System.exit(1);
			IApplMessage msg = MsgUtil.buildDispatch("gui", move, move+"(1)", destName);
			conn.forward(msg.toString());
		} catch (Exception e) {
 			e.printStackTrace();
		}	
	}
	
//	public static void main( String[] args) {
// 		new StartStopGui( "StartStopGui" );
//	}
}

