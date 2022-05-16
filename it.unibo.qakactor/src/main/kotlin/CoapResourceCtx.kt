package it.unibo.kactor

import org.eclipse.californium.core.coap.CoAP.ResponseCode.CHANGED
import org.eclipse.californium.core.coap.CoAP.ResponseCode.DELETED
import org.eclipse.californium.core.CoapResource
import org.eclipse.californium.core.server.resources.CoapExchange
import kotlinx.coroutines.launch


class CoapResourceCtx(name: String, val ctx : QakContext) : CoapResource(name) {
//    private var counter = 0
    private val actorResources =  mutableMapOf<String, ActorBasic>()
    private val logo    = "       CoapResourceCtx $name "
    private var applRep = "$logo | created  "

    init {
        isObservable = true
        sysUtil.aboutThreads("CoapResourceCtx $name | AFTER init   " );
        //println("               %%% CoapResourceCtx $name | created  ")
    }

    override fun handleGET(exchange: CoapExchange) {
        println("               %%% CoapResourceCtx " + name + " | handleGET from:" +
                exchange.sourceAddress + " arg:" + exchange.requestText)
        exchange.respond( "$applRep")
    }

/*
 * POST is NOT idempotent.	Use POST when you want to add a child resource
 */
    override fun handlePOST(exchange: CoapExchange) {
        println("               %%% CoapResourceCtx $name | POST not implemented")
        exchange.respond( "POST not implemented")
    }

/*
 * PUT method is idempotent. Use PUT when you want to modify
 */

    override fun handlePUT(exchange: CoapExchange) {
        val arg = exchange.requestText
        println("               %%% CoapResourceCtx $name | PUT arg=$arg")
        try{
            val event    = ApplMessage( arg )     //should be an event
            propagateEvent( event )
            updateCoapResource("$event redirected")
          }catch( e : Exception){
            updateCoapResource("error on msg $arg")
            //println("$logo | handlePUT ERROR on msg ")
        }
        exchange.respond(CHANGED)
    }

    override fun handleDELETE(exchange: CoapExchange) {
        delete()
        exchange.respond(DELETED)
    }
//-----------------------------------------------------------------------
	
    fun addActorResource(owner: ActorBasic){
        this.add( owner )
        actorResources.put( owner.name , owner )
        println("               %%% CoapResourceCtx $name | addActorResource ${owner.name}" )
    }

    fun getActorResource( name : String ) : ActorBasic?{
        val r = actorResources.get( name )
        if( r != null ){
            println("               %%% CoapResourceCtx $name | getActorResource " + (r as CoapResource).name )
        }
        return r
    }

    fun updateCoapResource( v : String){
        applRep = v
        changed()             //DO NOT FORGET!!!
    }

@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
    fun propagateEvent(event : ApplMessage){
        if( ctx == null ) return;  //defensive May 2022
        ctx.actorMap.forEach{
            //sysUtil.traceprintln("               %%% CoapResourceCtx $name | in ${ctx.name} propag $event to ${it.key} in ${it.value.context.name}")
            val a = it.value
            try{
                a.scope.launch{ a.actor.send(event) }
            }catch( e1 : Exception) {
                println("               %%% CoapResourceCtx $name | propagateEvent WARNING: ${e1.message}")
            }
        }
    }


 }
