package kotlindemo
//demoActors.kt
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.SendChannel

var dispatcher    = newSingleThreadContext("single")
lateinit var receiverActor : SendChannel<String>

fun startReceiver(  ){
	val myScope = CoroutineScope(dispatcher)

	receiverActor = myScope.actor<String>( dispatcher, capacity = 2) {
		//actor is a coroutine builder (dual of produce)
		println("receiverActor STARTS")
		/*
		channel is a reference to the mailbox channel that this coroutine receives messages from.
		It is provided for convenience, so that the code in the coroutine can refer to the channel
		as channel as apposed to this.
		All the ReceiveChannel functions on this interface delegate to the channel instance
		returned by this function.
		 */
		//delay(500)		//time of initialization ...
		for (v in channel) {
			when( v ){
				"end" -> { println("receiverActor ENDS ${curThread()}") }
				else -> {
					println("receiverActor receives $v ${curThread()}")
				}
			}
		}
		/*
		var msg = channel.receive()
		while( msg != "end" ){ 	//message-driven
			delay(500)   //time to elaborate the msg ...
			println("receiverActor receives $msg ${curThread()}")
			msg = channel.receive()
		}
		println("receiverActor ENDS ${curThread()}")
		 */
	}
}
@kotlinx.coroutines.ObsoleteCoroutinesApi

fun startSender( ){
	println("senderActor STARTS")
	val myScope = CoroutineScope(dispatcher)
	val senderActor = myScope.actor<String> {	//(2)
		println("sender STARTS")
		for( i in 1..4 ) {
			receiverActor.send("Hello$i")
			println("sender has sent Hello$i ${curThread()}")
		}
		receiverActor.send("end")
		println("sender ENDS ${curThread()}" )
 	}
}

fun actorsSenderReceiver(){
		startReceiver( )	//first, to setup: lateinit var receiverActor
		startSender()
}

fun main() {
	println("BEGINS CPU=$cpus ${curThread()}")
	runBlocking{
		actorsSenderReceiver()
		(receiverActor as Job).join()
		//delay(2000)  //DA EVITARE ...
	}
	println("ENDS main ${curThread()}")
}