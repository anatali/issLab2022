package unibo.wenvUsage22.actors.fsm;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
 
 
import unibo.wenvUsage22.common.ApplData;

public class ActorFsm1 extends QakActor22FsmAnnot{
private int n = 0;

	public ActorFsm1(String name) {
		super(name);
 	}

	@State( name = "s0", initial=true)
 	protected void s0( IApplMessage msg ) {
		outInfo(""+msg);
		n++;
		addTransition( "s1", ApplData.resumeCmd , false);
		this.autoMsg( ApplData.resumeCmd("a1", "a1"));
  	}
 
	@State( name = "s1" )
 	protected void s1( IApplMessage msg ) {
		outInfo("n="+n + " " + msg);
		if( n < 2 ) {
			addTransition( "s0", ApplData.resumeCmd, false );
		}else {
			addTransition( "s2", ApplData.resumeCmd, false );
		}
		this.autoMsg( ApplData.resumeCmd("a1", "a1"));
  	}
	
	@State( name = "s2" )
 	protected void s2( IApplMessage msg ) {
		outInfo("BYE:"+msg);
	}
}
