package unibo.actor22;

import java.lang.reflect.Method;
import java.util.Vector;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22comm.interfaces.StateActionFun;
import unibo.actor22comm.utils.ColorsOut;
 
 

public abstract class QakActor22FsmAnnot  extends QakActor22Fsm{
protected QakActor22FsmAnnot myself;
protected String initialState = null;

	public QakActor22FsmAnnot(String name) {
		super(name);
		myself = this;
 	}

	@Override
	protected void setTheInitialState(  ) {	}   //No more necessary for annotations
	
	@Override
	protected void declareTheStates( ) {
    	try {
 		  Method[] m = this.getClass().getDeclaredMethods( );
		  //ColorsOut.outappl("method: "+ m.length  , ColorsOut.CYAN);		      
  		  for( int i=0; i<m.length; i++ ) {
  			  m[i].setAccessible(true);
 		      if( m[i].isAnnotationPresent(State.class)) elabAnnotatedMethod(m[i]);	  
  		  }
   		} catch (Exception e) {
  			ColorsOut.outerr("readAnnots ERROR:" + e.getMessage() )   ;	
  		}		
	}	
	protected void setTheInitialState( String stateName ) {
     if( initialState == null  ) {
		initialState= stateName;
		declareAsInitialState( initialState );
	 } else ColorsOut.outerr("Multiple intial states not allowed" );		
	}	
	
	protected void elabStateMethod(Method m, String stateName) {
		if( ! m.getName().equals(stateName)) {
			ColorsOut.outerr(getName() + " | QakActor22FsmAnnot  Method name must be the same as state name" );
		}
		  Vector<String> nextStates = new Vector<String>();
		  Vector<String> msgIds     = new Vector<String>();
		  
		  Transition[] ta        = m.getAnnotationsByType(Transition.class);
 		  
		  for ( Transition t : ta ) {
			  ColorsOut.outappl("Transition simple: "+ t.msgId() + " -> " + t.state(), ColorsOut.CYAN);
			  nextStates.add(t.state());
			  msgIds.add(t.msgId());
		  }
		  //Farlo staticamente NO
		  //Nextstate = controllo della guardia
//		  for ( TransitionGuarded t : tg ) {
//			  ColorsOut.outappl("TransitionGuarded "+ t.name() + " for " + t.msgId()  , ColorsOut.CYAN);
//			  if( guardForTransition(stateName, t.name() ) ) {
//				  nextStates.add(t.stateOk());
//				  msgIds.add(t.msgId());				  
//			  }else {
//				  nextStates.add(t.stateKo());
//				  msgIds.add(t.msgId());				  				  
//			  }
//		  }
// 		  ColorsOut.outappl("nextStates "+ nextStates.size() , ColorsOut.CYAN);
//		  ColorsOut.outappl("msgIds "+ msgIds.size() , ColorsOut.CYAN);
		  doDeclareState(m,stateName,nextStates,msgIds );		
	}
	
	protected boolean guardForTransition(String stateName, String transName ) {
		return false;
	}
	
	protected void doDeclareState(Method curMethod, String stateName, Vector<String> nextStates, Vector<String> msgIds) {
		  declareState( stateName, new StateActionFun() {
				@Override
				public void run( IApplMessage msg ) {
				try {
  					//outInfo("uuuu "+ msg  + " " + this );	
  					curMethod.invoke(  myself, msg   );  //I metodi hanno this come arg implicito
  					for( int j=0; j<nextStates.size();j++ ) {
   						addTransition( nextStates.elementAt(j), msgIds.elementAt(j) );
  					}					
  					nextState();
				} catch ( Exception e) {
						ColorsOut.outerr("wrong execution for:"+ stateName + " - " + e.getMessage());
				}
    			}			        			  
		  });//declareState		
	}
	
	protected void elabAnnotatedMethod(Method m) {
		String functor =  m.getName();		    	 
		Class<?>[] p   =  m.getParameterTypes();
//    		  ColorsOut.outappl("state ANNOT functor="+ functor + " p.length=" + p.length , ColorsOut.CYAN);
//    		  ColorsOut.outappl(""+p[0].getCanonicalName().equals("it.unibo.kactor.IApplMessage") , ColorsOut.CYAN);
		if( p.length==0 || p.length>1 || 
    				  ! p[0].getCanonicalName().equals("it.unibo.kactor.IApplMessage") ) {
    			  ColorsOut.outerr("wrong arguments for state:"+ functor);
		}else {
			State stateAnnot=m.getAnnotation(State.class);
			if( stateAnnot.initial() )  setTheInitialState(stateAnnot.name());
			//ColorsOut.outappl("state ANNOT name= "+ stateAnnot.name() + " initialState=" + initialState, ColorsOut.CYAN);
			elabStateMethod( m, stateAnnot.name());		  
		}
 	}
	

}
