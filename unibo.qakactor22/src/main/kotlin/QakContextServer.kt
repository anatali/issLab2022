package it.unibo.kactor

import  unibo.comm22.interfaces.Interaction2021
import kotlinx.coroutines.*
import unibo.comm22.NaiveApplHandler
import unibo.comm22.interfaces.IApplMsgHandler
import unibo.comm22.tcp.TcpServer
import unibo.comm22.utils.ColorsOut
import unibo.comm22.utils.CommSystemConfig
import java.net.ServerSocket
import java.net.Socket

/*
Works at node level
*/

class QakContextServer(val ctx: QakContext, scope: CoroutineScope,
                       name:String, val protocol: Protocol ) : ActorBasic( name, scope) {
    protected var hostName: String? = null
    //protected var factoryProtocol: FactoryProtocol?  //June2022
 
    //val connMap : MutableMap<Int, Interaction2021> = mutableMapOf<Int, Interaction2021>() //Oct2019

    init {
        System.setProperty("inputTimeOut", QakContext.workTime.toString() )  //100 min	
        //factoryProtocol = MsgUtil.getFactoryProtocol(protocol) //June2022
        scope.launch(Dispatchers.IO) {
            autoMsg( "start", "startQakContextServer" )
        }
        sysUtil.aboutThreads("QakContextServer $name scope=$scope | AFTER init   " );
    }




    override suspend fun actorBody(msg : IApplMessage){
        println("               %%% QakContextServer $name | READY TO RECEIVE TCP CONNS on ${ctx.portNum} ")
        waitForConnection()
    }



    suspend protected fun waitForConnection() {
        //We could handle several connections
        GlobalScope.launch(Dispatchers.IO) {
            try {
                MsgUtil.outblue("%%% QakContextServer $name | waitForConnection protocol=${protocol == Protocol.TCP}");
                //while (true) {
                    //println("       QakContextServer $name | WAIT FOR CONNECTION")
                    //val conn = factoryProtocol!!.createServerProtocolSupport(ctx.portNum) //BLOCKS
                    //var conn : Interaction2021
                    if( protocol == Protocol.TCP ){
                        val userDefHandler = ContextMsgHandler("${ctx.name}MsgH", ctx)
                        //MsgUtil.outgreen(name + " | waitForConnection $userDefHandler" );
                        val server = TcpServer("tcpSrv",ctx.portNum,userDefHandler)
                        CommSystemConfig.tracing = true
                        server.activate()
                        //ColorsOut.outappl(name + " | waitForConnection $server on ${ctx.portNum}", ColorsOut.YELLOW);
                        //val serverSocket: ServerSocket = tcpSupport.connectAsReceiver(portNum)
                    //}
                    //sysUtil.connActive.add(conn)      //TODO
                    //handleConnection( conn )          //TODO: create userDefHandler
                }
            } catch (e: Exception) {
                 println("      QakContextServer $name | WARNING: ${e.message}")
            }
        }
    }
/*
EACH CONNECTION WORKS IN ITS OWN COROUTINE
 */

    //June2002: sarebbe un userDefHandler
/*
    suspend protected fun handleConnection(conn: Interaction2021 ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                sysUtil.traceprintln("               %%% QakContextServer $name | NEWWWWWWWWWW conn:$conn")
                while (true) {
                    val msg = conn.receiveMsg()       //BLOCKING
                    sysUtil.traceprintln("               %%% QakContextServer  $name | receives:$msg in ${sysUtil.curThread()}")
                    if( msg != null ) {
                        val inputmsg = ApplMessage(msg)
						sysUtil.updateLogfile( actorLogfileName, inputmsg.toString(), dir=msgLogNoCtxDir )
                        if (inputmsg.msgType() == ApplMessageType.event.toString()) {
                            propagateEvent(inputmsg)
                            continue
                        }
                        val dest = inputmsg.msgReceiver()
                        val actor = ctx.hasActor(dest)
                        if (actor is ActorBasic) {
                            try {
                                if (inputmsg.msgType() == ApplMessageType.request.toString()) { //Oct2019
                                    //set conn in the msg to the actor
                                    inputmsg.conn = conn
                                }
                                MsgUtil.sendMsg(inputmsg, actor)
                            } catch (e1: Exception) {
                                println("               %%% QakContextServer $name |  ${e1.message}")
                            }
                        } else println("               %%% QakContextServer $name | WARNING!! no local actor ${dest} in ${ctx.name}")
                    }// msg != null
                    else{
                        conn.close()
                        sysUtil.connActive.remove(conn)
                        break
                    }
                }
            } catch (e: Exception) {
                println("               %%% QakContextServer $name | handleConnection: ${e.message}")
                sysUtil.connActive.remove(conn)
            }
        }
    }//handleConnection



    suspend fun propagateEvent(event : IApplMessage){
         ctx.actorMap.forEach{
             //sysUtil.traceprintln("       QakContextServer $name | in ${ctx.name} propag $event to ${it.key} in ${it.value.context.name}")
             val a = it.value
             try{
                 a.actor.send(event)
             }catch( e1 : Exception) {
                println("               %%% QakContextServer $name | propagateEvent WARNING: ${e1.message}")
             }
         }
    }

 */
}

