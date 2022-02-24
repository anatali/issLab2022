package consolegui

object consoleGuiTcp{
	fun create(  hostIP : String,     port : String,     destName : String) {
		consoleGuiSimple( connQak.ConnectionType.TCP, hostIP, port, destName)
	}
}
fun main(){
	consoleGuiTcp.create( hostAddr, port, qakdestination)
}
 