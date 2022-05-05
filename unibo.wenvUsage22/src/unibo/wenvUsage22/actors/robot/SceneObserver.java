package unibo.wenvUsage22.actors.robot;

import it.unibo.kactor.IApplMessage;
import unibo.actor22.QakActor22;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

public class SceneObserver extends QakActor22{

	public SceneObserver(String name) {
		super(name);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | handleMsg " + msg, ColorsOut.CYAN);		
	}
 
}
