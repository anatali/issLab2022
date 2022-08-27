/* Generated by AN DISI Unibo */ 
package it.unibo.cmdconsole

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Cmdconsole ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		val interruptedStateTransitions = mutableListOf<Transition>()
		 var N=0  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						 consolegui.StartStopGui( "boundaryqak30", 8032 )  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="alarmSimulation", cond=doswitch() )
				}	 
				state("alarmSimulation") { //this:State
					action { //it:State
						delay(3000) 
						emit("alarm", "alarm($N)" ) 
						 N = N+1  
						//genTimer( actor, state )
					}
					//After Lenzi Aug2002
					sysaction { //it:State
					}	 	 
					 transition( edgeName="goto",targetState="alarmSimulation", cond=doswitch() )
				}	 
			}
		}
}
