package connQak

import it.unibo.supports.FactoryProtocol
import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ApplMessage

class connQakTcp( hostIP : String,  port : String,  destName : String ) :
										           connQakBase(hostIP, port, destName){
	lateinit var conn   : IConnInteraction
	
	override fun createConnection( ){ //hostIP: String, port: String  
		val fp    = FactoryProtocol(null,"TCP","connQakTcp")
	    conn      = fp.createClientProtocolSupport(hostIP, port.toInt() )    
	}
	
	override fun forward( msg: ApplMessage ){
 		conn.sendALine( msg.toString()  )				
	}
	
	override fun request( msg: ApplMessage ){
 		conn.sendALine( msg.toString()  )
		//Acquire the answer	
		val answer = conn.receiveALine()
		println("connQakTcp | answer= $answer")		
	}
	
	override fun emit( msg: ApplMessage ){
 		conn.sendALine( msg.toString()  )			
	}	
}