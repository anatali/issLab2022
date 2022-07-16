package unibo.actor22.interrupts;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.SystemData;
import unibo.comm22.utils.CommUtils;

public class TestInterrupts extends QakActor22FsmAnnot{
private  int n = 0;
private  int limit = 3 ;

	public TestInterrupts(String name) {
		super(name);
 	}
 	
	@State( name = "init", initial=true)
	@Transition( state = "s0", msgId = SystemData.startSysCmdId    )	 
	protected void init( IApplMessage msg ) {
		//Guard0.limit =
		outInfo(""+msg );
 	}

	@State( name = "s0" )
	@Transition( state = "s1_interrupted",   msgId= SystemData.stopSysCmdId, interrupt = true  )
	@Transition( state = "s1" ,  msgId = SystemData.demoSysId )
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		autoMsg(SystemData.demoSysCmd(getName(),getName() ));
	}

	
	@State( name = "s1" )
	@Transition( state = "s0" ,  msgId = SystemData.demoSysId, guard = "guardLimit")
	@Transition( state = "s1" ,  msgId = SystemData.haltSysCmdId )
	@Transition( state = "s1_interrupted" ,  msgId = SystemData.stopSysCmdId, interrupt = true )
 	protected void s1( IApplMessage msg ) {
		outInfo(""+msg );
		Guard1.setValue(n++);
		CommUtils.delay(200);   
		autoMsg(SystemData.demoSysCmd(getName(),getName() ));
		 
	}
	
	//Stato di risposta a interruzione. Al termine faccio resume e torno alle stesse
	//transizioni di s1, così gestisco demoSysId dopo avere fatto s1_interrupted
	@State( name = "s1_interrupted" )
	protected void end( IApplMessage msg ) {
		outInfo(""+msg + " stateWithInterrupt=" +  stateWithInterrupt  );
		//RESUME: faccio le addTransition che avrebbe fatto s1 senza la parte di interrupt
		resume();
	}

	//-------------------------------------------------------	
		@TransitionGuard
		protected boolean guardLimit() {
			return n < limit;
		}	
}
