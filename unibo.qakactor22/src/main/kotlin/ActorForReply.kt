package it.unibo.kactor

import unibo.comm22.interfaces.IApplMsgHandler
import unibo.comm22.interfaces.Interaction2021
import unibo.comm22.utils.ColorsOut

class ActorForReply( name:  String,
                     val h: IApplMsgHandler, val conn: Interaction2021 ) : ActorBasic( name ) {

    override suspend fun actorBody(msg: IApplMessage) {
        ColorsOut.outappl(name + " | actorBody $msg conn= $conn", ColorsOut.MAGENTA);
        if( msg.isReply() ) h.sendAnswerToClient(msg.toString(), conn);
        context!!.removeInternalActor(this);
    }

}