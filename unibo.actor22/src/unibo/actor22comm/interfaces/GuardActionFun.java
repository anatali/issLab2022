package unibo.actor22comm.interfaces;

import it.unibo.kactor.IApplMessage;

public interface GuardActionFun {
	boolean eval(IApplMessage msg);
}
