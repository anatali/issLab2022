package prodCons
//prodManyConsKotlin.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce

@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
val aProducer : ReceiveChannel<Int> = GlobalScope.produce{
    for( i in 1..3 ){
        println( "aProducer produces $i in ${curThread()}")
        send( i )
    }
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
fun consumer1(scope: CoroutineScope){
    scope.launch{
        delay(100)
        val v = aProducer.receive()
        println( "consumer1 receives ${v} in ${curThread()}" )
     }
}
@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
fun consumer2(scope: CoroutineScope){
    scope.launch{
        for( i in 1..2 ) {
            val v = aProducer.receive()
            println("consumer2 receives ${v} in ${curThread()}")
            delay(100)
        }
    }
}

@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
fun main() {
    println("BEGINS CPU=${kotlindemo.cpus} ${kotlindemo.curThread()}")
    runBlocking {
        consumer1(this)
        consumer2(this)
        println("ENDS runBlocking ${kotlindemo.curThread()}")
    }
    println("ENDS main ${kotlindemo.curThread()}")
}
