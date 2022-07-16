/*
ClientUsingWs.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.actor22.ws;

import unibo.actor22.common.ApplData;
import unibo.comm22.interfaces.IObserver;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;
import unibo.comm22.ws.*;
import java.util.Observable;

public class ClientUsingWs implements IObserver{
 
	private Interaction2021 conn;
 /* 
	protected void doBasicMoves() throws Exception {
		conn = WsConnection.create("localhost:8085/socket" );
		((WsConnection)conn).addObserver(this);
 
 		//conn.forward( turnLeft( 800  ) );
 		conn.forward( ApplData.moveForward(500) ); 
 			//La info di fine mossa viene gestita da update/2
 		
 		
//		conn.forward( stop( ) );
////    	Thread.sleep( 500 );
//		conn.forward( turnRight( 400 ) );

//			conn.forward( turnRight(300) );
// 			CommUtils.delay(500);
//			conn.forward( stop() );
//			conn.forward( turnLeft(300) );
// 			CommUtils.delay(500);
//			conn.forward( stop() );
//			//CommUtils.delay(500);
//			conn.forward( moveForward(1000) );
 
//			CommUtils.delay(1500);
//			conn.close();
// 
	}
	
	@Override
	public void update(Observable source, Object data) {
		ColorsOut.out("ClientUsingWs update/2 receives:" + data);
//		JSONObject d = new JSONObject(""+data);
//		ColorsOut.outappl("ClientUsingWs update/2 collision=" + d.has("collision"), ColorsOut.MAGENTA);
		
	}
	@Override
	public void update(String data) {
		ColorsOut.out("ClientUsingWs update receives:" + data);
	}	
 

	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingWs().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	
 */

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(String value) {
		// TODO Auto-generated method stub
		
	}
	
 }
