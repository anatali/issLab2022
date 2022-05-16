/*
BasicRobotKactor.kt

 */

package kactorWEnv
import it.unibo.supports.IssWsHttpKotlinSupport
import it.unibo.supports.NaiveActorKotlinObserver
import kotlindemo.cpus
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel
import org.json.JSONObject
import java.lang.Exception

lateinit var basicRobotKactor: SendChannel<String>
lateinit var support: IssWsHttpKotlinSupport

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun createBasicActor(scope:CoroutineScope) : SendChannel<String> {
	 println("createBasicActor ${kotlindemo.curThread()}")
	 support = IssWsHttpKotlinSupport.getConnectionWs(scope, "localhost:8091")
	 basicRobotKactor = scope.actor( capacity = 30 ) {
 			var state = "working"
 			//val obs = WEnvSupportObserver("obs",basicRobotKactor,this)
		    //support.registerActor(obs)
/*
msg-driven behavior
 */
			while (state == "working") {
				var msg = channel.receive()
				println("BasicRobotKactor working receives: $msg ") //
					val msgJson = JSONObject(msg)
 					var input   = msgJson.keys().next()
					//println("BasicRobotKactor input: $input ")
 					when (input) {
						"robotmove" -> support.forward( msg )
						"endmove"   -> { println("endmove result= ${msgJson.getString("endmove")}") }
						"sonarName" -> { println("sonar   distance=${msgJson.getInt("distance")}") }
						"collision" -> { println("collision handling ... ") }
						else -> println("NO HANDLE for $msg")
					}
			}//while

			println("BasicRobotKactor ENDS state=$state")
		    basicRobotKactor.close()
		}//actor
	return basicRobotKactor
}//createBasicActor

fun main( ) {
	runBlocking {
		println("main BEGINS CPU=$cpus ${kotlindemo.curThread()}")
		createBasicActor(this)
		println("main ENDS runBlocking ${kotlindemo.curThread()}")
	}
	println("main ENDS ${kotlindemo.curThread()}")
}


 

