package consolegui
 
object consoleGuiMqtt{
	fun create(  hostIP : String,     port : String,     destName : String) {
		consoleGuiSimple( connQak.ConnectionType.MQTT, hostIP, port, destName)
	}
}
 
fun main(){
	consoleGuiMqtt.create( mqtthostAddr, mqttport, qakdestination)
}
 