/*
 MainActorBasicDemo
 */
package it.unibo.kactor

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import java.io.File


class ApplActor( name:String, scope: CoroutineScope) : ActorBasic(name, scope){
override suspend fun actorBody(msg : IApplMessage){
 			File("${name}_MsgLog.txt").appendText("${msg}\n")
	}	
}
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
fun main() = runBlocking {
	println("MainActorBasicDemo START")
	sysUtil.logMsgs = true
	//val qaappl = ApplActor("qaappl", this)
	//MsgUtil.sendMsg("cmd", "cmd(w)", qaappl)
	//qaappl.emit("alarm", "alarm(fire)")

	val a = Demo("demo",this)
	delay(500)
	MsgUtil.sendMsg("msg1", "msg1(hello)", a)
	delay(1000)
	MsgUtil.sendMsg("msg2", "msg2(helloAgain)", a)
	delay(1000)
	println("MainActorBasicDemo END")
	System.exit(0)
}