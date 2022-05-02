package unibo.actor22.coap;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.SystemData;

public class TestCoap extends QakActor22FsmAnnot{
private int n = 0;

	public TestCoap(String name) {
		super(name);
 	}
 	
	@State( name = "init", initial=true)
	@Transition( state = "s0"   )	//empty move
	protected void init( IApplMessage msg ) {
		outInfo(""+msg );
 	}

	@State( name = "s0" )
	@Transition( state = "s1" ,  msgId = SystemData.demoSysId  )
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		n++;   //Uncomment to go in s1
		this.updateResourceRep("n="+n);
 		this.autoMsg( SystemData.demoSysCmd( getName(),getName() ) );
	}

	
	@State( name = "s1" )
	protected void s1( IApplMessage msg ) {
		outInfo(""+msg );
		//System.exit(0);
	}

}
