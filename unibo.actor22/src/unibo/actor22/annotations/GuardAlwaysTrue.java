package unibo.actor22.annotations;

 
import unibo.actor22comm.interfaces.IGuard;
import unibo.actor22comm.utils.ColorsOut;

public class GuardAlwaysTrue  implements IGuard{
	@Override
  	public boolean eval( ) {
 		//ColorsOut.outappl("GuardAlwaysTrue eval" , ColorsOut.ANSI_YELLOW);
 		return true;
	}

}
