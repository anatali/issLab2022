package rx
 
import it.unibo.kactor.*
import kotlinx.coroutines.delay
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import alice.tuprolog.Term
import alice.tuprolog.Struct

 
class distanceFilter (name : String ) : ActorBasic( name ) {
val LimitDistance = 8
 
    override suspend fun actorBody(msg: IApplMessage) {
  		elabData( msg )
 	}

 
	  suspend fun elabData( msg: IApplMessage ){ //		 
 		val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
  		//println("   $name |  data = $data ")		
		val Distance = Integer.parseInt( data ) 
 		if( Distance < LimitDistance ){
	 		val m1 = MsgUtil.buildEvent(name, "obstacle", "obstacle($data)")
			//println("   ${name} |  emit m1= $m1")
			emitLocalStreamEvent( m1 ) //propagate event obstacle
     	}else{
			//println("   $name |  DISCARDS $Distance ")
 		}				
 	}
}