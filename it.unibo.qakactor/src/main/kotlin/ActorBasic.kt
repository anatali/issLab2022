package it.unibo.kactor

import alice.tuprolog.*
import it.unibo.`is`.interfaces.protocols.IConnInteraction
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.eclipse.californium.core.CoapClient
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED
import org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED
import org.eclipse.californium.core.coap.MediaTypeRegistry


/*
    Implements an abstract actor able to receive an ApplMessage and
    to delegate its processing to the abstract method actorBody
 */

abstract class  ActorBasic(  name:         String,
                           val scope:        CoroutineScope = GlobalScope,
                           var discardMessages : Boolean = false, 
                           val confined :    Boolean = false,
                           val ioBound :     Boolean = false,
                           val channelSize : Int = 50
                        ) : CoapResource(name), MqttCallback {

    //val cpus = Runtime.getRuntime().availableProcessors();

    val tt      = "               %%% "
    var context : QakContext? = null  //to be injected
    var resVar  : String ="fail"      // see solve
    val pengine     = Prolog()      //USED FOR LOCAL KB
    val NoMsg       = MsgUtil.buildEvent(name, "local_noMsg", "noMsg")
				
    val mqtt        = MqttUtils(name)
    protected val subscribers = mutableListOf<ActorBasic>()
    var mqttConnected = false
    protected var count = 1;

    protected lateinit var currentSolution : SolveInfo
    protected lateinit var currentProcess  : Process
 
//    private var timeAtStart: Long = 0

    internal val requestMap : MutableMap<String, IApplMessage> = mutableMapOf<String, IApplMessage>()  //Oct2019

    private   var logo             : String 	        //Coap Jan2020
    protected var ActorResourceRep : String 			//Coap Jan2020
	protected var actorLogfileName  : String = ""
	protected var msgLogNoCtxDir   = "logs/noctx"
	protected var msgLogDir        = msgLogNoCtxDir
    
    init{                                   //Coap Jan2020
 		createMsglogFile()					//APR2020 : an Actor could have no context
        isObservable     = true
        logo             = "       ActorBasic(Resource) $name "
        ActorResourceRep = "$logo | created  "
        sysUtil.aboutThreads("$name CREATED - ")

    }
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    protected val dispatcher =
        if( confined ) sysUtil.singleThreadContext
        else  if( ioBound ) sysUtil.ioBoundThreadContext
              else sysUtil.cpusThreadContext

/*
 ===================================================
Message-driven kactor
 ===================================================
*/ 	
	
	open fun createMsglogFile(){
		actorLogfileName  = "${name}_MsLog.txt"
        sysUtil.createFile(actorLogfileName, dir = msgLogNoCtxDir)
	}
	fun createMsglogFileInContext(){	//called by QAkContext.addInternalActor when a context is injected
		if( context !== null ){
            sysUtil.deleteFile(actorLogfileName, dir = msgLogNoCtxDir)
			msgLogDir = "logs/${context!!.name}"
            sysUtil.createFile(actorLogfileName, dir = msgLogDir)
		}else println("ActorBasic $name | WARNING: createMsglogFileInContext you should never be here")	
	}
	
 	open fun writeMsgLog( msg: IApplMessage){ //APR2020
          if( context !== null ){ 
        	  //Update the log of the context
              sysUtil.updateLogfile(context!!.ctxLogfileName, "item($name,nostate,$msg).", dir = msgLogNoCtxDir)
		  }	
          //Update the log of the actor
        sysUtil.updateLogfile(actorLogfileName, "item($name,nostate,$msg).", dir = msgLogDir)
	}
	
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    @kotlinx.coroutines.ObsoleteCoroutinesApi
    val actor = scope.actor<IApplMessage>( dispatcher, capacity=channelSize ) {
        //println("ActorBasic $name |  RUNNING IN $dispatcher"  )
        for( msg in channel ) {
            sysUtil.traceprintln("$tt ActorBasic  $name |  msg= $msg ")
			writeMsgLog( msg )
			if( msg.msgContent() == "stopTheActor") {  channel.close() }
            else{
				 actorBody( msg )
            }
        }
    }
	
	
    //To be overridden by the application designer
    abstract suspend fun actorBody(msg : IApplMessage)
 
	
	
	fun setDiscard( v: Boolean){
		discardMessages = v
	}
    //fun setContext( ctx: QakContext ) //built-in
//    fun terminate(){
//        context!!.actorMap.remove(  name )
//        actor.close()
//    }
	
/*
TERMINATION 
*/		
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    fun terminate(arg: Int=0){
		println("$tt ActorBasic $name | terminates $arg ")
        if( context !== null ) context!!.actorMap.remove(  name )
        actor.close()
    }
    fun terminateCtx(arg: Int=0){
		println("$tt ActorBasic $name | terminateCtx $arg TODO ")
        //context!!.terminateTheContext()         
    }
@kotlinx.coroutines.ExperimentalCoroutinesApi
@kotlinx.coroutines.ObsoleteCoroutinesApi
	suspend fun waitTermination(){
		(actor as Job).join()
	}	
/*
--------------------------------------------
Messaging
--------------------------------------------
 */
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend open fun autoMsg(  msg : IApplMessage) {
     //println("ActorBasic $name | autoMsg $msg actor=${actor}")
        actor.send( msg )
    }

    //ADDED April 2022 to avoid loop
    fun sendMsgToMyself( msg : IApplMessage){
        scope.launch { autoMsg(msg) }
    }

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun autoMsg( msgId : String, msg : String) {
        actor.send(MsgUtil.buildDispatch(name, msgId, msg, this.name))
    }



    //Oct2019
@kotlinx.coroutines.ObsoleteCoroutinesApi 
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun sendMessageToActor(msg : IApplMessage, destName: String, conn : IConnInteraction? = null ) {
        //println("$tt ActorBasic sendMessageToActor | destName=$destName  ")
        if( context == null ){  //Defensive programming
            sysUtil.traceprintln("$tt ActorBasic sendMessageToActor |  no QakContext for the current actor")
            return
        }
        val destactor = context!!.hasActor(destName)
        if( destactor is ActorBasic) { //DESTINATION LOCAL
            //println("$tt ActorBasic sendMessageToActor | ${msg.msgId()}  dest=$destName LOCAL IN ${context!!.name}")
            destactor.actor.send( msg )
            return
        }
        val ctx = sysUtil.getActorContext(destName)
        if( ctx == null ) { //DESTINATION REMOTE but no context known
            sysUtil.traceprintln("$tt ActorBasic sendMessageToActor | ${msg.msgId()} dest=$destName REMOTE no context known ")
            if( conn != null ){ //we are sending an answer via TCP to an 'alien'
                sysUtil.traceprintln("$tt ActorBasic sendMessageToActor | dest=$destName sending answer  ${msg.msgId()} using $conn ")
                conn.sendALine( "$msg" )
                return
            }else{ //attempt to send the reply via mqtt hoping that the destName is mqtt-connected
                if( attemptToSendViaMqtt(context!!, msg,destName) ) return
                else {
                    println("$tt ActorBasic sendMessageToActor |  ${msg.msgId()} WARNING dest=$destName NON REACHABLE for $msg ")
                    return
                }
            }
        }//ctx of destination is unknwkn
 //HERE: DESTINATION remote, context of dest known 
        if( attemptToSendViaMqtt(ctx, msg,destName) ) return
		
		if( ! msg.isRequest() && ! msg.isReply() ){
	        val uri = "coap://${ctx.hostAddr}:${ctx.portNum}/${ctx.name}/$destName"
	        //println("$tt ActorBasic sendMessageToActor qak | ${uri} msg=$msg" )
	
	        //if( attemptToSendViaMqtt(ctx, msg,destName) ) return  //APR2020
	        sendCoapMsg( uri, msg.toString() )
		}
 //DESTINATION remote, context of destName known and NO MQTT  => using proxy
        // REMOVED: Coap 2020 but not for request
        else{	//request
	        val proxy = context!!.proxyMap.get(ctx.name)
            sysUtil.traceprintln("$tt ActorBasic sendMessageToActor |  ${msg.msgId()} $destName REMOTE with PROXY  ")
	        //WARNING: destName must be the original and not the proxy
	        if( proxy is ActorBasic) { proxy.actor.send( msg ) }
	        else println("$tt WARNING. ActorBasic  sendMessageToActor |  ${msg.msgId()} proxy of $ctx is null ")
		}
         
    }

    fun attemptToSendViaMqtt(ctx : QakContext, msg : IApplMessage, destName : String) : Boolean{
        //sysUtil.traceprintln("$tt ActorBasic attemptToSendViaMqtt | $msg}" )
        if( ctx.mqttAddr.length > 0  ) {
            if( ! mqttConnected ){
                mqtt.connect(name, ctx.mqttAddr)
                mqttConnected = true
            }
            sysUtil.traceprintln("$tt ActorBasic sendViaMqtt | destName=$destName : $msg")
            //mqtt.sendMsg(msg, "unibo/qak/$destName")
			mqtt.publish( "unibo/qak/$destName", msg.toString() )
            return true
        }
        return false
    }
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun forward( msgId : String, msg: String, destName: String) {
        val m = MsgUtil.buildDispatch(name, msgId, msg, destName)
        sendMessageToActor( m, destName)
     }//forward
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun request( msgId : String, msg: String, destActor: ActorBasic) {
        //println("       ActorBasic $name | forward $msgId:$msg to ${destActor.name} in ${sysUtil.curThread() }")
        destActor.actor.send(MsgUtil.buildRequest(name, msgId, msg, destActor.name))
    }
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun request( msgId : String, msg: String, destName: String) {
        val m = MsgUtil.buildRequest(name, msgId, msg, destName)
        sendMessageToActor( m, destName)
     }//request

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun answer( reqId: String, msgId : String, msg: String) {
    sysUtil.traceprintln("$tt ActorBasic $name | answer $msgId:$msg  }")
        val reqMsg = requestMap.remove(reqId) //one request, one reply
        if( reqMsg == null ){
            println("$tt ActorBasic $name | WARNING: answer $msgId INCONSISTENT: no request found ")
            return
        }
        val destName = reqMsg.msgSender()
        //println("$tt ActorBasic $name | answer destName=$destName  }")
        val m = MsgUtil.buildReply(name, msgId, msg, destName)
        sendMessageToActor( m, destName, (reqMsg as ApplMessage).conn )
    }//answer

/*
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun replyreq( reqId: String, reqestmsgId : String, msg: String) {
        //println(" $tt ActorBasic $name | replyreq $reqId related to request:$reqestmsgId content=$msg  ")
        val reqMsg = requestMap.get(reqestmsgId)		//WHY NOT remove?
        if( reqMsg == null ){
            println("$tt ActorBasic $name | WARNING: replyreq to $reqestmsgId INCONSISTENT: no request found ")
            return
        }
        val destName = reqMsg.msgSender()
        val m = MsgUtil.buildReplyReq(name, reqId, msg, destName)
        sendMessageToActor( m, destName, reqMsg.conn )
    }
*/
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emit(ctx: QakContext, event : IApplMessage) {  //used by NodeProxy
    sysUtil.traceprintln("$tt ActorBasic $name | emit from proxy ctx= ${ctx.name} ")
         ctx.actorMap.forEach {
			val ctxName  = it.key
			if(  ctx.name != ctxName ){
	            val destActor = it.value
                sysUtil.traceprintln("$tt ActorBasic $name | PROPAGATE ${event.msgId()} locally to ${destActor.name} ")
	            destActor.actor.send(event)
			}
        }
    }

//ADDED MAY2020	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emitWithDelay(  evId: String, evContent: String, dt : Long = 0L ) {
		scope.launch{
			delay( dt )
			val event = MsgUtil.buildEvent(name, evId, evContent)
			emit( event, avatar=true )			
		}
	}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emit(event : IApplMessage, avatar : Boolean = false ) {
	//avatar=true means that the emitter is able to sense the event that emits
        if( context == null ){
             println("$tt ActorBasic $name | WARNING emit: actor has no QakContext. ")
             this.actor.send(event)  //AUTOMSG
             return
        }
        //PROPAGATE TO LOCAL ACTORS
        if( context!!.mqttAddr.length == 0  //There is NO MQTT for this context
            ||  //the event is local
            event.msgId().startsWith("local")  ) {
            context!!.actorMap.forEach {
                val destActor = it.value
                //do not propagate the event to the emitter!!!!
                if( destActor.name != this.name ||  avatar) {
                    //sysUtil.traceprintln(" $tt ActorBasic $name | PROPAGATE ${event.msgId()} locally to ${destActor.name} " )
                    destActor.actor.send(event)
                }
            }
        }
        //PROPAGATE TO REMOTE ACTORS
        if( event.msgId().startsWith("local")) return       //local_ => no propagation
        //EMIT VIA MQTT IF there is
        if( context!!.mqttAddr.length != 0 ) {
//            sysUtil.trace
			println("$tt ActorBasic $name | emit MQTT ${event.msgId()}  on ${sysUtil.getMqttEventTopic()}")
            //mqtt.sendMsg(event, sysUtil.getMqttEventTopic())
			mqtt.publish(sysUtil.getMqttEventTopic(), event.toString() )  //Are perceived also by the emitter
        }
        //sysUtil.traceprintln(" $tt ActorBasic $name | ctxsMap SIZE = ${sysUtil.ctxsMap.size}")

		//APR2020: we do not look anymore at the ctxsMap since we have introduced sysUtil.connActive for TCP connections
	
        sysUtil.ctxsMap.forEach{
            val ctxName  = it.key
            //val ctx      = it.value
            sysUtil.traceprintln("$tt ActorBasic $name | ${context!!.name} try to propagate event ${event.msgId()} to ${ctxName}  ")
            val proxy  = context!!.proxyMap.get(ctxName)
            if( proxy is ActorBasic && proxy.name != this.context!!.name ){
                sysUtil.traceprintln("$tt ActorBasic $name | emit ${event}  towards $ctxName ")
                proxy.actor.send( event )    //Propagate via proxy THAT MUST exist if we know the context
            } 

        //sysUtil.traceprintln(" $tt ActorBasic $name | emit ${event.msgId()}  ENDS")
        }
	
        // DEC2019 propagate on TCP connections
        //APR2020 : no more events to aliens on TCP - only via MQTT
//        sysUtil.connActive.forEach {
//            println(" $tt ActorBasic $name | emit ${event.msgId()} on active conn: $it")
//            it.sendALine(event.toString() )
//        }
}

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emit( msgId : String, msg : String) {
        val event = MsgUtil.buildEvent(name, msgId, msg)
        emit( event )
    }


