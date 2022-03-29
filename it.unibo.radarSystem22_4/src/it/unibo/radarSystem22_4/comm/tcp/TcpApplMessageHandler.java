package it.unibo.radarSystem22_4.comm.tcp;

import it.unibo.radarSystem22_4.comm.ApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

/*
 * Ente attivo per la ricezione di messaggi su una connessione Interaction2021
 */
public class TcpApplMessageHandler extends Thread{
private IApplMsgHandler handler ;
private Interaction2021 conn;

public TcpApplMessageHandler(  IApplMsgHandler handler, Interaction2021 conn ) {
		this.handler = handler;
		this.conn    = conn;
 		this.start();
	}
 	
	@Override 
	public void run() {
		String name = handler.getName();
		try {
			ColorsOut.out( "TcpApplMessageHandler | STARTS with handler=" + name + " conn=" + conn, ColorsOut.BLUE );
			while( true ) {
				//ColorsOut.out(name + " | waits for message  ...");
			    String msg = conn.receiveMsg();
			    ColorsOut.out(name + "  | TcpApplMessageHandler received:" + msg + " handler="+handler, ColorsOut.GREEN );
			    if( msg == null ) {
			    	conn.close();
			    	break;
			    } else{ 
			    	//Creare ApplMessage
			    	IApplMessage m = new ApplMessage(msg);
			    	handler.elaborate( m, conn ); 
			    }
			}
			ColorsOut.out(name + " | BYE"   );
		}catch( Exception e) {
			ColorsOut.outerr( name + "  | ERROR:" + e.getMessage()  );
		}	
	}
}
