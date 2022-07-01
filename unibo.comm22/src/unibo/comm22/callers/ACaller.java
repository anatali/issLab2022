package unibo.comm22.callers;

import it.unibo.kactor.IApplMessage;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;

public class ACaller {

	protected void callUsingTcp() {
		ColorsOut.outappl("ACaller callUsingTcp" , ColorsOut.BLUE);
		ConnQakTcp support   = new ConnQakTcp();
		Interaction2021 conn = support.createConnection("localhost", 8005);
		try {
			ColorsOut.outappl("forward ..." , ColorsOut.BLUE);		
			IApplMessage m1 = CommUtils.buildDispatch("acaller", "msg1", "msg1(1)", "demominimal");
			conn.forward(m1.toString());

			IApplMessage m3 = CommUtils.buildEvent("acaller", "alarm", "alarm(fire)" );		
			ColorsOut.outappl("event ... " + m3, ColorsOut.BLUE);		
			conn.forward(m3.toString());
			
			
			
			IApplMessage m2 = CommUtils.buildRequest("acaller", "msg2", "msg2(1)", "demominimal");
			ColorsOut.outappl("request ... " + m2, ColorsOut.BLUE);		
			String answer = conn.request(m2.toString());
			ColorsOut.outappl("answer="+ answer, ColorsOut.BLUE);		
		} catch (Exception e) {
 			e.printStackTrace();
		}
	}
	public void doJob() {
		callUsingTcp();
	}
	public static void main( String[] args) {
		ACaller appl = new ACaller();
		appl.doJob();
	}
}
