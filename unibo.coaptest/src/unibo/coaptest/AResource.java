package unibo.coaptest;

import it.unibo.kactor.ActorBasic;
import it.unibo.kactor.IApplMessage;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.CoroutineScope;
 

public class AResource extends ActorBasic{

	public AResource(String name) {
		super(name, null, false, false, false, 50);  //null -> GlobalScope
	}
	@Override
	public Object actorBody(IApplMessage arg0, Continuation<? super Unit> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

 

}
