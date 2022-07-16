package unibo.actor22.timer;

import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.TimerActor;
import unibo.actor22.Qak22Context;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22.guards.Guard0;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.Timer22;

public class TestTimerActor extends QakActor22FsmAnnot{

	private static final String timeOutEventId = "tout_a1";
	
	public TestTimerActor(String name) {
		super(name); 
 	}
	
	@State( name = "init", initial=true)
	@Transition( state = "s0", msgId= timeOutEventId  )	 
	@Transition( state = "s0", msgId = SystemData.startSysCmdId )
	protected void init( IApplMessage msg ) {
		outInfo(""+msg );
		Qak22Context.registerAsEventObserver(getName(), timeOutEventId);
 		new Timer22(2000, timeOutEventId);
 	}

	@State( name = "s0" )
	@Transition( state = "s0", msgId= timeOutEventId )	 
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		Qak22Context.unregisterAsEventObserver(getName(), timeOutEventId);
		//System.exit(0);
 	}

}