/*
 --------------------------------------------
 OBSERVABLE
 --------------------------------------------
*/
    fun subscribe( a : ActorBasic) : ActorBasic {
        subscribers.add(a)
    sysUtil.traceprintln(" $tt ActorBasic $name | subscribe ${a.name} ")
        return a
    }
    fun subscribeLocalActor( actorName : String) : ActorBasic {
        val a = sysUtil.getActor(actorName)
        if( a != null  ){ subscribers.add(a); return a}
        else{ println("WARNING: actor $actorName not found" );
            throw Exception("actor $actorName not found in the current context")
        }
    }
    fun unsubscribe( a : ActorBasic) {
        subscribers.remove(a)
    }
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emitLocalStreamEvent(ev: String, evc: String ){
        emitLocalStreamEvent(MsgUtil.buildEvent(name, ev, evc))
    }
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    suspend fun emitLocalStreamEvent(v: IApplMessage){
        subscribers.forEach {
            sysUtil.traceprintln(" $tt ActorBasic $name | emitLocalStreamEvent $it $v ");
            it.actor.send(v)
		}
    }


/*
--------------------------------------------
MQTT
--------------------------------------------
 */
    fun checkMqtt(){
        if( context!!.mqttAddr.length > 0  ){
            mqtt.connect(name,context!!.mqttAddr)
            mqttConnected = true
            mqtt.subscribe(this, "unibo/qak/$name")
            mqtt.subscribe( this, sysUtil.getMqttEventTopic())
        }
    }
    fun removeFromMqtt(){
        if( context!!.mqttAddr.length > 0  ){
            mqtt.disconnect()
            mqttConnected = false
        }
    }

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    override fun messageArrived(topic: String, msg: MqttMessage) {
        //sysUtil.traceprintln("$tt ActorBasic $name |  MQTT messageArrived on "+ topic + ": "+msg.toString());
        val m = ApplMessage( msg.toString() )
    sysUtil.traceprintln("$tt ActorBasic $name |  MQTT ARRIVED on $topic  m=$m ${actor}")
        //this.scope.launch{ actor.send( m ) }
        GlobalScope.launch{ actor.send( m ) }
    }
    override fun connectionLost(cause: Throwable?) {
        println("$tt ActorBasic $name |  MQTT connectionLost $cause " )
    }
    override fun deliveryComplete(token: IMqttDeliveryToken?) {
//		println("       ActorBasic $name |  deliveryComplete token= "+ token );
    }
