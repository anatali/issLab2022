package it.unibo.kactor
import alice.tuprolog.Prolog
import alice.tuprolog.Struct
import alice.tuprolog.Term
import alice.tuprolog.Theory
import it.unibo.`is`.interfaces.protocols.IConnInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader


/*
ECLIPSE KOTLIN
https://dl.bintray.com/jetbrains/kotlin/eclipse-plugin/last/
*/

//A module in kotlin is a set of Kotlin files compiled together
object sysUtil{
	private val pengine = Prolog()
    internal val dispatchMap :  MutableMap<String,Struct> = mutableMapOf<String,Struct>()
	internal val ctxsMap :      MutableMap<String, QakContext> = mutableMapOf<String, QakContext>()
	internal val ctxActorMap :  MutableMap<String, ActorBasic> = mutableMapOf<String, ActorBasic>()
	val ctxOnHost =  mutableListOf<QakContext>()

	val runtimeEnvironment     = Runtime.getRuntime()
	val userDirectory          = System.getProperty("user.dir")
	val cpus                   = Runtime.getRuntime().availableProcessors()
	
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	val singleThreadContext    = newSingleThreadContext("qaksingle")
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	val ioBoundThreadContext   = newFixedThreadPoolContext(64, "qakiopool")
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	val cpusThreadContext      = newFixedThreadPoolContext(cpus, "qakcpuspool")

	var mqttBrokerIP : String? = ""
	var mqttBrokerPort : String? = ""
	var mqttBrokerEventTopic : String? = ""
	var trace   : Boolean = false
	var logMsgs : Boolean = false
	val connActive : MutableSet<IConnInteraction> = mutableSetOf<IConnInteraction>()    //Oct2019

	
@JvmStatic    	fun getMqttEventTopic() : String {
		if(mqttBrokerEventTopic !== null ) return mqttBrokerEventTopic!!
		else return "unibo/qak/events"
	}

@JvmStatic    	fun getPrologEngine() : Prolog = pengine
@JvmStatic    	fun curThread() : String = "thread=${Thread.currentThread().name}"

@JvmStatic    	fun getContext( ctxName : String ) : QakContext?  { return ctxsMap.get(ctxName.toLowerCase())}
@JvmStatic    	fun getActor( actorName : String ) : ActorBasic? { return ctxActorMap.get(actorName.toLowerCase())}

@JvmStatic    	fun getActorContextName( actorName : String): String?{
		val ctxName = solve( "qactor($actorName,CTX,_)", "CTX" )
		return ctxName
	}
	fun setActorContextName( actorName : String, ctxName : String ) {
		//if does not exists ...
		val res = solve( "qactor($actorName,$ctxName,_)", "" )
		if( res == "success") println("$actorName already set in $ctxName")
		else solve( "assertz( qactor($actorName,$ctxName,_) )", "" )
	}
@JvmStatic    	fun getActorContext ( actorName : String): QakContext?{
		val ctxName = solve( "qactor($actorName,CTX,_)", "CTX" )
		//println("               %%% sysUtil |  getActorContext ctxName=${ctxName} - ${ctxsMap.get( ctxName )}")
		return ctxsMap.get( ctxName )
	}
	fun createContexts(  hostName : String,
					desrFilePath:String, rulesFilePath:String){
		loadTheory( desrFilePath )
		loadTheory( rulesFilePath )
		if( solve("tracing", "" ).equals("success") ) trace=true
		if( solve("msglogging", "" ).equals("success") ) logMsgs=true
		try {
			mqttBrokerIP         = solve("mqttBroker(IP,_,_)", "IP" )
			mqttBrokerPort       = solve("mqttBroker(_,PORT,_)", "PORT")
			mqttBrokerEventTopic = solve("mqttBroker(_,_,EVTOPIC)", "EVTOPIC")
		}catch(e: Exception){
			println("               %%% sysUtil | NO MQTT borker FOUND")
		}
        //Create the messages
        try {
            val dispatcNames      = solve("getDispatchIds(D)", "D")
            val dispatchNamesList = strRepToList(dispatcNames!!)
            dispatchNamesList.forEach { d -> createDispatch(d) }
        }catch( e : Exception){ //println("               %%% sysUtil | NO DISPATCH FOUND")
		}
        //Create the contexts
		//println("               %%% sysUtil | getCtxNames( X )" )
		val ctxs: String? = solve("getCtxNames( X )", "X")
			//context( CTX, HOST, PROTOCOL, PORT )
			val ctxsList = strRepToList(ctxs!!)
			//waits for all the other context before activating the actors
			ctxsList.forEach { ctx -> createTheContext(ctx, hostName = hostName) }//foreach ctx
		//APR2020: removed, since we use CoAP e no more TCP
 			addProxyToOtherCtxs(ctxsList, hostName = hostName)  //here could wait in polling ...
	}//createContexts

