package unibo.actor22.guards;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.SystemData;

public class TestGuards extends QakActor22FsmAnnot{
private int n = 0;

	public TestGuards(String name) {
		super(name);
 	}
 	
	@State( name = "s0", initial=true)
	@Transition( state = "s1" ,  msgId = SystemData.demoSysId, guard = Guard0.class )
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		//n++;
		Guard0.setValue(n);
		this.autoMsg( SystemData.demoSysCmd( getName(),getName() ) );
	}
 
	@State( name = "s1" )
	protected void s1( IApplMessage msg ) {
		outInfo(""+msg );
		System.exit(0);
	}

}
