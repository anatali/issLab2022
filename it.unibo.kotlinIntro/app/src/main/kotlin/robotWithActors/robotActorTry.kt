/*
robotActorTry.kt

 */

package robotWithActors
import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.actor0.ApplMessage
import it.unibo.robotService.ApplMsgs
import it.unibo.supports.FactoryProtocol
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel
import mapRoomKotlin.TripInfo
import org.json.JSONObject
import prodCons.curThread
import java.lang.Exception
import java.util.HashMap

val MoveJsonCmd : HashMap<String, String> = hashMapOf(
	"w" to ApplMsgs.forwardMsg, 
	"s" to ApplMsgs.backwardMsg, 
	"l" to ApplMsgs.turnLeftMsg, 
	"r" to ApplMsgs.turnRightMsg, 
	"h" to ApplMsgs.haltMsg
)

lateinit var robotActorTry: SendChannel<String>

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun createActor(scope:CoroutineScope) : SendChannel<String> {
	 println("createActor ${kotlindemo.curThread()}")
	 robotActorTry = scope.actor( capacity = 3 ) {
			val moves = TripInfo()
			var state = "working"
         	lateinit var conn: IConnInteraction

			fun doInit(){
				//println("robotActorTry | doInit ${curThread()}" )
				val fp = FactoryProtocol(null, "TCP", "robot")
				//println("    ---  robotActorTry | doInit fp:$fp")
				conn = fp.createClientProtocolSupport("localhost", 8010)
				println("    ---  doInit | connected:$conn")
				InputReader.createInputReader( robotActorTry, conn )
			}

		    fun doCmd( cmd: String ){
		    	when(cmd){
		    		"init" -> doInit()
					"end"  -> state = "end"
					else   -> println("cmd unknown")
		    	}
		    }

			suspend fun doMove(moveShort: String, dest: String) { //Talk with BasicRobotActor
				try {
					//println("    ---   robotActorTry | doMove moveShort:$moveShort  " )
					val cmd = MoveJsonCmd.get(moveShort)
					val applMsg = "msg(robotmove,dispatch,actortry,DEST,CMD,1)"
							.replace("DEST", dest)
							.replace("CMD", cmd!!)
					//println("    ---   robotActorTry | doMove applMsg:$applMsg  " )
					val msg = ApplMessage.create(applMsg)
					//println("    ---   robotActorTry | doMove msg:$msg ${curThread()}" )
					conn.sendALine(msg.toString())
					moves.updateMovesRep(moveShort);
					moves.showMap()
					moves.showJourney()
					delay(1000) //to avoid too-rapid movement
				} catch (e: Exception) {
					e.printStackTrace()
				}
			}//doMove

			 fun doEndMove( endOfMove: String, move: String ) {
				if( endOfMove=="false") println("$move failed")
			}

 			suspend fun doSonar(msg: String) {
				println("robotActorTry doSonar  $msg")
				//robotActorTry.send("{\"move\":\"s\"}" )	//automsg => capacity > 0
				/*
				with capacity=0 the actor is not able to elaborate this s-command
				since it is still executing the previous
				*/
			}

			suspend fun doCollision(msg: String) {
				println("robotActorTry | doCollision $msg  ");
 			}//doCollision

/*
msg-driven behavior
 */
			while (state == "working") {
				var msg = channel.receive()
				println("robotActorTry working receives: $msg ") //
				try {
					val msgJson = JSONObject(msg)
 					var input = msgJson.keys().next()
					//println("robotActorTry input: $input ")
 					when (input) {
						"cmd"       -> doCmd (msgJson.getString("cmd") )
						"move"      -> doMove(msgJson.getString("move"), "stepRobot")
						"endmove"   -> doEndMove(msgJson.getString("endmove"),msgJson.getString("move"))
						"sonarName" -> doSonar(msg)
						"collision" -> doCollision(msg)
						else -> println("NO HANDLE for $msg")
					}
				} catch (e: Exception) {
					println("robotActorTry error $e ")
				}
			}//while

			println("robotActorTry ENDS state=$state")
		    robotActorTry.close()
		}//actor
	return robotActorTry
}//createActor




 