/*
For direct usage without qak
 */
    fun connectToMqttBroker( mqttAddr : String){
        mqtt.connect(name, mqttAddr )
    }

    fun publish( msg: String, topic: String ){
        mqtt.publish( topic, msg, 1, false);
    }

    fun subscribe(  topic: String ) {
        mqtt.subscribe(this,topic)
    }


/*
--------------------------------------------
machineExec
--------------------------------------------
 */
    fun machineExec(cmd: String) : Process {
        try {
            return sysUtil.runtimeEnvironment.exec(cmd)
        } catch (e: Exception) {
            println("       ActorBasic $name | machineExec ERROR $e ")
            throw e
        }
    }
    fun getCurrentTime():Long {
        return System.currentTimeMillis()
    }

    fun getDuration(start: Long) : Long{
        val duration = (System.currentTimeMillis() - start) 
        //println("DURATION = $duration start=$start")
        return duration
    }

/*
KNOWLEDGE BASE
*/

    fun registerActor( ) {
        //	println("QActorUtils Regsitering in TuProlog ... " + this.getName()  );
        val lib = pengine.getLibrary("alice.tuprolog.lib.OOLibrary")
        //	println("QActorUtils Registering in TuProlog18 ... " + lib );
        val internalName = Struct("" + this.name)
        (lib as alice.tuprolog.lib.OOLibrary).register(internalName, this)
        //	println("QActorUtils Registered in TuProlog18 " + internalName );
    }

    fun solve( goal: String, rVar: String ="" ) {
        //println("       ActorBasic $name | solveGoal ${goal} rVar=$rVar" );
        val sol = pengine.solve( "$goal.")
        currentSolution = sol
        if(  sol.isSuccess  ) {
            if( (rVar != "") ) {
                val resStr = sol.getVarValue(rVar).toString()
                resVar = sysUtil.strCleaned(resStr)

            }else resVar = "success"
        } else resVar = "fail"
    }
    fun solveOk() : Boolean{
        return resVar != "fail"
    }
    fun getCurSol(v : String) : Term {
        if(currentSolution.isSuccess )
            return currentSolution.getVarValue( v )
        else return Term.createTerm("no(more)solution")
    }

