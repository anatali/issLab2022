package it.unibo.radarSystem22_4.comm.tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

  

public class TcpConnection implements Interaction2021{
private DataOutputStream outputChannel;
private BufferedReader inputChannel;
private Socket socket;

	public TcpConnection( Socket socket  ) throws Exception {
		this.socket                    = socket;
		OutputStream outStream 	       = socket.getOutputStream();
		InputStream inStream  	       = socket.getInputStream();
		outputChannel                  =  new DataOutputStream(outStream);
		inputChannel                   =  new BufferedReader( new InputStreamReader( inStream ) );		
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		ColorsOut.out( "TcpConnection | sendALine  " + msg + " on " + outputChannel, ColorsOut.ANSI_YELLOW );	 
		try {
			outputChannel.writeBytes( msg+"\n" );
			outputChannel.flush();
			//Colors.out( "TcpConnection | has sent   " + msg, Colors.ANSI_YELLOW );	 
		} catch (IOException e) {
			//Colors.outerr( "TcpConnection | sendALine ERROR " + e.getMessage());	 
			throw e;
		}	
	}

	@Override
	public String request(String msg)  throws Exception {
		forward(  msg );
		String answer = receiveMsg();
		return answer;
	}


	@Override
	public void reply(String msg) throws Exception {
		forward(msg);
	} 
	
	@Override
	public String receiveMsg()  {
 		try {
			//socket.setSoTimeout(timeOut)
			String	line = inputChannel.readLine() ; //blocking =>
 			return line;		
		} catch ( Exception e   ) {
			ColorsOut.outerr( "TcpConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		return null;
		}		
	}

	@Override
	public void close() { 
		try {
			socket.close();
			ColorsOut.out( "TcpConnection | CLOSED  " );
		} catch (IOException e) {
			ColorsOut.outerr( "TcpConnection | close ERROR " + e.getMessage());	
		}
	}



}
