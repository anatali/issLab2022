package unibo.actor22.annotations;

 
import unibo.actor22.interfaces.IGuard;
import unibo.comm22.utils.ColorsOut;

public class GuardAlwaysTrue  implements IGuard{
	@Override
  	public boolean eval( ) {
 		//ColorsOut.outappl("GuardAlwaysTrue eval" , ColorsOut.ANSI_YELLOW);
 		return true;
	}

}
