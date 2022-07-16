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
 	
	@State( name = "init", initial=true)
	@Transition( state = "s0"   )	//empty move
	protected void init( IApplMessage msg ) {
		outInfo(""+msg );
 	}

	@State( name = "s0" )
	@Transition( state = "s1" ,  msgId = SystemData.demoSysId, guard = "guard0" )
	protected void s0( IApplMessage msg ) {
		outInfo(""+msg );
		n++;   //Uncomment to go in s1
		Guard0.setValue(n);
		this.autoMsg( SystemData.demoSysCmd( getName(),getName() ) );
	}

	
	@State( name = "s1" )
	@Transition( state = "s3" ,  guard = "noGuard0" )  //emptyMove con Guardia
	@Transition( state = "s2" ,  guard = "guard0" )  //emptyMove con Guardia
	protected void s1( IApplMessage msg ) {
		outInfo(""+msg );
	}
	@State( name = "s2" )
	protected void s2( IApplMessage msg ) {
		outInfo(""+msg );
		System.exit(0);
	}
	@State( name = "s3" )
	protected void s3( IApplMessage msg ) {
		outInfo(""+msg );
		System.exit(0);
	}
//-------------------------------------------------------	
	@TransitionGuard
	protected boolean guard0() {
		outInfo("guard0 "+ (n > 0) );
		return n > 0;
	}
	
	@TransitionGuard
	protected boolean noGuard0() {
		outInfo("noGuard0 "+ (n == 0) );
		return n == 0;
	}	
}
