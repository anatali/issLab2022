package connQak

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ApplMessage
import it.unibo.kactor.IApplMessage
import unibo.actor22comm.tcp.TcpClientSupport
import unibo.actor22comm.interfaces.Interaction2021

class connQakTcp(  ) : connQakBase( ){
	lateinit var conn   : Interaction2021 //IConnInteraction
	
	override fun createConnection( ){ //hostIP: String, port: String
		conn = TcpClientSupport.connect( hostAddr, port.toInt(),10 )
		println("connQakTcp createConnection $hostAddr:$port")
	}
	
	override fun forward( msg: IApplMessage ){
		println("connQakTcp | forward: $msg")	
 		conn.sendALine( msg.toString()  )				
	}
	
	override fun request( msg: IApplMessage ){
 		conn.sendALine( msg.toString()  )
		//Acquire the answer	
		val answer = conn.receiveALine()
		println("connQakTcp | answer= $answer")		
	}
	
	override fun emit( msg: IApplMessage ){
 		conn.sendALine( msg.toString()  )			
	}	
}