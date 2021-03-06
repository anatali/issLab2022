/* Generated by AN DISI Unibo */ 
package it.unibo.pathexec

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Pathexec ( name: String, scope: CoroutineScope  ) : ActorBasicFsm( name, scope ){

	override fun getInitialState() : String{
		return "s0"
	}
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		 var CurMoveTodo = ""    //Upcase, since var to be used in guards
		   var MapStr      = ""
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						  CurMoveTodo = ""  
						println("pathexec ready ...")
					}
					 transition(edgeName="t00",targetState="doThePath",cond=whenRequest("dopath"))
					transition(edgeName="t01",targetState="setTheMap",cond=whenDispatch("setMap"))
					transition(edgeName="t02",targetState="s0",cond=whenDispatch("pathreset"))
				}	 
				state("setTheMap") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 val Payload = currentMsg.msgContent(); println("payload=$Payload")  
						if( checkMsgContent( Term.createTerm("setMap(MapStr)"), Term.createTerm("M"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 MapStr = payloadArg(0)
												println("map=$MapStr")  
						}
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
				state("doThePath") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("dopath(PATH,MAP)"), Term.createTerm("dopath(PATH,MAP)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 println("xxxxxxxxxxxxxxxxxxxxxxxx")
												val path = payloadArg(0)
												println("path=$path") 
											   pathut.setPath(path) 
											   val map = payloadArg(1); 
												println("map=$map")  
						}
						println("pathexec pathTodo = ${pathut.getPathTodo()}")
					}
					 transition( edgeName="goto",targetState="nextMove", cond=doswitch() )
				}	 
				state("nextMove") { //this:State
					action { //it:State
						 CurMoveTodo = pathut.nextMove()  
						println("pathexec curMoveTodo=$CurMoveTodo")
					}
					 transition( edgeName="goto",targetState="endWorkOk", cond=doswitchGuarded({ CurMoveTodo.length == 0  
					}) )
					transition( edgeName="goto",targetState="doMove", cond=doswitchGuarded({! ( CurMoveTodo.length == 0  
					) }) )
				}	 
				state("doMove") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						delay(300) 
					}
					 transition( edgeName="goto",targetState="doMoveW", cond=doswitchGuarded({ CurMoveTodo == "w"  
					}) )
					transition( edgeName="goto",targetState="doMoveTurn", cond=doswitchGuarded({! ( CurMoveTodo == "w"  
					) }) )
				}	 
				state("doMoveTurn") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						forward("cmd", "cmd($CurMoveTodo)" ,"basicrobot" ) 
						stateTimer = TimerActor("timer_doMoveTurn", 
							scope, context!!, "local_tout_pathexec_doMoveTurn", 300.toLong() )
					}
					 transition(edgeName="t03",targetState="nextMove",cond=whenTimeout("local_tout_pathexec_doMoveTurn"))   
					transition(edgeName="t04",targetState="s0",cond=whenDispatch("pathreset"))
				}	 
				state("doMoveW") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						request("step", "step(350)" ,"basicrobot" )  
					}
					 transition(edgeName="t05",targetState="endWorkKo",cond=whenEvent("alarm"))
					transition(edgeName="t06",targetState="s0",cond=whenDispatch("pathreset"))
					transition(edgeName="t07",targetState="nextMove",cond=whenReply("stepdone"))
					transition(edgeName="t08",targetState="endWorkKo",cond=whenReply("stepfail"))
				}	 
				state("handleAlarm") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 var PathTodo = pathut.getPathTodo()  
						println("pathexec handleAlarm ... pathTodo=$PathTodo")
					}
				}	 
				state("endWorkOk") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						println("PATH DONE - BYE")
						answer("dopath", "dopathdone", "dopathdone(ok)"   )  
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
				state("endWorkKo") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						 var PathStillTodo = pathut.getPathTodo()  
						println("PATH FAILURE - SORRY. PathStillTodo=$PathStillTodo")
						answer("dopath", "dopathfail", "dopathfail($PathStillTodo)"   )  
					}
					 transition( edgeName="goto",targetState="s0", cond=doswitch() )
				}	 
			}
		}
}
