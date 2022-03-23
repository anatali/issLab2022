package it.unibo.comm2022.interfaces;

import java.net.DatagramSocket;

public interface IUdpConnection {
		public  DatagramSocket connectAsReceiver(int portNum) throws Exception;
		public DatagramSocket connectAsClient(String hostName, int port) throws Exception;
		public void closeConnection(DatagramSocket socket);
}
