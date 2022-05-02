package unibo.Robots.common;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public class VRobotMoves {

	public final static String robotCmdId = "move";
	public final static String cril_w = "moveForward(300)";
	public final static String cril_a = "turnLeft(300)";
	public final static String cril_d = "turnRight(300)";
	public final static String cril_s = "moveBAckward(300)";
	public final static String cril_h = "alarm(300)";
	
	public static final IApplMessage move( String cmd,String sender, String receiver ) {
		switch( cmd ) {
		case "w" : return CommUtils.buildDispatch(sender,robotCmdId, cril_w,receiver);
		case "a" : return CommUtils.buildDispatch(sender,robotCmdId, cril_a,receiver);
		case "d" : return CommUtils.buildDispatch(sender,robotCmdId, cril_d,receiver);
		case "h" : return CommUtils.buildDispatch(sender,robotCmdId, cril_h,receiver);
		}
		return  CommUtils.buildDispatch(sender,robotCmdId, cmd,receiver);
	}
	
	public static final IApplMessage w(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, robotCmdId, cril_w,receiver);
	}
	public static final IApplMessage a(String sender, String receiver)   {
		return CommUtils.buildDispatch(sender, robotCmdId,cril_a,receiver);
	}

	
	
	
	public static final String w = "moveForward";
	
	public static void moveForward(String name, Interaction2021 conn, int duration)  {
		try {
			//ColorsOut.outappl(name + " | moveForward conn:" + conn,  ColorsOut.BLUE);
			conn.forward( ApplData.moveForward(duration) );
		}catch( Exception e) {
			ColorsOut.outerr( name +  " | doBasicMoves ERROR:" +  e.getMessage() );
		}	
	}
	
	public static void turnLeft(String name, Interaction2021 conn) {
		try {
			conn.forward( ApplData.turnLeft( 300  ) );
		}catch( Exception e) {
			ColorsOut.outerr( name +  " | turnLeft ERROR:" +  e.getMessage() );
		}	
	}
	public static void turnRight(String name, Interaction2021 conn) {
		try {
			conn.forward( ApplData.turnRight( 300  ) );
		}catch( Exception e) {
			ColorsOut.outerr( name +  " | turnLeft ERROR:" +  e.getMessage() );
		}	
	}

	public static void turnLeftAndStep(String name, int duration, Interaction2021 conn) {
		try {
			turnLeft(name,conn);
			CommUtils.delay(310);
			//step( name,conn  );	  		
			stepAfterTurn(name,conn,duration);     //con dt>300 sta barando ...
 			turnLeft(name,conn);
			CommUtils.delay(310);
 		}catch( Exception e) {
			ColorsOut.outerr( name +  " | turnLeft ERROR:" +  e.getMessage() );
		}	
	}
	public static void turnRightAndStep(String name, int duration, Interaction2021 conn) {
		try {
			turnRight(name,conn);
			CommUtils.delay(310);
			//step( name,conn  );	 //collision?		
			stepAfterTurn(name,conn,duration);     //con dt>300 sta barando ...
			turnRight(name,conn);
			CommUtils.delay(310);
 		}catch( Exception e) {
			ColorsOut.outerr( name +  " | turnLeft ERROR:" +  e.getMessage() );
		}	
	}
	public static void turnLeftAndHome(String name, Interaction2021 conn) {
		try {
			turnLeft(name,conn);
			CommUtils.delay(310);
			//step( name,conn  );	  		
			stepAfterTurn(name,conn,2500);
  		}catch( Exception e) {
			ColorsOut.outerr( name +  " | turnLeft ERROR:" +  e.getMessage() );
		}	
	}
	
	public static void step(String name, Interaction2021 conn) {
		moveForward( name,conn,300 );	 //se collision non completa		
		//CommUtils.delay(400); 		
	}
	public static void stepAfterTurn(String name, Interaction2021 conn, int dt) {
		moveForward( name,conn,dt );	 //se collision non completa		
		CommUtils.delay(dt+100); 		
	}

}
