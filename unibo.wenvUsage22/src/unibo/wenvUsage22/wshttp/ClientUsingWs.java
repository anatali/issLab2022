/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.wenvUsage22.wshttp;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.*;
import unibo.wenvUsage22.common.ApplData;

import java.util.Observable;

public class ClientUsingWs implements IObserver{
 
	private Interaction2021 conn;
  
	protected void doBasicMoves() throws Exception {
		conn = WsConnection.create("localhost:8091" );
		((WsConnection)conn).addObserver(this);
 
 		//conn.forward( turnLeft( 800  ) );
 		conn.forward( ApplData.moveForward(2300) );
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
/*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingWs().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	

	
 }
