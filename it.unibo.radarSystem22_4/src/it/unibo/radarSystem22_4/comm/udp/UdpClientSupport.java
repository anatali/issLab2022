package it.unibo.radarSystem22_4.comm.udp;
import java.net.DatagramSocket;
import java.net.InetAddress;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;


public class UdpClientSupport {

	
	public static Interaction2021 connect(String host, int port) throws Exception {
		DatagramSocket socket = new DatagramSocket();
        InetAddress address   = InetAddress.getByName(host);
        UdpEndpoint server    = new UdpEndpoint(address, port);
		Interaction2021 conn  = new UdpConnection(socket, server);
		return conn;
 	}
 
}
