/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.webForActors;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.*;
import java.util.Observable;

public class ClientUsingWs implements IObserver{
 
	private Interaction2021 conn;
  
	protected void doJob() throws Exception {
		conn = WsConnection.create("localhost:8085/socket" );
		((WsConnection)conn).addObserver(this);
		ColorsOut.out("ClientUsingWs conn:" + conn);
		try{
			conn.forward(  "Hello from remote client" );
		}catch (Exception e){
			ColorsOut.outerr("ClientUsingWs doJob ERROR:" +e.getMessage());
		}
 	}
	
	@Override
	public void update(Observable source, Object data) {
		ColorsOut.out("ClientUsingWs update/2 receives:" + data);
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
 		new ClientUsingWs().doJob();
		CommUtils.aboutThreads("At end - ");
	}
	

	
 }
