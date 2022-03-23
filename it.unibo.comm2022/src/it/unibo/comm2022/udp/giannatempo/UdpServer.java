package it.unibo.comm2022.udp.giannatempo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.utils.ColorsOut;



public class UdpServer extends Thread{
private DatagramSocket socket;
private byte[] buf;
public Map<UdpEndpoint,UdpServerConnection> connectionsMap; //map a port to a specific connection object, if any
protected IApplMsgHandler userDefHandler;
protected String name;
protected boolean stopped = true;

 	public UdpServer( String name, int port,  IApplMsgHandler userDefHandler   ) {
		super(name);
		connectionsMap = new ConcurrentHashMap<UdpEndpoint,UdpServerConnection>();
	      try {
	  		this.userDefHandler   = userDefHandler;
	  		ColorsOut.out(getName() + " | costructor port=" + port, ColorsOut.BLUE  );  
			this.name             = getName();
			socket                = new DatagramSocket( port );
	     }catch (Exception e) { 
	    	 ColorsOut.outerr(getName() + " | costruct ERROR: " + e.getMessage());
	     }
	}
	
	@Override
	public void run() {
	      try {
		  	ColorsOut.out( "UdpServer | STARTING ... "  );
			while( ! stopped ) {
				//Wait a packet				 
				ColorsOut.out( "UdpServer | waits a packet "  );	 
				buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				InetAddress address = packet.getAddress();
	            int port = packet.getPort();
	            UdpEndpoint client = new UdpEndpoint(address, port);
	            //String received = new String(packet.getData(), 0, packet.getLength());
	            ColorsOut.out( "UdpServer | received packet from " + client   ); 
	            UdpServerConnection conn = connectionsMap.get(client);
	            if(conn == null) {
	            	conn = new UdpServerConnection(socket, client, connectionsMap);
	            	connectionsMap.put(client, conn);
	            }else {
	            	 ColorsOut.outappl("UdpServer | CONNECTION ALREADY SET with " + client, ColorsOut.GREEN   ); 
	            }
	            conn.handle(packet);		 
		 		//Create a message handler on the connection
		 		new UdpApplMessageHandler( userDefHandler, conn );			 		
			}//while
		  }catch (Exception e) {  //Scatta quando la deactive esegue: serversock.close();
			  ColorsOut.out( "UdpServer |  probably socket closed: " + e.getMessage(), ColorsOut.GREEN);		 
		  }
	}
	
	public void activate() {
		if( stopped ) {
			stopped = false;
			this.start();
		}//else already activated
	}
 
	public void deactivate() {
		try {
			ColorsOut.out( "UdpServer |  DEACTIVATE serversock=" +  socket);
			stopped = true;
			socket.close();
			connectionsMap.clear();
		} catch (Exception e) {
			ColorsOut.outerr( "UdpServer |  deactivate ERROR: " + e.getMessage());	 
		}
	}

	public int getNumConnections() {
		return connectionsMap.size();
	}
 
}
