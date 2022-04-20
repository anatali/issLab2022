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
import unibo.actor22comm.http.*;
import java.util.Observable;

public class ClientUsingWsHttp implements IObserver{
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String HttpURL          = "http://"+localHostName+":"+port+"/api/move";
 

	private Interaction2021 connWs, connHttp;

	protected void doBasicMoves() throws Exception {
		connWs   = WsConnection.create("localhost:8091" );
		connHttp = HttpConnection.create("localhost:8090" );
		((WsConnection)connWs).addObserver(this);
		String answer = connHttp.request( ApplData.moveForward(2000) );
		ColorsOut.outappl("answer= " + answer, ColorsOut.BLACK  );
		CommUtils.delay(10000);
		connWs.close();
	}
 
 /*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientUsingWsHttp().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	
	@Override
	public void update(Observable source, Object data) {
		ColorsOut.outappl("ClientUsingWsHttp update/2 receives:" + data, ColorsOut.MAGENTA);
//		JSONObject d = new JSONObject(""+data);
//		ColorsOut.outappl("ClientUsingWsHttp update/2 collision=" + d.has("collision"), ColorsOut.MAGENTA);
		
	}
	@Override
	public void update(String data) {
		ColorsOut.out("ClientUsingWsHttp update receives:" + data);
	}
	
 }
