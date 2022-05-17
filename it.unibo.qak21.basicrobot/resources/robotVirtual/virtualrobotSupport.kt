package robotVirtual

import java.io.PrintWriter
import java.net.Socket
import org.json.JSONObject
import java.io.BufferedReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.InputStreamReader
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.MsgUtil
import it.unibo.kactor.ApplMessage
 
 //A support for using the virtual robot
 
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
object virtualrobotSupport {
	lateinit var owner      : ActorBasic
	lateinit var robotsonar	: ActorBasic
	    private var hostName = "localhost"
        private var port     = 8999
        private val sep      = ";"
        private var outToServer : PrintWriter?     = null
        private val applCmdset = setOf("w","s","a","d","z","x","r","l","h"  )

        var traceOn = true
	
	init{
		println(" CREATING")		
	}
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	fun create( owner: ActorBasic, hostNameStr: String, portStr: String, trace : Boolean = false  ){
 		this.owner   = owner	 
 		this.traceOn = trace
 		//initClientConn
            hostName         = hostNameStr
            port             = Integer.parseInt(portStr)
             try {
                val clientSocket = Socket(hostName, port)
                trace("CONNECTION DONE with $port")
                outToServer  = PrintWriter(clientSocket.getOutputStream())
				//ACTIVATE the robotsonar as the beginning of a pipe
				robotsonar = virtualrobotSonarSupportActor("robotsonar", clientSocket)
				owner.context!!.addInternalActor(robotsonar)  
			  	println("		--- virtualrobotSupport | has created the robotsonar")	
             }catch( e:Exception ){
                 println("			*** virtualrobotSupport | ERROR $e")
             }	
	}
	
	fun trace( msg: String ){
		if( traceOn )  println("			*** virtualrobotSupport | $msg")
	}



    fun move(cmd: String) {	//cmd is written in application-language
			halt()	//defensive ...
			val msg = translate( cmd )
			trace("move  $msg")
            outToServer?.println(msg)
            outToServer?.flush()
			if( cmd=="l" || cmd =="r") { Thread.sleep( 300 ) } 
    }
/*
 	Performs a move written in cril
*/		
        fun domove(cmd: String) {	//cmd is written in cril 
            val jsonObject = JSONObject(cmd )
            val msg = "$sep${jsonObject.toString()}$sep"
            outToServer?.println(msg)
            outToServer?.flush()
        }
//translates application-language in cril
        fun translate(cmd: String) : String{ //cmd is written in application-language
		var jsonMsg = "{ 'type': 'alarm', 'arg': -1 }"
			when( cmd ){
				"msg(w)", "w" -> jsonMsg = "{ 'type': 'moveForward',  'arg': -1 }"
				"msg(s)", "s" -> jsonMsg = "{ 'type': 'moveBackward', 'arg': -1 }"
				"msg(a)", "a" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
				"msg(d)", "d" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
				"msg(l)", "l" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': 300 }"
				"msg(r)", "r" -> jsonMsg = "{ 'type': 'turnRight', 'arg': 300 }"
				"msg(z)", "z" -> jsonMsg = "{ 'type': 'turnLeft',  'arg': -1  }"
				"msg(x)", "x" -> jsonMsg = "{ 'type': 'turnRight', 'arg': -1  }"
				"msg(h)", "h" -> jsonMsg = "{ 'type': 'alarm',     'arg': 100 }"
				else -> println("virtualrobotSupport command $cmd unknown")
			}
            val jsonObject = JSONObject( jsonMsg )
            val msg = "$sep${jsonObject.toString()}$sep"
            println("virtualrobotSupport msg=$msg  ")
			return msg
		}
	
	    fun halt(){
            domove("{ 'type': 'alarm',     'arg': 100 }")
  		}
	

fun terminate(){
	robotsonar.terminate()
}	
	
 
 

}

 







