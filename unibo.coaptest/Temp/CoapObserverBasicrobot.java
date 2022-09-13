package unibo.coaptest;

import unibo.comm22.utils.ColorsOut;
 

public class CoapObserverBasicrobot extends CoapObserverBase{

	public CoapObserverBasicrobot(String uri) {
		super(uri);
	}

	protected void outMsg( String content ) {
		//MsgUtil.outgreen("CoapObserverBase | value=" + content );
		ColorsOut.outappl("CoapObserver1 | value=" + content, ColorsOut.BLUE);
	}

	public static void main( String[] args ) {
		new CoapObserverBasicrobot("localhost:8020/ctxbasicrobot/basicrobot");
	}
}
