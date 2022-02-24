package it.unibo.qak.sonar

import it.unibo.kactor.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader

class sonarOnRaspShow( name : String, scope: CoroutineScope) : ActorBasic( name, scope ) {

     override suspend fun actorBody(msg: ApplMessage) {
        when( msg.msgId() ){
            "start" -> scope.launch{  readInputData() }
            else -> println("   sonarShow $name |  receives $msg ")
        }
    }

    suspend fun readInputData(){
        val numData = 8
        var dataCounter = 1
        val p : Process = machineExec("sudo ./SonarAlone")
        val reader = BufferedReader(InputStreamReader(p.getInputStream()))
        while( true ){
             var data = reader.readLine()    //blocking
              println("data ${dataCounter++} = $data " )
             if( dataCounter % numData == 0 ) { //every numData ...
                //println("EMIT sonar($dataCounter, $data)"  )
                 val m = MsgUtil.buildEvent(name, "sonar", "sonar($dataCounter, $data)")
                emitLocalStreamEvent( m  )
                emit(m.msgId(), m.msgContent())
            }
        }

    }

}

fun main() = runBlocking {

    QakContext.createContexts(
        "localhost",this,
        "sonarSysDescr.pl",
        "sysRules.pl"
    )

    val sonar = QakContext.getActor("sonarShow")
    MsgUtil.sendMsg("start", "start", sonar!!)
}