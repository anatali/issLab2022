package unibo.webRobot22;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.web.util.HtmlUtils;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;

public class RobotCoapObserver implements CoapHandler{

    @Override
    public void onLoad(CoapResponse response) {
        ColorsOut.outappl("RobotCoapObserver changed!" + response.getResponseText(), ColorsOut.GREEN);
        //send info over the websocket
        WebSocketConfiguration.wshandler.sendToAll("" + response.getResponseText());
        //simpMessagingTemplate.convertAndSend(WebSocketConfig.topicForTearoomstatemanager, new ResourceRep("" + HtmlUtils.htmlEscape(response.getResponseText())));
    }

    @Override
    public void onError() {
        ColorsOut.outerr("RobotCoapObserver observe error!");
    }
}
