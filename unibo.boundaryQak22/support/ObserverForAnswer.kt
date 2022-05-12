 
import com.andreapivetta.kolor.Color
import it.unibo.actor0.ActorBasicKotlin
import it.unibo.actor0.ApplMessage
import kotlinx.coroutines.CoroutineScope

class ObserverForAnswer(
    name: String, scope:CoroutineScope,
    val callback: ( String ) -> Unit 
    ) : ActorBasicKotlin(name, scope) {

    override suspend fun handleInput(msg : ApplMessage){
        colorPrint("$name  msg=$msg", Color.MAGENTA)
            callback( msg.msgContent )
    }


}