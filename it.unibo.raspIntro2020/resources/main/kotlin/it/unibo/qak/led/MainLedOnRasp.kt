package it.unibo.qak.led

import it.unibo.kactor.MsgUtil
import it.unibo.kactor.QakContext
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    QakContext.createContexts(
        "192.168.1.8",this,
        "ledOnRaspSysDescr.pl",
        "sysRules.pl"
    )

 }