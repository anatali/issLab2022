package unibo.testqakexample;
import static org.junit.Assert.assertTrue;


import it.unibo.ctxWasteService.MainCtxWasteServiceKt;
import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.QakContext;
import org.junit.*;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;

public class TestCore0 {

	@Before
	public void up() {
		new Thread(){
			public void run(){
				MainCtxWasteServiceKt.main();
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
 		String truckRequestStr = "msg(depositrequest, request,python,wasteservice,depositrequest(glass,200),1)";
		try{
			ConnTcp connTcp   = new ConnTcp("localhost", 8013);
			String answer     = connTcp.request(truckRequestStr);
 			ColorsOut.outappl("testLoadok answer=" + answer , ColorsOut.GREEN);
			connTcp.close();
			assertTrue(answer.contains("loadaccept"));
		}catch(Exception e){
			ColorsOut.outerr("testLoadok ERROR:" + e.getMessage());

		}
 	}
}