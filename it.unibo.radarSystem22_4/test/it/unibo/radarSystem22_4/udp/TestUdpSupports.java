package it.unibo.radarSystem22_4.udp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.radarSystem22_4.common.NaiveApplHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.udp.UdpClientSupport;
import it.unibo.radarSystem22_4.comm.udp.UdpServer;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;
import it.unibo.radarSystem22_4.comm.utils.CommSystemConfig;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;


public class TestUdpSupports {
private UdpServer server;
public static final int testPort = 8111; 
	
	@Before
	public void init() {
		CommSystemConfig.tracing = false;
	}
	
	protected void startTheServer(String name) {
		server = new UdpServer(name, testPort, new NaiveApplHandler("naiveH") );
		server.activate();		
	}
	
	protected void stopTheServer() {
		if( server != null ) server.deactivate();		
	}

	
	//@Test 
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
		     BasicUtils.aboutThreads( " | testManyConns  BEFORE - ");  
			 for( int i=1; i<=3;i++) {
				//Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupportsForRequest.testPort);
				String msg = "hello"+i  ;
				System.out.println("testManyConns | forward the msg=" + msg + " on conn:" + conn);	 
				IApplMessage m = CommUtils.buildDispatch("tester", "info", msg, "naiveH");
				conn.forward(m.toString());
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
	    BasicUtils.aboutThreads( " | testManyConns  AFTER - ");  
		ColorsOut.out(" -------------- testManyConns BYE");
	}
	
	//@Test 
	public void testSingleClient() {
		ColorsOut.out(" -------------- testSingleClient");
	    BasicUtils.aboutThreads( " | testSingleClient  BEFORE - ");  
 		startTheServer("oneClientServer");
 		//Create a connection
		new ClientDoingRequest().doWork("client1");		
		stopTheServer();
	    BasicUtils.aboutThreads( " | testSingleClient  AFTER - ");  
		ColorsOut.out(" -------------- testSingleClient BYE");
	}
	
	
	//@Test 
	public void testManyClients() {
		ColorsOut.out(" -------------- testManyClients");
	    BasicUtils.aboutThreads( " | testManyClients  BEFORE - ");  
  		startTheServer("manyClientsServer");
		new ClientDoingRequest().doWork("client1");
		new ClientDoingRequest().doWork("client2");
		new ClientDoingRequest().doWork("client3");
		stopTheServer();
	    BasicUtils.aboutThreads( " | testManyClients  AFTER - ");  
		ColorsOut.out(" -------------- testManyClients BYE");
	}
	
	@Test
	public void testManyRequests() {
		ColorsOut.out(" -------------- testManyRequests");
	    BasicUtils.aboutThreads( " | testManyRequests  START - ");  
		try {
			NaiveApplHandler handler = new NaiveApplHandler("naiveHMod") {
				int i = 0;
				@Override
				public void elaborate(IApplMessage message, Interaction2021 conn) {
					System.out.println(name + " | elaborate " + message + " conn=" + conn);
					BasicUtils.delay(100); //taking some time to reply
					IApplMessage reply = CommUtils.prepareReply(message, "answerTo_"+message)  ;
					this.sendAnswerToClient(reply.toString(), conn);
					i++;
					try {
						if(i%2==0) conn.close(); 
						//even if the connection closes, and even if the current connection has a message in it, 
						//all the requests are processed
					} catch (Exception e) { fail(); }
			  	}
			};
			server = new UdpServer("manyRequestsServer", testPort, handler );
			server.activate();			
		    BasicUtils.aboutThreads( " | testManyRequests  AFTER server - ");  

		    Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupports.testPort);
		    
		    BasicUtils.aboutThreads( " | testManyRequests  AFTER conn - ");  
		
			for(int i=0; i<4; i++) {
				String request = "hello_"+i;
				IApplMessage msg  = CommUtils.buildRequest("tetster", "req", request, "naiveH");
				System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
				conn.forward(msg.toString());
			}
			
		    BasicUtils.aboutThreads( " | testManyRequests After Forward - ");  
			 	
			int i=0;
			for(i=0; i<4; i++) {
				String answer = conn.receiveMsg();
				System.out.println("testManyRequests | receives the answer: " +answer );
			}
			
		    BasicUtils.aboutThreads( " | testManyRequests After Receive - ");  
		    
			assertEquals(4,i);


			BasicUtils.delay(10);
			assertEquals(0,server.getNumConnections()); //if connection has been closed, no connections should be left
			
			String request = "hello_"+i;
			IApplMessage msg  = CommUtils.buildRequest("tetster", "req", request, "naiveH");
			System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
			conn.forward(msg.toString());
			BasicUtils.delay(10);
			assertEquals(1,server.getNumConnections()); //this connection has still not been closed by the server
 			
			stopTheServer();
		    BasicUtils.aboutThreads( " | testManyRequests  END - ");  
			ColorsOut.out(" -------------- testManyRequests BYE");
			
		} catch (Exception e) {
			System.out.println("client1" + " | ERROR " + e.getMessage());
		}
	}
 
	//@Test
	public void testThreads() {
	    BasicUtils.aboutThreads( " | testThreads  START - ");  
	    NaiveApplHandler handler = new NaiveApplHandler("naiveH");
		server = new UdpServer("manyRequestsServer", testPort, handler );
		server.activate();			
	    BasicUtils.aboutThreads( " | testThreads  AFTER SERVER - ");  
		try {
			Interaction2021 conn  = UdpClientSupport.connect("localhost", TestUdpSupports.testPort);
		    BasicUtils.aboutThreads( " | testThreads  AFTER CONN - ");  
			for(int i=1; i<=4; i++) {
				String request = "hello_"+i;
				IApplMessage msg  = CommUtils.buildRequest("tetster", "req", request, "naiveH");
				System.out.println("client1" + " | forward the request=" + request + " on conn:" + conn);
				conn.forward(msg.toString());
			}
		    BasicUtils.aboutThreads( " | testThreads  AFTER Forward - ");  
		} catch (Exception e) {
 			e.printStackTrace();
		}
		server.deactivate();			
	    BasicUtils.aboutThreads( " | testThreads  END - ");  
	}
}
