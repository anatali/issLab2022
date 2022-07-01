package unibo.comm22.callers;

import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.tcp.TcpClientSupport;
import unibo.comm22.utils.ColorsOut;

public class ConnQakTcp extends ConnQakBase{
    private Interaction2021 conn;

    @Override
    public Interaction2021 createConnection(String hostAddr, int port){
        try {
            //ColorsOut.outappl("createConnection to:" + hostAddr+":"+port, ColorsOut.BLUE);
            conn = TcpClientSupport.connect(hostAddr,port,10);
            ColorsOut.outappl("createConnection DONE:" + conn, ColorsOut.BLUE);
            createInpurReader(conn);
            return conn;
        } catch (Exception e) {
            ColorsOut.outerr("createConnection ERROR:" + e.getMessage() );
            return null;
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

//-------------------------------------------------
    protected void createInpurReader(Interaction2021 conn){
        new Thread(){
            public void run(){
                try {
                    ColorsOut.out( "createInpurReader | STARTS conn=" + conn, ColorsOut.BLUE );
                    while( true ) {
                        //ColorsOut.out(name + " | waits for message  ...");
                        String msg = conn.receiveMsg();
                        ColorsOut.outappl("ConnQakTcp InpurReader received:" + msg, ColorsOut.YELLOW );
                        if( msg == null ) {
                            conn.close();
                            break;
                        } else{
                            //Siamo a livello di supporto: cleanArch ok
                            //WebSocketConfiguration.wshandler.sendToAll(msg);
                        }
                    }
                    ColorsOut.out("TcpApplMessageHandler  |  BYE", ColorsOut.BLUE   );
                }catch( Exception e) {
                    ColorsOut.outerr( "TcpApplMessageHandler | ERROR:" + e.getMessage()  );
                }

            }
        }.start();
    }
}
