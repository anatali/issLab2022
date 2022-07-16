package unibo.actor22;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22.annotations.TransitionGuard;
import unibo.actor22.interfaces.StateActionFun;
import unibo.comm22.utils.ColorsOut;
 
 

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
 		  Method[] methods  = this.getClass().getDeclaredMethods( );
 		  //ColorsOut.outappl("method: "+ m.length  , ColorsOut.CYAN);		
 		  
 		 Method[] guards = Arrays.stream(methods)
				.filter(m -> m.isAnnotationPresent(TransitionGuard.class))
                .toArray(Method[]::new); 		
 		 
 		 
 		 List<Method> annotatedMethods = new ArrayList<>( methods.length  ); //  /2 ???
 		 
 		 for (Method method : methods) {
  			  method.setAccessible(true);	//anche per ogni guardia-Lenzi
  			  if (method.isAnnotationPresent(State.class)) annotatedMethods.add(method);  			  
 		      //if( methods [i].isAnnotationPresent(State.class)) elabAnnotatedMethod(methods [i]);	  
  		 }
 		 for (Method method : annotatedMethods) elabAnnotatedMethod(method, guards);
  		  
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
	
    protected Method getGuard(Method[] guards, String guardName) { //From Lenzi
        return Arrays.stream(guards)
                .filter(m -> m.getName().equals(guardName))
                .findFirst()
                .orElse(null);
    }
    
	
	protected void elabStateMethod(Method m, String stateName, Method[] allguards) { //Guards by Lenzi
		if( ! m.getName().equals(stateName)) {
			ColorsOut.outerr(getName() + " | QakActor22FsmAnnot  Method name must be the same as state name" );
		}
		  Vector<String> nextStates = new Vector<String>();
		  Vector<String> msgIds     = new Vector<String>();
		  //Vector<Class> guards      = new Vector<Class>();
		  Vector<Boolean> interrupts= new Vector<Boolean>();
		  Vector<Method> guards     = new Vector<Method>(); //From Lenzi
		  
		  Transition[] ta        = m.getAnnotationsByType(Transition.class);
 		  
		  for ( Transition t : ta ) {
			  ColorsOut.outappl("Transition simple: "+ t.msgId() + " -> " + t.state() + " guard=" + t.guard(), ColorsOut.CYAN);
			  nextStates.add(t.state());
			  msgIds.add(t.msgId());
			  interrupts.add(t.interrupt());
			  
			  String guardName = t.guard();
			  if ( guardName.isEmpty() ) guards.add(null);
			  else {
				  Method guard = getGuard(allguards, guardName);
	                if (guard == null) {
	                    throw new IllegalArgumentException("Non existent guard:" + guardName);
	                }	
	                guards.add( guard );
	          }
 		  }
// 		  ColorsOut.outappl("nextStates "+ nextStates.size() , ColorsOut.CYAN);
//		  ColorsOut.outappl("msgIds "+ msgIds.size() , ColorsOut.CYAN);
		  doDeclareState(this, m,stateName,nextStates,msgIds,guards,interrupts );	
		   
	}
	
	protected boolean guardForTransition(String stateName, String transName ) {
		return false;
	}
	
	protected void doDeclareState( QakActor22FsmAnnot castMyself,	//Since Lenzi guards as methods
			Method curMethod, String stateName, Vector<String> nextStates, 
			Vector<String> msgIds, Vector<Method> guards, Vector<Boolean> interrupts) {
		  declareState( stateName, new StateActionFun() {
				@Override
				public void run( IApplMessage msg ) {
				try {
  					//Esegue il body  					
					curMethod.invoke(  castMyself, msg   );  //I metodi hanno this come arg implicito
  					
  					boolean stateWithInterrupt=false;

  					for( int j=0; j<nextStates.size();j++ ) {
  						Method  g   =  guards.elementAt(j);
   						boolean hasInterrupt = interrupts.elementAt(j);
  						if( hasInterrupt  ) {  							
  							if( ! stateWithInterrupt  ) stateWithInterrupt = true;  
  	  						else {ColorsOut.outerr(stateName + ": multiple interrupt not allowed");}
 						}
  						//Object og = g.newInstance();  //Guard as class: deprecated
   						//Boolean result = (Boolean) g.getMethod("eval").invoke( og );
  						Boolean result = (g != null) ? (Boolean) g.invoke(castMyself) : true;
  						
 						if( result ) {
							//ColorsOut.outappl("g:"+ g + " result=" + result.getClass().getName(), ColorsOut.GREEN);
 							addTransition( nextStates.elementAt(j), msgIds.elementAt(j), stateWithInterrupt );
  						}
  					}					
  					nextState(stateName );
				} catch ( Exception e) {
						ColorsOut.outerr("wrong execution for:"+ stateName + " - " + e.getMessage());
				}
    			}			        			  
		  });//declareState		
	}
	
	protected void elabAnnotatedMethod(Method m, Method[] guards) { //guards by Lenzi
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
			elabStateMethod( m, stateAnnot.name(), guards);		  
		}
 	}
	

}
