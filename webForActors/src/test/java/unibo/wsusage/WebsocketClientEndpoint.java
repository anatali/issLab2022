package unibo.wsusage;

import javax.imageio.ImageIO;
import javax.websocket.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;


@ClientEndpoint
public class WebsocketClientEndpoint {

    Session userSession = null;
    private IMessageHandler messageHandler;

    public WebsocketClientEndpoint(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    /**
     * Callback hook for Message Events. This method will be invoked when a client send a message.
     */
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received ... " + message);
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    @OnMessage
    public void onMessage(ByteBuffer bytes) {
        System.out.println("Received ... " + bytes);
        System.out.println("Handle byte buffer");
        try{
             ByteArrayInputStream bis = new ByteArrayInputStream(bytes.array());
             BufferedImage bImage2 = ImageIO.read(bis);
            ImageIO.write(bImage2, "jpg", new File("outputimage.jpg") );
            System.out.println("image created");
        }catch( Exception e){
            throw new RuntimeException(e);
        }

    }


    /**
     * register message handler
      * @param msgHandler
     */
    public void addMessageHandler(IMessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    /**
     * Send a message.
     * @param message
     */
    public void sendMessage(String message) {
        System.out.println("Sending ... " + message);
        this.userSession.getAsyncRemote().sendText(message);
    }

}