    fun createDispatch(  d : String )  {
         val dd  = solve("dispatch($d,C)", "C")
         val dt = ( Term.createTerm("dispatch($d,$dd)") )
         //println("sysUtil | dispatch $dt  ")
         dispatchMap.put( d, dt as Struct )
     }
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	fun createTheContext(  ctx : String, hostName : String  ) : QakContext?{
		val ctxHost : String?  = solve("getCtxHost($ctx,H)","H")
		//println("               %%% sysUtil | createTheContext $ctx ctxHost=$ctxHost  ")
		//val ctxProtocol : String? = solve("getCtxProtocol($ctx,P)","P")
		val ctxPort     : String? = solve("getCtxPort($ctx,P)","P")
		//println("               %%% sysUtil | $ctx host=$ctxHost port = $ctxPort protocol=$ctxProtocol")
		val portNum = Integer.parseInt(ctxPort)

//		val useMqtt = ctxProtocol!!.toLowerCase() == "mqtt"	//CoAP 2020
		var mqttAddr = ""
//		if( useMqtt ){	//CoAP 2020
			if( mqttBrokerIP != null ){
				mqttAddr = "tcp://$mqttBrokerIP:$mqttBrokerPort"
				println("               %%% sysUtil | context $ctx WORKS ALSO WITH MQTT mqttAddr=$mqttAddr")
			}
			//else{ throw Exception("no MQTT broker declared")  }	//CoAP 2020
//		}
		//CREATE AND MEMO THE CONTEXT
		var newctx : QakContext?  
		if( ! ctxHost.equals(hostName) ){
              println("               %%% sysUtil | createTheContext $ctx for DIFFERENT host=$ctxHost} ")
			  newctx = QakContext( ctx, "$ctxHost", portNum, "", true) //isa ActorBasic
		}else{
			  println("               %%% sysUtil | createTheContext $ctx FOR host=$hostName  ")
			  newctx = QakContext( ctx, "$ctxHost", portNum, "") //isa ActorBasic
		}
		//val newctx = QakContext( ctx, "$ctxHost", portNum, "") //isa ActorBasic
		newctx.mqttAddr = mqttAddr //!!!!!! INJECTION !!!!!!
		ctxsMap.put(ctx, newctx)
		if( ! ctxHost.equals(hostName) ){
			return null
		}
		ctxOnHost.add(newctx)
		return newctx
 	}//createTheContext

	fun addProxyToOtherCtxs( ctxsList : List<String>, hostName : String){
		ctxsList.forEach { ctx ->
			val curCtx = ctxsMap.get("$ctx")
			if( curCtx is QakContext && curCtx.hostAddr != hostName ) {
				val others = solve("getOtherContextNames(OTHERS,$ctx)","OTHERS")
				val ctxs = strRepToList(others!!)
 					//others!!.replace("[", "").replace("]", "").split(",")
				ctxs.forEach {
 					if( it.length==0  ) return
					val ctxOther = ctxsMap.get("$it")
					if (ctxOther is QakContext) {
						println("               %%% sysUtil | FOR ACTIVATED CONTEXT ${ctxOther.name}: ADDING A PROXY to ${curCtx.name} ")
  						ctxOther.addCtxProxy(curCtx)
					}else{  //NEVER??
						if( ctxOther!!.mqttAddr.length > 1 )  return //NO PROXY for MQTT ctx
						println("               %%% sysUtil | WARNING: CONTEXT ${it} NOT ACTIVATED: " +
					 			"WE SHOULD WAIT FOR IT, TO SET THE PROXY in ${curCtx.name}")
						val ctxHost : String?     = solve("getCtxHost($it,H)","H")
 						val ctxProtocol : String? = solve("getCtxProtocol($it,P)","P")
						val ctxPort : String?     = solve("getCtxPort($it,P)","P")
						curCtx.addCtxProxy( it, ctxProtocol!!, ctxHost!!, ctxPort!! )  
					}
				}
			} //else{ println("sysUtil | WARNING: $ctx NOT ACTIVATED ") }
		}
	}//addProxyToOtherCtxs

	fun getAllActorNames(ctxName: String) : List<String>{
		val actorNames : String? = solve("getActorNames(A,$ctxName)","A" )
		val actorList = strRepToList(actorNames!!)
		return actorList
	}
	fun getAllActorNames( ) : List<String>{
		val actorNames : String? = solve("getActorNames(A,ANY)","A" )
		val actorList = strRepToList(actorNames!!)
		return actorList
	}

