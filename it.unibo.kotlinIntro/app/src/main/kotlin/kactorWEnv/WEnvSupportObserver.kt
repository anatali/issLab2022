package kactorWEnv
import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import it.unibo.actor0.sysUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import org.json.JSONObject

class WEnvSupportObserver(name: String,
                          var master: SendChannel<String>, scope:CoroutineScope)
    : ActorBasicKotlin(name, scope) {

    override suspend fun handleInput(msg : ApplMessage){
        var msgJsonStr = msg.msgContent
        println("$name  | appl $msg  ${sysUtil.aboutThreads(name)}" )
        //println("$name  | msgJsonStr=$msgJsonStr   " )
        //val msgJson = JSONObject(msgJsonStr)
        //println("$name  | msgJson=$msgJson   " )
        master.send( msgJsonStr )
    }
}