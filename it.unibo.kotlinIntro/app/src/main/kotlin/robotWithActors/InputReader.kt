/*
============================================================
InputReader

============================================================
 */
package robotWithActors

import com.andreapivetta.kolor.Color
import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.actor0.ApplMessage
import it.unibo.actor0.sysUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import java.lang.Exception

object InputReader {
    lateinit var myrobot : SendChannel<String>

    //Factory method
    fun createInputReader( robot : SendChannel<String>, conn: IConnInteraction) {	//: Job
        myrobot = robot
        val inputScope = CoroutineScope( Dispatchers.IO )
        //Launch a coroutine to read data from the given conn
        inputScope.launch { getInput(  conn) }
    }//createTcpInputReader

    suspend fun getInput(  conn: IConnInteraction ) {
        while (true) {
            //println("	&&& InputReader  | waitInput ... $conn ${curThread()} "   );
            try {
                val v = conn.receiveALine()    //blocking ...
                sysUtil.colorPrint("InputReader  | $v", Color.BLUE);
                if (v != null) {
                    val msgAppl = ApplMessage.create(v)
                    val msg     = msgAppl.msgContent //.replace("@",",")
                    sysUtil.colorPrint("InputReader  | send $msg ", Color.BLUE);
                    myrobot.send(msg)	//inform myrobot about the result
                } else {
                    break
                }
            }catch( e : Exception){
                sysUtil.colorPrint("InputReader  | ERROR: ${e} ")
                break
            }
        } //while
    }
    
}