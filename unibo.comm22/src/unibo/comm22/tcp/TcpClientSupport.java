package unibo.comm22.tcp;
import java.net.Socket;

import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;



public class TcpClientSupport {
 	
	public static Interaction2021 connect(String host, int port, int nattempts ) throws Exception {
 		 
		for( int i=1; i<=nattempts; i++ ) {
			try {
				ColorsOut.outappl("TcpClient | attempt " + i + " to connect with host:" + host + " port=" + port, ColorsOut.YELLOW);
				Socket socket         =  new Socket( host, port );
				Interaction2021 conn  =  new TcpConnection( socket );
				return conn;
			}catch(Exception e) {
				ColorsOut.outerr("TcpClient | Another attempt to connect with host:" + host + " port=" + port);
				Thread.sleep(500);
			}
		}//for
		throw new Exception("TcpClient | Unable to connect to host:" + host);
		 
 	}
 
}
