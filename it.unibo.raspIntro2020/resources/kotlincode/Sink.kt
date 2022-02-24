package kotlincode

import it.unibo.kactor.*
import kotlinx.coroutines.CoroutineScope
import javacode.CoapSupport

class Sink(name:String, scope: CoroutineScope) : ActorBasic( name, scope ){
var resourceSupport : CoapSupport	 
	
	init{
		resourceSupport = CoapSupport("coap://192.168.1.8:5683", "sonardata")
	}
    override suspend fun actorBody(msg : ApplMessage){
        println("   $name |  receives msg= $msg ")
		//PUT to a CoAP resource
		resourceSupport.updateResource( msg.toString() )
    }
}