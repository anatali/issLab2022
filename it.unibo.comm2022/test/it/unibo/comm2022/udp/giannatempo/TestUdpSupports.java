package it.unibo.comm2022.udp.giannatempo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.comm2022.common.NaiveApplHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.BasicUtils;
import it.unibo.comm2022.utils.ColorsOut;


public class TestUdpSupports {
private UdpServer server;
public static final int testPort = 8111; 


	
	
	protected void startTheServer(String name) {
		server = new UdpServer(name, testPort, new NaiveApplHandler("naiveH") );
		server.activate();		
	}
	
	protected void stopTheServer() {
		if( server != null ) server.deactivate();		
	}

	
	@Test 
	public void testManyConns() {
		ColorsOut.out(" -------------- testManyConns");
//		server = new UdpServer("udpSrv", testPort, new NaiveApplHandler("naiveH") 
//		{
//			@Override
//			public void elaborate(String message, Interaction2021 conn) {
//				ColorsOut.out( "naiveH received " + message);
//			}
//		}
//		);
//		server.activate();		
		
		startTheServer("manyConnsServer");
		 try {
		     Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupports.testPort);
			 for( int i=1; i<=3;i++) {
				//Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
				String msg = "hello"+i  ;
				System.out.println("testManyConns | forward the msg=" + msg + " on conn:" + conn);	 
				conn.forward(msg);
				BasicUtils.delay(100);
				String answer = conn.receiveMsg();
				System.out.println("testManyConns | fanswer=" + answer + " on conn:" + conn);	 
				BasicUtils.delay(100);
			 }//for
		 } catch (Exception e) {
			 fail();
		 }
		BasicUtils.delay(1000);
		stopTheServer();
		ColorsOut.out(" -------------- testManyConns BYE");
	}
	
	@Test 
	public void testSingleClient() {
		ColorsOut.out(" -------------- testSingleClient");
 		startTheServer("oneClientServer");
 		//Create a connection
		new ClientDoingRequest().doWork("client1");		
		stopTheServer();
		ColorsOut.out(" -------------- testSingleClient BYE");
	}
	
	
	@Test 
	public void testManyClients() {
		ColorsOut.out(" -------------- testManyClients");
 		startTheServer("manyClientsServer");
		new ClientDoingRequest().doWork("client1");
		new ClientDoingRequest().doWork("client2");
		new ClientDoingRequest().doWork("client3");
		stopTheServer();
		ColorsOut.out(" -------------- testManyClients BYE");
	}
	
	@Test
	public void testManyRequests() {
		ColorsOut.out(" -------------- testManyRequests");
		try {
			NaiveApplHandler handler = new NaiveApplHandler("naiveHMod") {
				int i = 0;
				@Override
				public void elaborate(String message, Interaction2021 conn) {
					System.out.println(name + " | elaborate " + message + " conn=" + conn);
					BasicUtils.delay(100); //taking some time to reply
					this.sendMsgToClient("answerTo_"+message, conn);
					i++;
					try {
						if(i%2==0) conn.close(); //even if the connection closes, and even if the current connection has a message in it, all the requests are processed
					} catch (Exception e) { fail(); }
			  	}
			};
			server = new UdpServer("manyRequestsServer", testPort, handler );
			server.activate();
			
			Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupports.testPort);
			
			for(int i=0; i<4; i++) {
				String request = "hello_"+i;
				System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
				conn.forward(request);
			}
			int i=0;
			for(i=0; i<4; i++) {
				String answer = conn.receiveMsg();
				System.out.println("client1" + " | receives the answer: " +answer );
			}
			assertEquals(4,i);
			
			BasicUtils.delay(10);
			assertEquals(0,server.getNumConnections()); //if connection has been closed, no connections should be left
			
			String request = "hello_"+i;
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(request);
			BasicUtils.delay(10);
			assertEquals(1,server.getNumConnections()); //this connection has still not been closed by the server
			
			stopTheServer();
			ColorsOut.out(" -------------- testManyRequests BYE");
			
		} catch (Exception e) {
			System.out.println("client1" + " | ERROR " + e.getMessage());
		}
	}
	

//	private void delay( int dt ) {
//		try {
//			Thread.sleep(dt);
//		} catch (InterruptedException e) {
//				e.printStackTrace();
//		}		
//	}
	
}
