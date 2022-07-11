package unibo.testqakexample;


import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;


public class TrolleyPosObserver implements CoapHandler{
protected String history = "";
    @Override
    public void onLoad(CoapResponse response) {
        history += response.getResponseText();
        ColorsOut.outappl("TrolleyPosObserver history=" + history, ColorsOut.MAGENTA);
    }

    @Override
    public void onError() {
        ColorsOut.outerr("CoapObserver observe error!");
    }


}
