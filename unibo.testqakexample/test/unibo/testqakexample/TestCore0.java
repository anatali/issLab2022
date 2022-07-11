package unibo.testqakexample;
import static org.junit.Assert.assertTrue;


import it.unibo.ctxwasteservice.MainCtxwasteserviceKt;
import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.QakContext;
import org.eclipse.californium.core.CoapHandler;
import org.junit.*;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommSystemConfig;
import unibo.comm22.utils.CommUtils;


public class TestCore0 {
private CoapConnection conn;

	@Before
	public void up() {
		CommSystemConfig.tracing=false;

		startObserverCoap("localhost", new TrolleyPosObserver());

		new Thread(){
			public void run(){
				MainCtxwasteserviceKt.main();
			}
		}.start();
		waitForApplStarted();
  	}

 	protected void waitForApplStarted(){
		ActorBasic wasteservice = QakContext.Companion.getActor("wasteservice");
		while( wasteservice == null ){
			ColorsOut.outappl("TestCore0 waits for appl ... " , ColorsOut.GREEN);
			CommUtils.delay(200);
			wasteservice = QakContext.Companion.getActor("wasteservice");
		}

	}
	@After
	public void down() {
		ColorsOut.outappl("TestCore0 ENDS" , ColorsOut.BLUE);
	}

	@Test
	public void testLoadok() {
		ColorsOut.outappl("testLoadok STARTS" , ColorsOut.BLUE);
		assertTrue( coapCheck("home") );
		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(glass,200),1)";
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8013);
			String answer     = connTcp.request(truckRequestStr);
 			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			connTcp.close();
			assertTrue(answer.contains("loadaccept"));
			//TODO: problema dei tempi
			assertTrue( coapCheck("indoor") );
			CommUtils.delay(1000);
			assertTrue( coapCheck("gbox") );
			//TODO: controllare la history
			//CommUtils.delay(3000);
		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());

		}
 	}

//---------------------------------------------------

protected boolean coapCheck(String check){
	String answer = conn.request("");
	ColorsOut.outappl("coapCheck answer=" + answer, ColorsOut.CYAN);
	return answer.contains(check);
}
protected void startObserverCoap(String addr, CoapHandler handler){
		new Thread(){
			public void run(){
				try {
					String ctxqakdest       = "ctxwasteservice";
					String qakdestination 	= "wasteservice";
					String applPort         = "8013";
					String path             = ctxqakdest+"/"+qakdestination;
					conn                    = new CoapConnection(addr+":"+applPort, path);
					conn.observeResource( handler );
					ColorsOut.outappl("connected via Coap conn:" + conn , ColorsOut.CYAN);
				}catch(Exception e){
					ColorsOut.outerr("connectUsingCoap ERROR:"+e.getMessage());
				}
			}
		}.start();

}
}