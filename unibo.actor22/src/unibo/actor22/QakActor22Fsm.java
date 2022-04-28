package unibo.actor22;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import it.unibo.kactor.IApplMessage;
import kotlin.Pair;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.StateActionFun;
import unibo.actor22comm.utils.ColorsOut;
 

public abstract class QakActor22Fsm extends QakActor22 {  
	private HashMap<String,StateActionFun> stateMap = new HashMap<String,StateActionFun>();
	protected HashMap<String,String> nextMsgMap       = new HashMap<String,String>();
	protected Vector<IApplMessage>  OldMsgQueue       = new Vector< IApplMessage>();
	protected Vector< Pair<String, String> > transTab = new Vector< Pair<String, String> >();

	
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
		ColorsOut.outappl( getName() + " QakActor22Fsm | declareAsInitialState " + stateName, ColorsOut.BLUE);		
		curState = stateName;
	};
	
	protected void declareState(String stateName, StateActionFun action) {
		ColorsOut.outappl( getName() + " QakActor22Fsm | declareState " + stateName + " action=" + action, ColorsOut.BLUE);		
		if( action == null ) ColorsOut.outerr(getName() + " QakActor22Fsm | action null");
		else stateMap.put( stateName, action );
	}
	
	protected void addTransition(String state, String msgId) {
		ColorsOut.out( getName() + " QakActor22Fsm | in " + curState + ": transition to " + state + " for " +  msgId, ColorsOut.BLUE);		
		transTab.add( new Pair<>(state, msgId) );
		if( msgId == null) {
			ColorsOut.out( getName() + " QakActor22Fsm | in " + curState +	" adding an empty move" , ColorsOut.BLUE );		
		}
	}
	
	
	protected void nextState() {
		clearExpectedMsgs();
		Iterator< Pair<String, String> > iter = transTab.iterator();
		while( iter.hasNext() ) {
			Pair<String, String> v = iter.next();
			
			String state = v.getFirst();
			String msgId = v.getSecond();
			if( msgId == null ) { //Check the empty move
				autoMsg(SystemData.emptyMoveCmd(getName(),getName() ));
				stateTransition(state, null);
				return;
			}
			 
			IApplMessage oldMsg = searchInOldMsgQueue( msgId );
			if( oldMsg != null ) {
				stateTransition(state,oldMsg);
				break;
			}
			else  addExpectedMsg(state, msgId);
		}
	}
	protected void stateTransition(String stateName, IApplMessage msg ) {
		curState   = stateName;
		currentMsg = msg;
		transTab.removeAllElements();
		StateActionFun a = stateMap.get(curState);
		if( a != null ) a.run( msg );
		else ColorsOut.outerr(getName() + " | QakActor22Fsm TERMINATED");
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
		ColorsOut.out(getName() + " | QakActor22Fsm handleMsg " +  msg, ColorsOut.GREEN);
		//currentMsg = msg;
		String state = checkIfExpected(msg);
		if ( state != null ) stateTransition(state,msg);
		else memoTheMessage(msg);
	}
	
	protected void memoTheMessage(IApplMessage msg) {
		ColorsOut.outappl(getName() + " | QakActor22Fsm handleMsg not yet:" +  msg, ColorsOut.YELLOW);
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
