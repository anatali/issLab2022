package it.unibo.kactor

import kotlinx.coroutines.*
import org.eclipse.paho.client.mqttv3.*

var mqtttraceOn = false

class MqttUtils(val owner: String )  {
	//protected var clientid: String? = null
	protected var eventId: String? = "mqtt"
	protected var eventMsg: String? = ""
	protected lateinit var client: MqttClient
	protected lateinit var workActor : ActorBasic
	protected var isConnected = false
	
	protected val RETAIN = false;

	fun trace( msg : String ){
		if( mqtttraceOn ) println("$msg")
	}

	fun connect(clientid: String, brokerAddr: String ): Boolean {
		try {
  			trace("       %%% MqttUtils $owner | doing connect for $clientid to $brokerAddr "  );
			client = MqttClient(brokerAddr, clientid)
            //trace("connect $brokerAddr client = $client" )
			val options = MqttConnectOptions()
			options.setKeepAliveInterval(480)
			options.setWill("unibo/clienterrors", "crashed".toByteArray(), 2, true)
			client.connect(options)
			println("       %%% MqttUtils $owner | connect DONE $clientid to $brokerAddr " )//+ " " + client
 			isConnected = true
		} catch (e: Exception) {
			println("       %%% MqttUtils $owner | for $clientid connect ERROR for: $brokerAddr" )
			isConnected = false
		}
		return isConnected
	}

	fun connectDone() : Boolean{
		return isConnected
	}
	
	fun disconnect() {
		try {
			println("       %%% MqttUtils $owner | disconnect " + client)
			client.disconnect()
		} catch (e: Exception) {
			println("       %%% MqttUtils $owner | disconnect ERROR ${e}")
		}
	}


	fun subscribe(actor: ActorBasic, topic: String ) {
		//println("	%%% MqttUtils ${actor.name} subscribe to topic=$topic client=$client "  )
		try {
			this.workActor = actor
			//client.setCallback(this)
			client.setCallback(actor)
			client.subscribe(topic)
		}catch( e: Exception ){
			println("       %%% MqttUtils $owner | ${actor.name} subscribe topic=$topic ERROR=$e "  )
		}
	}

//	fun sendMsg( sender: String, msgID: String,  dest: String,  msgType: String ){
//	  	val msgNum = 0
//	  	//msg( MSGID, MSGTYPE SENDER, RECEIVER, CONTENT, SEQNUM )	  				
//	  	val msgout = "msg( $msgID, $msgType, $sender, $dest, $msgNum )"
//	  	//println(" ************ SENDING VIA MQTT mout=$msgout" )
//	  	publish(   "unibo/qasys", msgout, 1, RETAIN);		
//	}
//	fun sendMsg(  msg: String, topic: String ){
//		//println(" ************ SENDING VIA MQTT mout=$msg on $topic" )
// 	  	publish( topic, msg, 1, RETAIN);
//	}
//	fun sendMsg(  msg: ApplMessage, topic: String ){
//		//println(" ************ SENDING VIA MQTT mout=$msg on $topic" )
//		publish( topic, msg.toString(), 1, RETAIN);
//	}
/*
	override fun connectionLost(cause: Throwable?) {
		println("	%%% MqttUtils connectionLost $cause " )
	}

 	override fun deliveryComplete(token: IMqttDeliveryToken?) {
//		println("			%%% MqttUtils deliveryComplete token= "+ token );
	}
	override fun messageArrived(topic: String, msg: MqttMessage) {
		println("	%%% MqttUtils messageArrived on "+ topic + ": "+msg.toString());
		sendMsgToWorkActor( msg.toString() )
	}
gradle
*/
	/*
         * sends to a tpoic a content of the form
         * 	 msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
     */
	@Throws(MqttException::class)
	fun publish( topic: String, msg: String?, qos: Int=2, retain: Boolean=false) {
		val message = MqttMessage()
		message.setRetained(retain)
		if (qos == 0 || qos == 1 || qos == 2) {
			//qos=0 fire and forget; qos=1 at least once(default);qos=2 exactly once
			message.setQos(0)
		}
		message.setPayload(msg!!.toByteArray())
		try {
			client.publish(topic, message)
//			println("			%%% MqttUtils published "+ message + " on topic=" + topic);
		} catch (e:Exception) {
			println("       %%% MqttUtils $owner | publish ERROR $e topic=$topic msg=$msg"  )
 		}
	}


@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	fun sendMsgToWorkActor( msg: String ){
		workActor.scope.launch{
			val m = ApplMessage( msg )
			workActor.actor.send( m )
 		}
	}
	
	fun clearTopic( topic : String )  {
  		println("       %%%  MqttUtils clearTopic $topic" );
		publish( topic, "", 1, true);	//send a retained message !!
	}

/*
	fun println(msg: String?) {
		System.out.println(msg)
	}
*/
}