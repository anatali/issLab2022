package unibo.coaptest;

import unibo.comm22.utils.ColorsOut;
 

public class CoapObserverResource extends CoapObserverBase{

	public CoapObserverResource(String uri) {
		super(uri);
	}

	protected void outMsg( String content ) {
		//MsgUtil.outgreen("CoapObserverBase | value=" + content );
		ColorsOut.outappl("CoapObserver1 | value=" + content, ColorsOut.MAGENTA);
	}

	public static void main( String[] args ) {
		new CoapObserverResource("localhost:8065/ctxresource1/resource1");
	}
}
