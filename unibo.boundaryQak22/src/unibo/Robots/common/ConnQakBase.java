package unibo.Robots.common;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.interfaces.Interaction2021;

public abstract class ConnQakBase  {

    private static ConnQakBase currQakConn;

    public static ConnQakBase create(ProtocolType protocol) {
        if( protocol == ProtocolType.tcp ){
            currQakConn = new ConnQakTcp( );
            return currQakConn;
         }
        else return null;
    }

    public abstract Interaction2021 createConnection(String host, int port  );
    public abstract void forward(String msg);
    public abstract void request(String msg);
    public abstract void emit(String msg);



}