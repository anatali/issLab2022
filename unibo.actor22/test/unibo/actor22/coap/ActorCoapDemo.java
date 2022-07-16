package unibo.actor22.coap;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.*;
import unibo.actor22comm.SystemData;
import unibo.comm22.utils.CommUtils;

public class ActorCoapDemo extends QakActor22FsmAnnot{
private int n = 0;
	public ActorCoapDemo(String name) {
		super(name);
 	}	
	@State( name = "init", initial=true)
	@Transition( state = "s0"   )	//empty move
	protected void init( IApplMessage msg ) {
		outInfo(""+msg );
 	}
	@State( name = "s0" )
	@Transition( state = "s0" ,      msgId = SystemData.demoSysId, guard="goon"  )
	@Transition( state = "endJob" ,  msgId = SystemData.demoSysId, guard="terminate"  )
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		n++;   //Uncomment to go in s1
		this.updateResourceRep("n="+n);
		CommUtils.delay(1500);
 		autoMsg( SystemData.demoSysCmd( getName(),getName() ) );
	}
	@State( name = "endJob" )
	protected void endJob( IApplMessage msg ) {
		outInfo(""+msg );
		System.exit(0);
	}
	
	@TransitionGuard 
	protected boolean goon() {
		return n < 3;
	}
	
	@TransitionGuard 
	protected boolean terminate() {
		return n == 3;
	}	
}
