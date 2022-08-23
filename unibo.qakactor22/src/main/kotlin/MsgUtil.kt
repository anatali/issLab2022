package it.unibo.kactor

import unibo.comm22.interfaces.Interaction2021
import unibo.comm22.tcp.TcpConnection
import unibo.comm22.udp.UdpConnection
import java.net.DatagramSocket
import java.net.Socket
import unibo.comm22.udp.UdpEndpoint
import java.net.InetAddress
import jssc.SerialPort
import unibo.comm22.serial.SerialConnection
import unibo.comm22.utils.ColorsOut


//FILE MsgUtil.kt

//import  unibo.comm22.interfaces.Interaction2021
//import it.unibo.supports.FactoryProtocol

enum class Protocol {
    SERIAL, TCP, UDP, BTH
}

 
object MsgUtil {
var count = 1;
@JvmStatic
fun buildDispatch( actor: String, msgId : String ,
                       content : String, dest: String ) : IApplMessage {
        return ApplMessage(msgId, ApplMessageType.dispatch.toString(),
            actor, dest, "$content", "${count++}")
    }
@JvmStatic
fun buildRequest( actor: String, msgId : String ,
                       content : String, dest: String ) : IApplMessage {
        return ApplMessage(msgId, ApplMessageType.request.toString(),
            actor, dest, "$content", "${count++}")
    }
@JvmStatic
fun buildReply( actor: String, msgId : String ,
                      content : String, dest: String ) : IApplMessage {
        return ApplMessage(msgId, ApplMessageType.reply.toString(),
            actor, dest, "$content", "${count++}")
    }
@JvmStatic
fun buildReplyReq( actor: String, msgId : String ,
                    content : String, dest: String ) : IApplMessage {
        return ApplMessage(msgId, ApplMessageType.request.toString(),
            actor, dest, "$content", "${count++}")
    }
@JvmStatic
fun buildEvent( actor: String, msgId : String , content : String  ) : IApplMessage {
        return ApplMessage(msgId, ApplMessageType.event.toString(),
            actor, "none", "$content", "${count++}")
    }
	



@JvmStatic
suspend fun sendMsg( sender : String, msgId: String, msg: String, destActor: ActorBasic) {
        val dispatchMsg = buildDispatch(sender, msgId, msg, destActor.name)
        //println("sendMsg $dispatchMsg")
        destActor.actor.send( dispatchMsg )
    }


@JvmStatic
suspend fun sendMsg(msg: IApplMessage, destActor: ActorBasic) {
        destActor.actor.send(msg)
    }


@JvmStatic
suspend fun sendMsg(msgId: String, msg: String, destActor: ActorBasic) {
        val dispatchMsg = buildDispatch("any", msgId, msg, destActor.name)
        //println("sendMsg $dispatchMsg")
        destActor.actor.send(dispatchMsg)
    }




@JvmStatic
suspend fun sendMsg(  sender: String, msgId : String, payload: String, destName : String, mqtt: MqttUtils ){
		val msg = buildDispatch(actor=sender, msgId=msgId , content=payload, dest=destName )
		if( mqtt.connectDone() ){
			mqtt.publish( "unibo/qak/${destName}", msg.toString() )
		}
	}
	
/*
@JvmStatic
fun getFactoryProtocol(protocol: Protocol) : FactoryProtocol?{
        var factoryProtocol : FactoryProtocol? = null
        when( protocol ){
            Protocol.SERIAL -> println("MsgUtil WARNING: TODO")
            Protocol.TCP , Protocol.UDP -> factoryProtocol =
                FactoryProtocol(null, "$protocol", "actor")
            else -> println("MsgUtil WARNING: protocol unknown")
        }
        return factoryProtocol
    }

@JvmStatic    fun getConnection(protocol: Protocol, hostName: String, portNum: Int, clientName:String) : Interaction2021? {
        when( protocol ){
            Protocol.TCP , Protocol.UDP -> {
                val factoryProtocol = FactoryProtocol(null, "$protocol", clientName)
                try {
                    val conn = factoryProtocol.createClientProtocolSupport(hostName, portNum)
                    return conn
                }catch( e: Exception ){
                    //println("MsgUtil: NO conn to $hostName ")
                    return null
                }
            }
            else -> {
                 return null
            }
        }
    }
*/
    @JvmStatic
    fun getConnection(protocol: Protocol, hostName: String, portNum: Int, clientName:String) : Interaction2021? {
        when( protocol ){
            Protocol.TCP -> {
                //val factoryProtocol = FactoryProtocol(null, "$protocol", clientName)
                try {
                    val socket = Socket(hostName, portNum)
                    val conn   = TcpConnection(socket)
                    return conn
                }catch( e: Exception ){
                    //println("MsgUtil: NO conn to $hostName ")
                    return null
                }
            }
            Protocol.UDP -> {
                val socket = DatagramSocket()
                val address  = InetAddress.getByName(hostName)
                val endpoint = UdpEndpoint(address, portNum)
                val conn: Interaction2021 = UdpConnection(socket, endpoint)
                return conn
            }
            else -> {
                return null
            }
        }
    }


@JvmStatic
fun getConnectionSerial( portName: String, rate: Int) : Interaction2021 {
    //val  factoryProtocol =  FactoryProtocol(null,"${Protocol.SERIAL}",portName)
    val DATA_RATE   = 115200
    val serialPort  =  SerialPort(portName);
    serialPort.openPort();
    serialPort.setParams(DATA_RATE, SerialPort.DATABITS_8,
        SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
    val conn = SerialConnection(serialPort)
    //val conn = factoryProtocol.createSerialProtocolSupport(portName)
    return conn
}

@JvmStatic
fun strToProtocol( ps: String):Protocol{
        //var p: Protocol
        when( ps.toUpperCase() ){
            Protocol.TCP.toString() -> return Protocol.TCP
            Protocol.UDP.toString() -> return Protocol.UDP
            Protocol.SERIAL.toString() -> return Protocol.SERIAL
            else -> return Protocol.TCP
        }
     }
    @JvmStatic
    fun outgreen( msg: String) { ColorsOut.outappl(msg, ColorsOut.GREEN) }
    @JvmStatic
    fun outblue( msg: String) {  ColorsOut.outappl(msg, ColorsOut.BLUE)  }
    @JvmStatic
    fun outred( msg: String) { ColorsOut.outappl(msg, ColorsOut.RED)  }
    @JvmStatic
    fun outmagenta( msg: String) {  ColorsOut.outappl(msg, ColorsOut.MAGENTA) }

}