package rx

 
import it.unibo.kactor.ApplMessage
import alice.tuprolog.Struct
import alice.tuprolog.Term
import java.io.PrintWriter
import java.io.FileWriter
import java.io.ObjectOutputStream
import java.io.FileOutputStream
import it.unibo.kactor.ActorBasic
import java.io.File

class dataLogger(name : String) : ActorBasic(name){
	var pw : PrintWriter
	
 	init{
		pw = PrintWriter( FileWriter(name+".txt") )
 	}
    
@kotlinx.coroutines.ObsoleteCoroutinesApi
@kotlinx.coroutines.ExperimentalCoroutinesApi
	override suspend fun actorBody(msg: ApplMessage) {
  		elabData( msg )
		emitLocalStreamEvent(msg)	//propagate ... 
	}
 
 	protected suspend fun elabData( msg: ApplMessage ){
 		val data  = (Term.createTerm( msg.msgContent() ) as Struct).getArg(0).toString()
		println("	-------------------------------------------- $name data=$data")
   		pw.append( "$data\n " )
		pw.flush()
     }

}