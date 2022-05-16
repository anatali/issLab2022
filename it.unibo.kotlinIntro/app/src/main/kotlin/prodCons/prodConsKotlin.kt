package prodCons
//prodConsKotlin.kt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consume
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope

 
@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
val context  = newSingleThreadContext("myThread")
lateinit var producer : ReceiveChannel<Any>

@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
fun createProducer(scope : CoroutineScope ){
    producer =
    scope.produce(context, 0){
		send(5.2)
		println( "producer sent 5.2 in ${curThread()}")
		send("a")
		println( "producer sent a   in ${curThread()}")
		send(100)
		println( "producer sent 100 in ${curThread()}")    }
}
@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
suspend fun doconsume(){
    val v = producer.receive()	//the first item
    println( "doconsume receives1 $v in ${curThread()}")
    producer.consumeEach { println( "doconsume receives2 $it in ${curThread()}") }
}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() {
    println("BEGINS CPU=$cpus ${kotlindemo.curThread()}")
    runBlocking {
        createProducer(this);
        doconsume()
        println("ENDS runBlocking ${kotlindemo.curThread()}")
    }
    println("ENDS main ${kotlindemo.curThread()}")
}
