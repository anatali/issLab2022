package connQak;

import it.unibo.kactor.IApplMessage;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.tcp.TcpClientSupport;
import unibo.actor22comm.utils.ColorsOut;

public class ConnQakTcp extends ConnQakBase{
    private Interaction2021 conn;

    @Override
    public Interaction2021 createConnection(String hostAddr, int port){
        try {
            conn = TcpClientSupport.connect(hostAddr,port,10);
            ColorsOut.outappl("createConnection DONE:" + conn, ColorsOut.BLUE);
            return conn;
        } catch (Exception e) {
            return null;
            //e.printStackTrace();
        }
    }



    @Override
    public void forward(String msg) {
        try {
            ColorsOut.outappl("doMove:" + msg   , ColorsOut.BLUE);
            conn.forward(msg );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void request(String msg) {

    }

    @Override
    public void emit(String msg) {

    }
}
