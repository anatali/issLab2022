package it.unibo.comm2022.tcp;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.proxy.ProxyAsClient;
import it.unibo.comm2022.utils.ColorsOut;

public class TestProxyTcp {
public static final int testPort = 8112; 
	private TcpServer server;

	@Before
	public void up() {
		System.out.println(" =============== ACTIVATING SERVER  " );
		server = new TcpServer("tcpServer",testPort, new NaiveApplHandler("naiveH") );		
		server.activate();		
		
		
	}
	
	@After
	public void down() {
		if( server != null ) server.deactivate();
		System.out.println(" =============== SERVER DEACTIVATED" );
	}	

	@Test
	public void useProxy() {
		ProxyAsClient pxy = new ProxyAsClient("pxy", "localhost", ""+testPort, ProtocolType.tcp);
		//pxy.sendCommandOnConnection("hello");
		String req    = "arequest";
		String answer = pxy.sendRequestOnConnection( req );
		ColorsOut.out(answer, ColorsOut.MAGENTA);
		assertTrue( answer.equals("answerTo_"+ req));
		//BasicUtils.delay(1000);
	}
}
