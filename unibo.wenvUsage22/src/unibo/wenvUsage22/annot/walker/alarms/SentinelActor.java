package unibo.wenvUsage22.annot.walker.alarms;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.State;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;
import unibo.actor22comm.utils.ColorsOut;

public class SentinelActor extends QakActor22FsmAnnot {

	public SentinelActor(String name) {
		super(name);
		autoMsg( SystemData.activateActor( name, name ) );
 	}

	@State( name = "sentinelStart", initial=true)
	public void sentinelStart(IApplMessage msg) {
		outInfo(""+msg);
		//CommUtils.delay( 3000 );  //NO
		new Thread() {
			public void run() {
				//CommUtils.delay( 1000 );
				ColorsOut.outappl( "QakActor22 | emit=" + SystemData.fireEvent(  )  , ColorsOut.GREEN);
				Qak22Util.emitEvent( SystemData.fireEvent(  ) );
				CommUtils.delay( 2000 );
				ColorsOut.outappl( "QakActor22 | emit=" + SystemData.endAlarm(  )  , ColorsOut.GREEN);
				Qak22Util.emitEvent( SystemData.endAlarm(  ) );
			}
		}.start();
	}
}
