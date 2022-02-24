package rx
 
import it.unibo.kactor.MsgUtil
import kotlinx.coroutines.delay
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import alice.tuprolog.Term
import alice.tuprolog.Struct

 
class distanceFilter (name : String ) : ActorBasic( name ) {
val LimitDistance = 10
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    override suspend fun actorBody(msg: ApplMessage) {
		if( msg.msgSender() == name) return //AVOID to handle the event emitted by itself
  		elabData( msg )
 	}

 	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	  suspend fun elabData( msg: ApplMessage ){ //OPTIMISTIC		 
 		val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
//  		println("$tt $name |  data = $data ")
		val Distance = Integer.parseInt( data ) 
/*
 * Emit a sonarRobot event to test the behavior with MQTT
 * We should avoid this pattern
*/	
//	 	val m0 = MsgUtil.buildEvent(name, "sonarRobot", "sonar($data)")
//	 	emit( m0 )
 		if( Distance < LimitDistance ){
	 		val m1 = MsgUtil.buildEvent(name, "obstacle", "obstacle($data)")
			//println("$tt $name |  emit m1= $m1")
			emitLocalStreamEvent( m1 ) //propagate event obstacle
     	}else{
			//println("$tt $name | DISCARDS $Distance ")
 		}				
 	}
}