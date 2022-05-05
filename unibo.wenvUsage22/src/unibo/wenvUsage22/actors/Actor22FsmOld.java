package unibo.wenvUsage22.actors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import it.unibo.kactor.IApplMessage;
import kotlin.Pair;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22comm.interfaces.StateActionFun;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;


public  class Actor22FsmOld extends QakActor22{
	private HashMap<String,StateActionFun> stateMap = new HashMap<String,StateActionFun>();
	private HashMap<String,String> nextMsgMap       = new HashMap<String,String>();
	private Vector<IApplMessage>  OldMsgQueue       = new Vector< IApplMessage>();
	private Vector< Pair<String, String> > transTab = new Vector< Pair<String, String> >();

	private  String curState = "";
	private IApplMessage startCmd, moveCmd, haltCmd;
 	
	public Actor22FsmOld(String name) {
		super(name);
		startCmd = CommUtils.buildDispatch("main", "activate", "activate",name );
		moveCmd  = CommUtils.buildDispatch("main", "move", "100",name );
		haltCmd  = CommUtils.buildDispatch("main", "halt", "100",name );
		initStateMap( );
	}
 	
	

	 
	protected void initStateMap( ) {
		stateMap.put("s0", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
				addTransition( "s1", moveCmd.msgId() );
				nextState();
			}			
		});
		stateMap.put("s1", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);	
				addTransition( "s1", moveCmd.msgId() );
				addTransition( "s3", haltCmd.msgId() );
				nextState();
			}			
		});
		stateMap.put("s3", new StateActionFun() {
			@Override
			public void run(IApplMessage msg) {
				outInfo(""+msg);
				outInfo("BYE" );
				addTransition( "s3", haltCmd.msgId() );
  			}			
		});
		curState = "s0";
		addExpecetdMsg(curState, startCmd.msgId());
	}
	
	protected void addTransition(String state, String msgId) {
		ColorsOut.outappl( getName() + " in " + curState + " | transition to " + state + " for " +  msgId, ColorsOut.BLUE);		
		transTab.add( new Pair<>(state, msgId) );
	}
	
	
	protected void nextState() {
		clearExpectedMsgs();
		Iterator< Pair<String, String> > iter = transTab.iterator();
		while( iter.hasNext() ) {
			Pair<String, String> v = iter.next();
			String state = v.getFirst();
			String msgId = v.getSecond();
			IApplMessage oldMsg = searchInOldMsgQueue( msgId );
			if( oldMsg != null ) {
				stateTransition(state,oldMsg);
				break;
			}
			else  addExpecetdMsg(state, msgId);
		}
	}
	protected void stateTransition(String stateName, IApplMessage msg ) {
		curState = stateName;
		transTab.removeAllElements();
		StateActionFun a = stateMap.get(stateName);
		a.run( msg );			
	}	
	protected void addExpecetdMsg(String state, String msgId) {
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
		ColorsOut.outappl(getName() + " | handleMsg " +  msg, ColorsOut.GREEN);
		String state = checkIfExpected(msg);
		if ( state != null ) stateTransition(state,msg);
		else memoTheMessage(msg);
	}
	
	protected void memoTheMessage(IApplMessage msg) {
		ColorsOut.outappl(getName() + " | handleMsg not yet:" +  msg, ColorsOut.YELLOW);
		OldMsgQueue.add(msg);		
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
		ColorsOut.outappl(curState + " | " + info, ColorsOut.BLACK);
	}
	
	
	
	public void doJob() {
		CommSystemConfig.tracing = false;
		//new EnablerContextForActors( "ctx",8030,ProtocolType.tcp).activate();
		Qak22Context.showActorNames();
		CommUtils.delay(1000);
		//Qak22Util.sendAMsg( startCmd );
		Qak22Util.sendAMsg( moveCmd );
		//Qak22Util.sendAMsg( haltCmd );
		Qak22Util.sendAMsg( moveCmd );
		Qak22Util.sendAMsg( haltCmd );
		Qak22Util.sendAMsg( startCmd );
		CommUtils.delay(2000);
	}
	
	public static void main( String[] args) {
		CommUtils.aboutThreads("Before start - ");
		new Actor22FsmOld("a1").doJob();
		CommUtils.aboutThreads("Before end - ");
		
	}
	
}
