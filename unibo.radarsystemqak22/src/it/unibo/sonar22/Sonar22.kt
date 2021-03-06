/* Generated by AN DISI Unibo */ 
package it.unibo.sonar22

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonar22 ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var goon = true  
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("${name} STARTS  ")
					}
					 transition(edgeName="t00",targetState="doActivate",cond=whenDispatch("sonaractivate"))
					transition(edgeName="t01",targetState="doDeactivate",cond=whenDispatch("sonardeactivate"))
				}	 
				state("doActivate") { //this:State
					action { //it:State
						 var D = 50 
								   for( i in 1..5){ 
						 		   	 unibo.actor22comm.utils.ColorsOut.outappl( 
											"${name} simulates $D", unibo.actor22comm.utils.ColorsOut.YELLOW)
								   	 emit( "sonardata", "distance( $D )" )
								   	 delay(500)
								   	 D = D - 5
								   }
								   for( i in 1..5){ 
								   	unibo.actor22comm.utils.ColorsOut.outappl( 
											"${name} simulates $D", unibo.actor22comm.utils.ColorsOut.YELLOW)
								   	emit( "sonardata",  "distance( $D )" )
								   	delay(500)
								   	D = D + 5
								   }
					}
				}	 
				state("doDeactivate") { //this:State
					action { //it:State
						println("${name} ENDS  ")
					}
				}	 
			}
		}
}
