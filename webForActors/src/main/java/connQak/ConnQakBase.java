package connQak;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.interfaces.Interaction2021;

public abstract class ConnQakBase  {

    private static ConnQakBase currQakConn;

    public static ConnQakBase create(ProtocolType protocol) {
        if( protocol == ProtocolType.tcp ){
            currQakConn = new ConnQakTcp( );
            return currQakConn;
           //.createConnection("localhost");
        }
        else return null;
    }

    public abstract Interaction2021 createConnection(String host, int port  );
    public abstract void forward(String msg);
    public abstract void request(String msg);
    public abstract void emit(String msg);
    /*
    abstract fun createConnection(   )
    abstract fun forward( msg : ApplMessage )
    abstract fun request( msg : ApplMessage )
    abstract fun emit( msg : ApplMessage )
*/


}