package unibo.wenvUsage22.annot.walker.alarms;

 
import unibo.actor22.Qak22Util;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.utils.ColorsOut;

public class Sentinel extends Thread {

 
	@Override 
	public void run() { 
		for( int i = 1; i<=3; i++) {
			CommUtils.delay( 1500 );  //Si blocca su wallDown
			ColorsOut.outappl( "Sentinel | emit=" + SystemData.fireEvent(  )  , ColorsOut.BLUE);
			Qak22Util.emitEvent( SystemData.fireEvent(  ) );
			CommUtils.delay( 2000 );
			ColorsOut.outappl( "Sentinel | emit=" + SystemData.endAlarm(  )  , ColorsOut.BLUE);
			Qak22Util.emitEvent( SystemData.endAlarm(  ) );
		}
	}

}
