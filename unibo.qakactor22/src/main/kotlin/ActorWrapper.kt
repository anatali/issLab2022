package it.unibo.kactor

import kotlinx.coroutines.*
 
 

abstract class ActorWrapper( name: String) :
	ActorBasic(name, scope, false, true, false, 50) {

    companion object {
		val d = newSingleThreadContext("single")
		private val scope = CoroutineScope( d  )
        fun setTrace(){
            sysUtil.trace=true;
            sysUtil.aboutThreads("ActorWrapper - ");
        }
    }
	
    override
    suspend fun actorBody( msg: IApplMessage ){
          doJob(msg)
    }


    protected abstract fun doJob(msg: IApplMessage)
}