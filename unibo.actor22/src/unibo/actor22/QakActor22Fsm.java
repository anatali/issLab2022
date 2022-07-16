package unibo.actor22;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import it.unibo.kactor.IApplMessage;
import kotlin.Pair;
import unibo.actor22comm.SystemData;
import unibo.actor22.interfaces.StateActionFun;
import unibo.comm22.utils.ColorsOut;
 

public abstract class QakActor22Fsm extends QakActor22 {  
	private HashMap<String,StateActionFun> stateMap = new HashMap<String,StateActionFun>();
	protected HashMap<String,String> nextMsgMap       = new HashMap<String,String>();
	protected Vector<IApplMessage>  OldMsgQueue       = new Vector< IApplMessage>();
	protected Vector< Pair<String, String> > transTab     = new Vector< Pair<String, String> >();
	protected Vector< Pair<String, Boolean> >interruptTab = new Vector< Pair<String, Boolean> >();

	
	private String curState = "";
	protected IApplMessage currentMsg = null;
  	
	public QakActor22Fsm(String name) {
		super(name);
		declareTheStates( );
		setTheInitialState( );
		//Auto invia il messaggio di  inizio che porta nello stato iniziale
		addExpectedMsg(curState, SystemData.startSysCmdId );
		ColorsOut.outappl(getName() + " | autoMsg ApplData.startSysCmd", ColorsOut.GREEN);
		autoMsg(SystemData.startSysCmd("system",name));
	}
 	
 	
	protected abstract void declareTheStates( );
	protected abstract void setTheInitialState( );
	
	protected void declareAsInitialState( String stateName ) {
		ColorsOut.out( getName() + " QakActor22Fsm | declareAsInitialState " + stateName, ColorsOut.BLUE);		
		curState = stateName;
	};
	
	protected void declareState(String stateName, StateActionFun action) {
		ColorsOut.out( getName() + " QakActor22Fsm | declareState " + stateName + " action=" + action, ColorsOut.BLUE);		
		if( action == null ) ColorsOut.outerr(getName() + " QakActor22Fsm | action null");
		else stateMap.put( stateName, action );
	}
	
	protected void addTransition(String state, String msgId, boolean withInterrupt) {
		ColorsOut.out( getName() + " QakActor22Fsm | in " + curState + ": transition to " + state + " for " +  msgId, ColorsOut.BLUE);		
		transTab.add(     new Pair<>(state, msgId) );
		interruptTab.add( new Pair<>(state, withInterrupt) );
//		if( msgId.equals("emptyMove")) {//Per info
//			ColorsOut.out( getName() + " QakActor22Fsm | in " + curState +	" adding an empty move" , ColorsOut.BLUE );		
//		}
	}
	
	protected String stateWithInterrupt = "";
	protected Vector< Pair<String, String> >  memoTransTab     = null;
	protected Vector< Pair<String, Boolean> > memoInterruptTab = null;
	
	protected void memoTheState(String currentState) {
		ColorsOut.out(getName() + " | QakActor22Fsm memoTheState " +  currentState, ColorsOut.GREEN);
		stateWithInterrupt = currentState;
		memoTransTab     = transTabCopy(transTab);     
		memoInterruptTab = interruptTabCopy(interruptTab);     
	}
	
	protected Vector<Pair<String,String>> transTabCopy( Vector<Pair<String,String>> tab){
		Vector< Pair<String, String> > copied = new Vector<Pair<String,String>>();
		Iterator< Pair<String, String> >  iter  = tab.iterator();
		while( iter.hasNext() ) {
			copied.add( iter.next() );
		}
		return copied;
	}
	protected Vector<Pair<String,Boolean>> interruptTabCopy( Vector<Pair<String,Boolean>> tab){
		Vector< Pair<String, Boolean> > copied = new Vector<Pair<String,Boolean>>();
		Iterator< Pair<String, Boolean> >  iter  = tab.iterator();
		while( iter.hasNext() ) {
			copied.add( iter.next() );
		}
		return copied;
	}
 	
