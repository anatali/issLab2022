package it.unibo.kactor

import alice.tuprolog.Struct
import alice.tuprolog.Term
import it.unibo.`is`.interfaces.protocols.IConnInteraction



open class ApplMessage:  IApplMessage{
    //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )
    protected var msgId: String = ""
    protected var msgType: String = ""
    protected var msgSender: String = ""
    protected var msgReceiver: String = ""
    protected var msgContent: String = ""
    protected var msgNum: Int = 0

    var conn : IConnInteraction? = null   //Oct2019

    val term: Term
        get() = if (msgType == "")
            Term.createTerm("msg(none,none,none,none,none,0)")
        else
            Term.createTerm(msgContent)
    constructor(MSGID: String, MSGTYPE: String, SENDER: String, RECEIVER: String,
                CONTENT: String, SEQNUM: String)   {
        msgId = MSGID
        msgType = MSGTYPE
        msgSender = SENDER
        msgReceiver = RECEIVER
        msgContent = envelope(CONTENT)
        msgNum = Integer.parseInt(SEQNUM)
        conn   = null
     }
        //@Throws(Exception::class)
    constructor( MSGID: String, MSGTYPE: String, SENDER: String, RECEIVER: String,
                 CONTENT: String, SEQNUM: String, connection : IConnInteraction )  {
        msgId = MSGID
        msgType = MSGTYPE
        msgSender = SENDER
        msgReceiver = RECEIVER
        msgContent = envelope(CONTENT)
        msgNum = Integer.parseInt(SEQNUM)

        conn   = connection //Oct2019 It is NOT NULL for a request
        //		System.out.println("ApplMessage " + MSGID + " " + getDefaultRep() );
    }

    //@Throws(Exception::class)
    constructor(msg: String)   {
        val msgStruct = Term.createTerm(msg) as Struct
        setFields(msgStruct)
    }

    private fun setFields(msgStruct: Struct) {
        msgId = msgStruct.getArg(0).toString()
        msgType = msgStruct.getArg(1).toString()
        msgSender = msgStruct.getArg(2).toString()
        msgReceiver = msgStruct.getArg(3).toString()
        msgContent = msgStruct.getArg(4).toString()
        msgNum = Integer.parseInt(msgStruct.getArg(5).toString())
    }

    override fun msgId(): String {
        return msgId
    }

    override fun msgType(): String {
        return msgType
    }

    override fun msgSender(): String {
        return msgSender
    }

    override fun msgReceiver(): String {
        return msgReceiver
    }

    override fun msgContent(): String {
        return msgContent
    }

    override fun msgNum(): String {
        return "" + msgNum
    }

    override fun toString(): String {
        return getDefaultRep()
    }

    override fun isEvent(): Boolean{
        return msgType == ApplMessageType.event.toString()
    }
    override fun isDispatch(): Boolean{
        return msgType == ApplMessageType.dispatch.toString()
    }
    override fun isRequest(): Boolean{
        return msgType == ApplMessageType.request.toString()
    }
    override fun isReply(): Boolean{
        return msgType == ApplMessageType.reply.toString()
    }
    override fun isInvitation(): Boolean{
        return msgType == ApplMessageType.invitation.toString()
    }
    /**/
    fun getDefaultRep(): String {
        return if (msgType == null)
            "msg(none,none,none,none,none,0)"
        else
            "msg($msgId,$msgType,$msgSender,$msgReceiver,$msgContent,${msgNum()})"
    }

    protected fun envelope(content: String): String {
        try {
            val tt = Term.createTerm(content)
            return tt.toString()
        } catch (e: Exception) {
            return "'$content'"
        }
    }

}//ApplMessage
