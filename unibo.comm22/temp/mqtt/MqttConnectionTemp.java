package unibo.comm22.mqtt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import it.unibo.kactor.ApplMessage;
import unibo.comm22.interfaces.IApplMsgHandlerMqtt;
import unibo.comm22.interfaces.Interaction2021;

public class MqttConnectionTemp implements Interaction2021{
public static final String topicInput = "topicCtxMqtt";
protected static MqttConnectionTemp mqttSup ;  //for singleton

private MqttClient client;

protected BlockingQueue<String> blockingQueue =
              new LinkedBlockingDeque<String>(10);
protected String clientid;

  //Factory method
  public static synchronized MqttConnectionTemp getSupport( ){  return mqttSup; }
  //Get the singleton
  //public static MqttConnection getSupport() { return mqttSup; }

  //Hidden costructor
  protected MqttConnectionTemp( String clientName, String mqttBrokerAddr ) {
    connectToBroker(clientName, mqttBrokerAddr);
  }
  
  public void connectToBroker(String clientid,  String brokerAddr) {     
    try {
		client  = new MqttClient(brokerAddr, clientid);
		MqttConnectOptions options = new MqttConnectOptions();
		options.setKeepAliveInterval(480);
		byte[] bb = "crashed".getBytes();
		options.setWill("unibo/clienterrors",  bb , 2, true);
		client.connect(options);
	} catch (MqttException e) {
 		e.printStackTrace();
	}
  }
  
  public void publish(String topic, String msg, int qos, boolean retain) {
	  MqttMessage message = new MqttMessage();
	  if (qos == 0 || qos == 1 || qos == 2) {
	    //qos=0 fire and forget; qos=1 at least once(default);qos=2 exactly once
	    message.setQos(qos);
	  }
	  try {
	    message.setPayload(msg.toString().getBytes());
	    client.publish(topic, message);
	  } catch (MqttException e) {   }
	}

  
  public void subscribe ( String topic, IApplMsgHandlerMqtt handler) {
	  subscribe(clientid, topic, handler);
	}
  protected void subscribe( String clientid, String topic, MqttCallback callback) {
	  try {
	    client.setCallback( callback );
	    client.subscribe( topic );
	  } catch (MqttException e) {   }
  }  
  
  @Override
  public void forward(String msg) throws Exception {
    new ApplMessage(msg); //no exception => we can publish
    publish(topicInput, msg, 2, false);
    //publish( "unibo/qak/$destName", msg.toString() )
  }
  
@Override
public void sendALine(String msg) throws Exception {
	// TODO Auto-generated method stub
	
}
@Override
public void sendALine(String msg, boolean isAnswer) throws Exception {
	// TODO Auto-generated method stub
	
}
@Override
public String receiveALine() throws Exception {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void closeConnection() throws Exception {
	// TODO Auto-generated method stub
	
}
//To send a request and wait for the answer
@Override
public String request(String msg) throws Exception {
ApplMessage requestMsg = new ApplMessage(msg);

//Preparo per ricevere la risposta
String sender = requestMsg.msgSender();
String reqid  = requestMsg.msgId();
String answerTopicName = "answ_"+reqid+"_"+sender;
MqttClient clientAnswer = setupConnectionFroAnswer(answerTopicName);

//publish(topicInput, requestMsg.toString(), 2, false); //qos=2 !
forward( requestMsg.toString() );
            String answer = receiveMsg();
            clientAnswer.disconnect();
            clientAnswer.close();
}

public void reply(String msg) throws Exception {
	try {
	    ApplMessage m = new ApplMessage(msg);
	    String dest   = m.msgReceiver();
	    String reqid  = m.msgId();
	    String answerTopicName = "answ_"+reqid+"_"+dest;
	    publish(answerTopicName,msg,2,false);
	}catch(Exception e) {  e.printStackTrace(); }
}
@Override
public String receiveMsg() throws Exception {
  String answer         = blockingQueue.take();
  ApplMessage msgAnswer = new ApplMessage(answer);
  answer = msgAnswer.msgContent();
  return answer;
}
@Override
public void close() throws Exception {
	// TODO Auto-generated method stub
	
}
  
  
}