	protected Vector<Pair<String,String>> tabRestore( Vector<Pair<String,String>> tab){
		Vector< Pair<String, String> > copied = new Vector<Pair<String,String>>();
		transTab = new Vector<Pair<String,String>>();
		Iterator< Pair<String, String> >  iter  = tab.iterator();
		while( iter.hasNext() ) {
			copied.add( iter.next() );
 		}
		return copied;
	}
	protected Vector<Pair<String,Boolean>> interruptTabRestore( Vector<Pair<String,Boolean>> tab){
		Vector< Pair<String, Boolean> > copied = new Vector<Pair<String,Boolean>>();
		interruptTab = new Vector<Pair<String,Boolean>>();
		Iterator< Pair<String, Boolean> >  iter  = tab.iterator();
		while( iter.hasNext() ) {
			copied.add( iter.next() );
		}
		return copied;
	}	
	protected void resume() {  
		ColorsOut.out(getName() + " | QakActor22Fsm in " + curState + " resume:" + memoTransTab.size(), ColorsOut.GREEN);		 
		transTab           = tabRestore(memoTransTab);
		interruptTab       = interruptTabRestore(memoInterruptTab);  
		curState           = "resuming";
	}
	protected void nextState(String currentState ) { //Called by  QakActor22FsmAnnot at 99
 		clearExpectedMsgs();
 		Iterator< Pair<String, String> >  iterOnTrans  = transTab.iterator();
		Iterator< Pair<String, Boolean> > iterOnIntrpt = interruptTab.iterator();
		boolean hasInterruptTransition = false; 
		
		while( iterOnTrans.hasNext() ) {
			Pair<String, String> curTrans   = iterOnTrans.next();	
			Pair<String, Boolean> curIntrpt = iterOnIntrpt.next();
			
			String nextState         		= curTrans.getFirst();
			String msgId            		= curTrans.getSecond();
			boolean withInterrupt    		= curIntrpt.getSecond();
			
			if( msgId.equals(SystemData.emptyMoveId) ) {  
				stateTransition(nextState, SystemData.emptyMoveCmd(getName(),getName() ));
				return;
			}
			if( withInterrupt && ! hasInterruptTransition && ! curState.equals("resuming")  ) { //
				memoTheState(currentState);
				hasInterruptTransition = true;
			}
			 
			IApplMessage oldMsg = searchInOldMsgQueue( msgId );
			if( oldMsg != null ) {
				stateTransition(nextState,oldMsg);
				break;
			}
			else addExpectedMsg(nextState, msgId);
		}
	}
	protected void stateTransition(String stateName, IApplMessage msg ) {
		curState   = stateName;
		currentMsg = msg;
		transTab.removeAllElements();
		interruptTab.removeAllElements();
		stateWithInterrupt = null;
		StateActionFun a = stateMap.get(curState);
		if( a != null ) {
			a.run( msg );
		}
		else ColorsOut.outerr(getName() + " | QakActor22Fsm TERMINATED since no body");
	}	
	protected void addExpectedMsg(String state, String msgId) {
		nextMsgMap.put(msgId, state);		
	}
	protected void clearExpectedMsgs( ) {
		nextMsgMap.clear();		
	}	
	protected String checkIfExpected(IApplMessage msg) {
		return nextMsgMap.get( msg.msgId() );
	}
	
 	
	@Override
	protected void handleMsg(IApplMessage msg) {
		//ColorsOut.out(getName() + " | QakActor22Fsm handleMsg " +  msg, ColorsOut.GREEN);
		//currentMsg = msg;
		String state = checkIfExpected(msg);
		if ( state != null ) stateTransition(state,msg);
		else memoTheMessage(msg);
	}
	
	protected void memoTheMessage(IApplMessage msg) {
		ColorsOut.outappl(getName() + " | QakActor22Fsm in " + this.curState + " handleMsg not yet:" +  msg, ColorsOut.YELLOW);
		OldMsgQueue.add(msg);	
		currentMsg=null;
	}

 
	protected IApplMessage searchInOldMsgQueue(String msgId) {
 		Iterator<IApplMessage> iter = OldMsgQueue.iterator();
		while( iter.hasNext() ) {
			IApplMessage msg = iter.next();
			if( msg.msgId().equals(msgId)) {
				OldMsgQueue.remove(msg);
				return msg;
			}			
		}
		return null;
 	}

	protected void outInfo(String info) {
		ColorsOut.outappl(getName() + "/" + curState + " | " + info, ColorsOut.BLACK);
	}
}
