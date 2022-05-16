/*
robotUsage.kt

 */

package kactorWEnv
import it.unibo.robotService.ApplMsgs
import kotlindemo.cpus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

val MoveJsonCmd : HashMap<String, String> = hashMapOf(
	"w" to ApplMsgs.forwardMsg, 
	"s" to ApplMsgs.backwardMsg, 
	"l" to ApplMsgs.turnLeftMsg, 
	"r" to ApplMsgs.turnRightMsg, 
	"h" to ApplMsgs.haltMsg
)

lateinit var robotUsage: SendChannel<String>

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun createRobotUsagekActor(scope:CoroutineScope, robot: SendChannel<String>) : SendChannel<String> {
	 println("createRobotUsagekActor ${kotlindemo.curThread()}")
	robotUsage = scope.actor( capacity = 3 ) {
        var state = "working"
 

		suspend fun doWork(){
			robot.send( MoveJsonCmd.get("r")!! )
			delay(500)
			robot.send( MoveJsonCmd.get("l")!! )
		}

		suspend fun doCmd( cmd: String ){
		    	when(cmd){
		    		"work" -> doWork()
					"end"  -> state = "end"
					else   -> println("cmd unknown")
		    	}
		    }

/*
msg-driven behavior
 */
			while (state == "working") {
				var msg = channel.receive()
				println("robotUsage working receives: $msg ") //
				try {
					val msgJson = JSONObject(msg)
 					var input = msgJson.keys().next()
					//println("robotUsage input: $input ")
 					when (input) {
						"cmd"       -> doCmd (msgJson.getString("cmd") )
						"endmove"   -> { println("robotUsage | endmove result= ${msgJson.getString("endmove")}") }
						"sonarName" -> { println("robotUsage | sonar   distance=${msgJson.getInt("distance")}") }
						"collision" -> { println("robotUsage | collision handling ... ") }
						else -> println("robotUsage | NO HANDLE for $msg")
					}
				} catch (e: Exception) {
					println("robotUsage error $e ")
				}
			}//while

			println("robotUsage ENDS state=$state")
		robotUsage.close()
		}//actor
	return robotUsage
}//createActor

fun main( ) {
	runBlocking {
		println("main BEGINS CPU=$cpus ${kotlindemo.curThread()}")
//CONFIGURE THE SYSTEM
		createBasicActor(this)		//set basicRobotKactor and support
		createRobotUsagekActor(this, basicRobotKactor )  //set robotUsage

		val obs  = WEnvSupportObserver("obs",robotUsage,this)
		support.registerActor(obs)

		robotUsage.send("{\"cmd\":\"work\"}")

		println("main ENDS runBlocking ${kotlindemo.curThread()}")
	}
	println("main ENDS ${kotlindemo.curThread()}")
}



 

