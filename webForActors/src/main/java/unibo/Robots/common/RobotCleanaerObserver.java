package unibo.Robots.common;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import unibo.actor22comm.utils.ColorsOut;

public class RobotCleanaerObserver {
	private CoapObserveRelation relation = null;
	private CoapClient client = null;
	private IWsHandler wsh ;

	public RobotCleanaerObserver(String port, String actorName){
		client = new CoapClient("coap://localhost:"+port+"/actors/"+actorName);
		ColorsOut.outappl("ActorObserver | CREATED client=" + client, ColorsOut.GREEN);
		observe();
	}

	public void setWebSocketHandler(IWsHandler h){
		wsh = h;
	}
	public void  observe( ) {
		relation = client.observe(
				new CoapHandler() {
					@Override public void onLoad(CoapResponse response) {
						String content = response.getResponseText();
						ColorsOut.outappl("ActorObserver | value=" + content, ColorsOut.GREEN);
						if( wsh != null ) wsh.sendToAll(content);
					}					
					@Override public void onError() {
						ColorsOut.outerr("OBSERVING FAILED (press enter to exit)");
					}
				});		
	}

}
