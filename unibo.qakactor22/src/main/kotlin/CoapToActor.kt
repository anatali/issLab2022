package it.unibo.kactor

import kotlinx.coroutines.launch
import org.eclipse.californium.core.server.resources.CoapExchange

/*
 * ----------------------------------------------------------------------------------------------
 * Temporary actor that makes a qak request and waits for a reply to a qak request
 * ----------------------------------------------------------------------------------------------
 */

class CoapToActor(name : String, val exchange: CoapExchange,
                  val owner: ActorBasic, val extmsg : IApplMessage) : ActorBasic( name ){
var answer = "noanswer" 	
 	init{
        this.context = owner.context
        context!!.addInternalActor( this )
		sysUtil.traceprintln("$tt $name| CREATED in ctx=${context!!.name} exchange=${exchange.getSourceAddress()}")
		//scope.launch{ autoMsg("start", "start") }
		scope.launch{ request( extmsg.msgId(), extmsg.msgContent(), owner) }
     }



     override suspend fun actorBody(msg : IApplMessage){
        if( msg.isReply() ){
  	        //sysUtil.traceprintln("$tt $name | PUT response: $msg exchange=${exchange.getSourceAddress()}"  )
 			answer = msg.toString().replace(name,owner.name)
 			exchange.respond( answer ) //DOES NOT WORK
            context!!.removeInternalActor( this )
         }
	}
}