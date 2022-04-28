package unibo.wenvUsage22.annot.walker;

 
import unibo.actor22comm.utils.ColorsOut;

public class GuardEndOfWork {
	protected static int vn ;
  	
	public static void setValue( int n ) {
		vn = n;
	}
	public static boolean checkValue(   ) {
		return vn == 4 ;
	}
 	public boolean eval( ) {
 		boolean b = checkValue();
 		ColorsOut.outappl("GuardEndOfWork eval="+b , ColorsOut.CYAN);
 		return b;
	}

}
