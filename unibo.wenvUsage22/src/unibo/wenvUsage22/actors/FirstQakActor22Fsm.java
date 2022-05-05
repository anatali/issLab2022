package unibo.wenvUsage22.actors;
import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.StateActionFun;
import unibo.actor22comm.utils.ColorsOut;
import unibo.wenvUsage22.common.ApplData;


public  class FirstQakActor22Fsm extends QakActor22Fsm {
 	
	public FirstQakActor22Fsm(String name) {
		super(name);
 	}
	
   	
 	  
	//invocata nel costruttore, prima del setting dei metodi locali della sottoclasse
	@Override
	protected void declareTheStates( ) {  
		
		StateActionFun s0State = new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo( ""+msg );
				addTransition( "s1", ApplData.moveCmdId );
				nextState();
			}			
		};	
		
		
		declareState( "s0", s0State);
		declareState("s1", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
				addTransition( "s1", ApplData.moveCmdId );
				addTransition( "s2", ApplData.haltSysCmdId );
				nextState();
			}			
		});
		declareState("s2", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);
				outInfo("BYE" );
				System.exit(0);
  			}			
		});
	}

 
	
	@Override
	protected void setTheInitialState( ) {
		declareAsInitialState( "s0" );
	}

 
}
