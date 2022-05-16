package it.unibo.kactor

interface IApplMessage {
    fun msgId(): String
    fun msgType(): String
    fun msgSender(): String
    fun msgReceiver(): String
    fun msgContent(): String
    fun msgNum(): String
    fun isEvent(): Boolean
    fun isDispatch(): Boolean
    fun isRequest(): Boolean
    fun isInvitation(): Boolean
    fun isReply(): Boolean
}