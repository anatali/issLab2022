package it.unibo.kactor

import kotlinx.coroutines.channels.SendChannel

interface IActorBasic{
    fun getChannel() : SendChannel<ApplMessage>
}