package it.unibo.qak.led

import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
 Message-driven ActorBasic
 Message and events are not ecplicitly declared
 See ledalone.qak
 */
class LedOnRasp(name:String, scope: CoroutineScope) : ActorBasic( name, scope ){

    override suspend fun actorBody(msg : ApplMessage){
        //println("   $name |  receives msg= $msg ")
        when( msg.msgId() ){
            BlsCmds.LedCmd.id ->
                when( msg.msgContent() ){
                    "ledCmd(on)"  -> turnLedOn()
                    "ledCmd(off)" -> turnLedOff()
                    else -> println("   $name | DISCARD $msg")
                 }
            //else -> println("   $name |  ${msg.msgId()} UNKNOWN ")
        }
    }

    fun turnLedOn(){
        //println("   $name |  turnLedOn")
        this.machineExec("sudo bash led25GpioTurnOn.sh")
    }
    fun turnLedOff(){
        //println("   $name |  turnLedOff")
        this.machineExec("sudo bash led25GpioTurnOff.sh")
    }

}