	fun strRepToList( liststrRep: String ) : List<String>{
		return liststrRep.replace("[","")
			.replace("]","").split(",")
	}
 	fun createTheActors( ctx: QakContext, scope : CoroutineScope ){
		val actorList = getAllActorNames(ctx.name)
		//println("sysUtil | createTheActors ${ctx.name} actorList=$actorList "   )
		actorList.forEach{
			if( it.length > 0 ){
				val actorClass = solve("qactor($it,_,CLASS)","CLASS")
				//println("sysUtil | CREATE actor=$it in context:${ctx.name}  class=$actorClass"   )
				val className = actorClass!!.replace("'","")
				createActor( ctx, it, className, scope)
			}
		}
	}//createTheActors

	fun createActor( ctx: QakContext, actorName: String,
					 className : String, scope : CoroutineScope = GlobalScope  ) : ActorBasic?{
		/*
		if( className=="external"){
			println("               %%% sysUtil |   actor=$actorName in context:${ctx.name}  is EXTERNAL"   )
			return null
		}
  	 */
		traceprintln("               %%% sysUtil | CREATE actor=$actorName in context:${ctx.name}  class=$className"   )
		val clazz = Class.forName(className)	//Class<?>
        var actor  : ActorBasic 
        try {
            val ctor = clazz.getConstructor(String::class.java, CoroutineScope::class.java )  //Constructor<?>
            actor = ctor.newInstance(actorName, scope  ) as ActorBasic 
        } catch( e : Exception ){
			//println("sysUtil  | ERROR ${e}" )
            val ctor = clazz.getConstructor( String::class.java )  //Constructor<?>
            actor = ctor.newInstance( actorName  ) as ActorBasic 
        }
		ctx.addActor(actor)
		actor.context = ctx
		//MEMO THE ACTOR
		ctxActorMap.put(actorName,actor  )
		return actor
	}

	fun solve( goal: String, resVar: String  ) : String? {
		//println("sysUtil  | solveGoal ${goal}" );
		val sol = pengine.solve( "$goal.")
		if( sol.isSuccess ) {
			if( resVar.length == 0 ) return "success"
			val result = sol.getVarValue(resVar)  //Term
			var resStr = result.toString()
			return  strCleaned( resStr )
		}
		else return null
	}

	fun loadTheory( path: String ) {
		try {
			//user.dir is typically the directory in which the Java virtual machine was invoked.
			//val executionPath = System.getProperty("user.dir")
			//println("               %%% sysUtil | loadheory executionPath: ${executionPath}" )
			//val resource = classLoader.getResource("/") //URL
			//val cl =  javaClass<ActorBasic> //javaClass does not work
			//println("               %%% sysUtil | loadheory classloader: ${cl}" )
			val worldTh = Theory( FileInputStream(path) )
			pengine.addTheory(worldTh)
		} catch (e: Exception) {
			println("               %%% sysUtil | loadheory WARNING: ${e}" )
			throw e
		}
	}


	fun strCleaned( s : String) : String{
		if( s.startsWith("'")) return s.replace("'","")
		else return s

	}


	fun traceprintln( msg : String ){
		if( sysUtil.trace ) println( msg  )
	}
	
/*
 	MSG LOGS
*/ 	
	fun createFile( fname : String, dir : String = "logs" ){
 		val logDirectory = File("$userDirectory/$dir")
		logDirectory.mkdirs()	//have the object build the directory structure, if needed
		var file = File(logDirectory, fname)
//		println("               %%% sysUtil | createFile file $file in $dir")
		file.writeText("")	//file is created and nothing is written to it
	}
	
	fun deleteFile( fname : String, dir  : String ){
		File("$userDirectory/$dir/$fname").delete()
	}
	fun updateLogfile( fname: String, msg : String, dir : String = "logs" ){
		if( logMsgs ) File("$userDirectory/$dir/$fname").appendText("${msg}\n")
	}
	fun aboutThreads(info: String){
		val tname    = Thread.currentThread().getName();
		val nThreads = ""+Thread.activeCount() ;
		traceprintln("               %%% $info thread=$tname n=$nThreads"  )
	}
	fun waitUser(prompt: String, tout: Long = 2000   ) {
			try {
				print(">>>  $prompt (tout=$tout) >>>  ")
				val input = BufferedReader(InputStreamReader(System.`in`))
				val startTime = System.currentTimeMillis()
				while (System.currentTimeMillis() - startTime < tout && !input.ready() ) { }
				println("")
  			} catch (e: java.lang.Exception) {
				e.printStackTrace()
			}
	}
}//sysUtil
