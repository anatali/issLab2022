package it.unibo.comm2022.enablers;

import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.proxy.ProxyAsClient;
import it.unibo.comm2022.tcp.NaiveApplHandler;
import it.unibo.comm2022.utils.ColorsOut;
 
/*
 
 */
public class TestEnablers {
	
 	private EnablerAsServer enabler;	
 	private int port = 8056; 	
 	private ProtocolType protocol = ProtocolType.tcp;
	private ProxyAsClient aproxy;
	
	@Before
	public void setup() { 		
 		//Il server
  	 	enabler = new EnablerAsServer("aSrv",port,protocol, 
  	 			new NaiveApplHandler("naiveH" ) );
  		//Il client
  	 	aproxy = new ProxyAsClient("aPxy", "localhost", ""+port, protocol );		
  
	}

	@After
	public void down() {
		System.out.println("down");		
		enabler.stop();
 	}	
	
	
 	
	@Test 
	public void testEnablers() {
		enabler.start();
		String req    = "aRequest";
		String answer = aproxy.sendRequestOnConnection(req);
		ColorsOut.out(answer, ColorsOut.MAGENTA);
		assertTrue( answer.equals("answerTo_"+ req));
		
 	}
}
