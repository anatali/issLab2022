package it.unibo.enablerCleanArch.supports.mqtt;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import it.unibo.enablerCleanArch.domain.ApplMessage;
import it.unibo.enablerCleanArch.domain.IDistance;
import it.unibo.enablerCleanArch.main.RadarSystemConfig;
import it.unibo.enablerCleanArch.supports.ColorsOut;
import it.unibo.enablerCleanArch.supports.Utils;

public class MqttSupportDemoEssential {
private String topic      = MqttConnection.topicInput;
private String brokerAddr = RadarSystemConfig.mqttBrokerAddr; // : 1883  OPTIONAL

private final String caller    = "demo";
//private final String receiver  = "called";
private final String requestId = "query";

//String sender, String msgId, String payload, String dest
private String helloMsg  = Utils.buildDispatch(caller, "cmd",    "hello",    "ANYONE").toString();
//private String aRequest  = Utils.buildRequest(caller,  requestId,"getTime",  receiver).toString();
//private String aReply    = Utils.buildReply(receiver,  requestId, "ANSWER",   caller).toString();

//private BlockingQueue<String> blockingQueue = new LinkedBlockingDeque<String>(10);

private MqttConnection mqtt;

public void simulateReceiver(String name) {
	new Thread() {
		public void run() {
		try {
 			ColorsOut.outappl("receiver STARTS with " + mqtt, ColorsOut.GREEN);
			String inputMNsg = mqtt.receiveMsg();
			ColorsOut.outappl("receiver RECEIVED:"+ inputMNsg, ColorsOut.BLACK);
 		} catch (Exception e) {
			ColorsOut.outerr("receiver  | Error:" + e.getMessage());
	 	}
		}//run
	}.start();
}

protected void init() {
 	mqtt = MqttConnection.getSupport();
	mqtt.connectToBroker("demo", RadarSystemConfig.mqttBrokerAddr);	
	MqttAnswerHandler h = new MqttAnswerHandler("demoH", mqtt.getQueue() );
	mqtt.subscribe(topic, h);
}

protected void end() {
	//mqtt.unsubscribe( topic );	
	mqtt.disconnect();
}

public void doSendReceive() {
	init();
	simulateReceiver("r1");
	
	//SENDER part
	try {
		mqtt.forward(helloMsg);	//OK
		Utils.delay(1000);
		end();  //Se no si ha poi un  connectionLost  
		ColorsOut.outappl("doSendReceive BYE BYE" , ColorsOut.BLACK);
 	} catch (Exception e) {
		e.printStackTrace();
	}
}

/*
 * -------------------------------------------------
 * REQUEST - RESPONSE
 * -------------------------------------------------
 */


public void simulateCalled( String name ) {
	new Thread() {
		public void run() {
		try {
 			ColorsOut.outappl(name + "| STARTS with " + mqtt, ColorsOut.GREEN);
			String inputMNsg = mqtt.receiveMsg();  //Si blocca sulla coda popolata da 
			ColorsOut.outappl(name + "| RECEIVED:"+ inputMNsg, ColorsOut.BLACK);
//Elaborate and send answer			
 			ApplMessage msgInput = new ApplMessage(inputMNsg);
			ColorsOut.outappl(name + " | input=" + msgInput + " topic="+topic, ColorsOut.GREEN);
			String caller = msgInput.msgSender();
			String reqId  = msgInput.msgId();
			String myReply = Utils.buildReply(name ,  reqId, "ANSWER", caller  ).toString();
			String content = "time('" + java.time.LocalTime.now() + "')";
 			String answer  = myReply.replace("ANSWER", content );  
			ColorsOut.outappl(name + "| replies:"+ answer, ColorsOut.GREEN);
			mqtt.reply(answer);  			
 		} catch (Exception e) {
			ColorsOut.outerr(name + " | Error:" + e.getMessage());
	 	}
		}//run 
	}.start(); 
}

public void doRequestRespond() {
	init();
	simulateCalled( "called1");
//	simulateCalled( "called2");
	//Caller part 	
	try {
		String req1 = Utils.buildRequest(caller,  requestId,"getTime",  "called1").toString();
		String answer1 = mqtt.request(req1);	 //blocking
		ColorsOut.outappl(caller + " RECEIVED answer1:"+ answer1, ColorsOut.BLACK);
//		String req2 = Utils.buildRequest(caller,  requestId,"getTime",  "called2").toString();
//		String answer2 = mqtt.request(req2);	   //blocking
//		Colors.outappl(caller + " RECEIVED answer2:"+ answer2, Colors.BLACK);
 	} catch (Exception e) {
		e.printStackTrace();
	}
}
 
	public static void main(String[] args) throws Exception  {
		//RadarSystemConfig.mqttBrokerAddr  = "tcp://localhost:1883";  
		//RadarSystemConfig.mqttBrokerAddr  = "tcp://broker.hivemq.com";
		RadarSystemConfig.mqttBrokerAddr  = "tcp://test.mosquitto.org";
		//RadarSystemConfig.mqttBrokerAddr  = "tcp://mqtt.eclipse.org:1883";  //NO
		MqttSupportDemoEssential sys = new MqttSupportDemoEssential();	
		
 		//sys.doSendReceive();
  		sys.doRequestRespond();
  		
  		System.exit(0);
 	}

}
