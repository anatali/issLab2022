package consolegui

object consoleGuiCoap{
	fun create(  hostIP : String,     port : String,     destName : String) {
		consoleGuiSimple( connQak.ConnectionType.COAP, hostIP, port, destName)
	}
}

fun main(){
	consoleGuiCoap.create( hostAddr, port, qakdestination)
}
 