package it.unibo.radarSystem22_4.udp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.udp.UdpClientSupport;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;

 
 
/*
 * A client that performs a request on a connnection with the server 
 * and waits for the answer
 */
public class ClientDoingRequest {
	public static boolean withserver = true;
	
	public void doWork( String name ) {
		try {
			Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupports.testPort);
			String request = "hello_from_" + name;
			IApplMessage m = CommUtils.buildRequest(name, "req", request, "naiveH");
			System.out.println(name + " | forward the request=" + request + " on conn:" + conn);	 
			conn.forward(m.toString());
			String answer = conn.receiveMsg();
			System.out.println(name + " | receives the answer: " +answer );	
			assertTrue( answer.equals("answerTo_"+ m));
		} catch (Exception e) {
			System.out.println(name + " | ERROR " + e.getMessage());	
			if( withserver ) fail();
		}
	}

}
