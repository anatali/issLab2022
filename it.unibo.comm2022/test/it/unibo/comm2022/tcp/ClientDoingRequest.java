package it.unibo.comm2022.tcp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import it.unibo.comm2022.interfaces.Interaction2021;

 
/*
 * A client that performs a request on a connnection with the server 
 * and waits for the answer
 */
public class ClientDoingRequest {
	public static boolean withserver = true;
	
	public void doWork( String name, int nattempts ) {
		try {
			Interaction2021 conn  = TcpClientSupport.connect("localhost", 
					                      TestTcpSupportsForRequest.testPort,nattempts);
			String request = "hello_from_" + name;
			System.out.println(name + " | forward the request=" + request + " on conn:" + conn);	 
			conn.forward(request);
			String answer = conn.receiveMsg();
			System.out.println(name + " | receives the answer: " +answer );	
			assertTrue( answer.equals("answerTo_"+ request));
		} catch (Exception e) {
			System.out.println(name + " | ERROR " + e.getMessage());	
			if( withserver ) fail();
		}
	}

}