/*
=======================================================================
     About CoAP: Jan 2020
=======================================================================
*/

    fun updateResourceRep( v : String){
         ActorResourceRep = v
         //println("%%%  updateResourceRep " + v)
         changed()             //DO NOT FORGET!!!
    }
    fun geResourceRep() : String{
		return ActorResourceRep
	}

    override fun handleGET(exchange: CoapExchange) {
        sysUtil.traceprintln("$logo | handleGET from: ${exchange.sourceAddress} arg: ${exchange.requestText}")
        exchange.respond( "$ActorResourceRep")
    }
    /*
     * POST is NOT idempotent.	Use POST when you want to add a child resource
     */
    override fun handlePOST(exchange: CoapExchange) {
        exchange.respond( "POST not implemented")
    }

/*
 * PUT method is idempotent. Use PUT when you want to modify
 */
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    override fun handlePUT(exchange: CoapExchange) {
        val arg = exchange.requestText
    sysUtil.traceprintln("$logo | handlePUT arg=$arg")
        try{
            val msg    = ApplMessage( arg )
            fromPutToMsg( msg, exchange )   
        }catch( e : Exception){
            updateResourceRep("error on msg $arg")
            println("$logo | handlePUT ERROR on msg ")
        }
    }

    override fun handleDELETE(exchange: CoapExchange) {
        delete()
        exchange.respond(DELETED)
    }

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    fun fromPutToMsg(msg : IApplMessage, exchange: CoapExchange ) {
    sysUtil.traceprintln("$logo | fromPutToMsggg msg=$msg")
        if( msg.isDispatch() || msg.isEvent() ) {
            scope.launch { autoMsg(msg) }
            exchange.respond( CHANGED )
            return
        }
        if( msg.isRequest() ) {
            //println("$logo | fromPutToMsg request=$msg")
            CoapToActor("caoproute${count++}", exchange, this, msg)
         }
     }



    fun sendCoapMsg(  url : String, msg : String   ){
        sysUtil.traceprintln("$logo |   sendCoapMsg url=${url}")
        val client = CoapClient(url)
        val resp = client.put(msg, MediaTypeRegistry.TEXT_PLAIN) //: CoapResponse
        sysUtil.traceprintln("$logo |   sendCoapMsg resp =${resp.getCode()}")
    }


}

