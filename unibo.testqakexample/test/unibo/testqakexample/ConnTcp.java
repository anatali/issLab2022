package unibo.testqakexample;

 
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.tcp.TcpClientSupport;
import unibo.comm22.utils.ColorsOut;

public class ConnTcp implements Interaction2021{
    private Interaction2021 conn;

    public ConnTcp(String hostAddr, int port) throws Exception{
        conn = TcpClientSupport.connect(hostAddr,port,10);
        ColorsOut.outappl("ConnTcp createConnection DONE:" + conn, ColorsOut.GREEN);
     }

    @Override
    public void forward(String msg) {
        try {
            //ColorsOut.outappl("ConnTcp forward:" + msg   , ColorsOut.GREEN);
            conn.forward(msg );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String request(String msg) throws Exception {
        forward(msg);
        return receiveMsg();
    }

    @Override
    public void reply(String s) throws Exception {
        forward(s);
    }

    @Override
    public String receiveMsg() throws Exception {
          return conn.receiveMsg();
    }

    @Override
    public void close() throws Exception {

    }


}
