package unibo.actor22.guards;

 
import unibo.actor22comm.interfaces.IGuard;
import unibo.actor22comm.utils.ColorsOut;

public class Guard0 implements IGuard{
	protected static int vn ;
  	
	public static void setValue( int n ) {
		vn = n;
	}
 
 	public boolean eval( ) {
 		boolean b = (vn > 0);
 		ColorsOut.outappl("Guard0 eval="+b , ColorsOut.CYAN);
 		return b;
	}

}
