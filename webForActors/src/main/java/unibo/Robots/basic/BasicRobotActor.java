package unibo.Robots.basic;

/*
 * BasicRobot riceve comandi aril da NaiveGui
 * Comando aril: 
 */

import it.unibo.kactor.IApplMessage;
import org.json.JSONObject;
import org.springframework.web.socket.TextMessage;
import unibo.Robots.common.ActorObserver;
import unibo.actor22.QakActor22FsmAnnot;
import unibo.actor22.annotations.State;
import unibo.actor22.annotations.Transition;
import unibo.actor22comm.SystemData;
import unibo.webForActors.WebSocketConfiguration;


public class BasicRobotActor extends QakActor22FsmAnnot{
	
	
protected 	BasicRobotAdapter robotAdapter;

 	
	public BasicRobotActor(String name) {
		super(name);
 		//ActorObserver obs = new ActorObserver("8083",getName());
	}


	@State( name = "activate", initial=true)
	@Transition( state = "start",   msgId= SystemData.startSysCmdId  )
	protected void activate( IApplMessage msg ) {
		outInfo(""+msg);
		robotAdapter = new BasicRobotAdapter(getName());
		//init();
 	}

	@State( name = "start" )
	@Transition( state = "work", msgId="move"  )
	protected void start( IApplMessage msg ) {
		outInfo(""+msg); //activate ...
	}


	@State( name = "work" )
 	//@Transition( state = "work", msgId="move"  )
	//@Transition( state = "waitMoveResult" )
	@Transition( state = "handleOk", msgId="endMoveOk"  )
	@Transition( state = "handleKo", msgId="endMoveKo"  )
	protected void work( IApplMessage msg ) {
		outInfo(""+msg);  //msg(move,dispatch,,basicrobot,w,3)
		String cmd = msg.msgContent().replace("'","");
		//VRobotMoves.step(getName(), conn );
		//VRobotMoves.moveForward( getName(),conn,300 );
		robotAdapter.robotMove(cmd);
 	}

	@State( name = "handleOk" )
	@Transition( state = "work", msgId="move"  )
 	protected void handleOk( IApplMessage msg ) {
		outInfo(""+msg);
		this.updateResourceRep( ""+msg );
		//WARNING: non more a clean architecture
		/*
		try {
			WebSocketConfiguration.wshandler.sendToAll( new TextMessage( msg.toString() ) );
		} catch (Exception e) {
			e.printStackTrace();
		}*/
 	}
	@State( name = "handleKo" )
	@Transition( state = "work", msgId="move"  )
	protected void handleKo( IApplMessage msg ) {
		outInfo(""+msg);

		JSONObject json = new JSONObject( msg.msgContent().replace("'",""));
		String move  = json.getString("move");
		int duration = json.getInt("duration") ;
		this.updateResourceRep(move + " failed after:"+duration);

		//this.updateResourceRep( ""+msg );
		/*
		//WARNING: non more a clean architecture
		try {
			WebSocketConfiguration.wshandler.sendToAll( new TextMessage( move + " failed after:"+duration ) );
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	
	@State( name = "endJob" )
	protected void endJob( IApplMessage msg ) {
		outInfo(""+msg);
   	}
	
 
	
}

 