package it.unibo.radarSystem22_4.comm.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;
  

public class UdpConnection implements Interaction2021{
	
public static final int MAX_PACKET_LEN = 1025;
public static final String closeMsg    = "@+-systemUdpClose@+-";
protected DatagramSocket socket;
protected UdpEndpoint endpoint;
protected boolean closed;

	public UdpConnection( DatagramSocket socket, UdpEndpoint endpoint) throws Exception {
		closed = false;
		this.socket = socket;
		this.endpoint = endpoint;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		//ColorsOut.out( "UdpConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );	 
		if (closed) { throw new Exception("The connection has been previously closed"); }
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, endpoint.getAddress(), endpoint.getPort());
	        socket.send(packet);
			//ColorsOut.out( "UdpConnection | has sent   " + msg, ColorsOut.ANSI_YELLOW );	 
		} catch (IOException e) {
			//ColorsOut.outerr( "UdpConnection | sendALine ERROR " + e.getMessage());	 
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
		String line;
 		try {
			if(closed) {
				line = null; //UdpApplMessageHandler will terminate
			}else {
				byte[] buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
			    //BasicUtils.aboutThreads( " | UdpConnection Before Receive  " );  
				socket.receive(packet);  //CREA UN Thread
			    //BasicUtils.aboutThreads( " | UdpConnection After Receive  "  );  
				line = new String(packet.getData(), 0, packet.getLength());
				if( line.equals(closeMsg)) {
					close();
				}
				packet = null;
			}
 			return line;		
		} catch ( Exception e ) {
			ColorsOut.outerr( "UdpConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		return null;
		}		
	}

	@Override
	public void close() {
		try {
			//CommUtils.buildDispatch("", "closeUdpConn", closeMsg, "system");
			forward(closeMsg);
		} catch (Exception e) {
			ColorsOut.outerr( "UdpConnection | close ERROR  " + e.getMessage() );	
		}
		closed = true;
		ColorsOut.out( "UdpConnection | closing   ", ColorsOut.ANSI_YELLOW );
		socket.close();
	}



}
