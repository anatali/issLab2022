package robotMbot
/*
 -------------------------------------------------------------------------------------------------
 Reads data from the serial connection to Arduino
 For each data value V, it emitLocalStreamEvent sonarRobot:sonar(V)
 -------------------------------------------------------------------------------------------------
 */
import it.unibo.kactor.*
import kotlinx.coroutines.launch
import java.io.BufferedReader
import alice.tuprolog.Term
import alice.tuprolog.Struct
import kotlinx.coroutines.delay
import kotlinx.coroutines.GlobalScope


class robotDataSourceArduino( name : String, val owner : ActorBasic ,
					val conn : SerialPortConnSupport) : ActorBasic(name, owner.scope){
	
companion object {
	val eventId = "sonarRobot"
}		
	init{
 		println("   	%%% $name |  starts conn=$conn")	 
	}

	override suspend fun actorBody(msg: IApplMessage) {
        //println("   	%%% $name |  handles msg= $msg  ")
		//val vStr  = (Term.createTerm( msg.msgContent()) as Struct).getArg(0).toString()
		//println("   	%%% $name |  handles msg= $msg  vStr=$vStr")
		elabData(   )
	}
 
	suspend fun elabData(  ){
 	GlobalScope.launch{	//to allow message handling
		var counter   = 0
        while (true) {
 			try {
 				var curDataFromArduino = conn.receiveALine()
  	 			//println("   	%%% $name | elabData received: $curDataFromArduino"    )
 				var v = curDataFromArduino.toDouble() 
				//handle too fast change ?? NOT HERE
  				var dataSonar = v.toInt();													
  	 			//println("   	%%% $name | elabData: $dataSonar | ${counter++}"    )
				//if( dataSonar < 350 ){ /REMOVED since USING STREAMS
 				var event = MsgUtil.buildEvent( name,"sonarRobot","sonar( $dataSonar )")								
  				//println("   	%%% $name | mbotSupport event: ${ event } owner=${owner.name}"   );						
				//owner.emit(  event )
				emitLocalStreamEvent( event )
				//delay(100)  //WARNING: if included, it does not change values
			} catch ( e : Exception) {
 				println("   	%%% $name | robotDataSourceArduino | ERROR $e   ")
            }
 			//delay(100)  //WARNING: if included, it does read correct values
		}//while
	}
 	}
	
 
}