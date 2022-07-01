package it.unibo.platform.bth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import javax.microedition.io.StreamConnection;
import it.unibo.is.interfaces.protocols.IConnInteraction;

public class BthConnSupport implements IConnInteraction{
private boolean debug = false;
private String logo;
private StreamConnection conn;
private InputStream inputChannel;
private OutputStream outputChannel;

	public BthConnSupport(String logo, StreamConnection conn){
		this.logo 	= logo;
		this.conn	= conn;
		try {
			inputChannel  = conn.openInputStream();
			outputChannel  = conn.openOutputStream();	
		} catch (IOException e) {
 			e.printStackTrace();
		}
	} 
	public String receiveALine() throws Exception {
//		InputStreamReader inputReader = new InputStreamReader( inputChannel );
//		BufferedReader inChannel      = new BufferedReader( inputReader );	
		DataInputStream  indata       = new DataInputStream(inputChannel);
//		String	cmd = inChannel.readLine();  //like a receive
		String	cmd =indata.readUTF();
			println( "has read ... " +cmd );
			return cmd;		
	}
 	public void sendALine(String msg) throws Exception {
		DataOutputStream outChannel = new DataOutputStream(outputChannel);
		outChannel.writeUTF( msg );		
	}
	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
 		
	}
	public void closeConnection() throws Exception {
		conn.close();
	}

 
	protected void println(String msg ){
 		if( debug ) 
 			System.out.println( "					%%% SocketBthConnSupport|" + logo + " " + msg  );
 	}


 
 
}
