package unibo.Robots.common;
/*
import unibo.actor22comm.coap.CoapConnection;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.tcp.TcpClientSupport;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
*/
import it.unibo.kactor.IApplMessage;
import unibo.comm22.coap.CoapConnection;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.tcp.TcpClientSupport;
import unibo.comm22.utils.ColorsOut;
import unibo.comm22.utils.CommUtils;
import unibo.webRobot22.PathCoapObserver;
import unibo.webRobot22.RobotCoapObserver;

public class RobotUtils {
    public static final String robotCmdId       = "move";
    public static final String basicrobotCmdId  = "cmd";
    public static final int robotPort           = 8020;
    private static Interaction2021 conn;
    private static Interaction2021 connToPathexec;


    public static void connectWithRobotUsingTcp(String addr){
         try {
            conn = TcpClientSupport.connect(addr, robotPort, 10);
            ColorsOut.outappl("HIController | connect Tcp conn:" + conn , ColorsOut.CYAN);
        }catch(Exception e){
            ColorsOut.outerr("RobotUtils | connectWithRobotUsingTcp ERROR:"+e.getMessage());
        }
    }
    public static CoapConnection connectWithRobotUsingCoap(String addr){
        try {
            String ctxqakdest       = "ctxbasicrobot";
            String qakdestination 	= "basicrobot";
            String path   = ctxqakdest+"/"+qakdestination;
            conn           = new CoapConnection(addr+":"+robotPort, path);
            connToPathexec = new CoapConnection(addr+":"+robotPort, ctxqakdest+"/pathexec" );
            ((CoapConnection)connToPathexec).observeResource( new PathCoapObserver() );
            ColorsOut.outappl("HIController | connect Coap conn:" + conn , ColorsOut.CYAN);
        }catch(Exception e){
            ColorsOut.outerr("RobotUtils | connectWithRobotUsingTcp ERROR:"+e.getMessage());
        }
        return (CoapConnection) conn;
    }
    public static IApplMessage moveAril(String robotName, String cmd  ) {
        //ColorsOut.outappl("HIController | moveAril cmd:" + cmd , ColorsOut.BLUE);
        switch( cmd ) {
            case "w" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(w)", robotName);
            case "s" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(s)", robotName);
            case "turnLeft" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(a)", robotName);
            case "l" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(a)", robotName);
            case "r" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(d)", robotName);
            case "h" : return CommUtils.buildDispatch("webgui", basicrobotCmdId, "cmd(h)", robotName);
            case "p" : return CommUtils.buildRequest("webgui", "step", "step(345)", robotName);

            case "start"  : return CommUtils.buildDispatch("webgui", robotCmdId, "start",  robotName);
            case "stop"   : return CommUtils.buildDispatch("webgui", "stop", "do",   robotName);
            case "resume" : return CommUtils.buildDispatch("webgui", "resume", "do", robotName);
            default:   return CommUtils.buildDispatch("webgui",   robotCmdId, "h",robotName);
        }
    }
/*
    public static void startRobot( String sender, String robotName ){
        Qak22Util.sendAMsg( SystemData.startSysCmd(sender,robotName) );
    }
*/
    public static void sendMsg(String robotName, String cmd){
        try {
            IApplMessage msg =  moveAril(robotName,cmd);
            ColorsOut.outappl("RobotUtils | sendMsg msg:" + msg + " conn=" + conn, ColorsOut.BLUE);
            /*
            if( msg.isRequest() ){
                String answer = conn.request( msg.toString() );
                ColorsOut.outappl("RobotUtils | answer:" + answer  , ColorsOut.BLUE);
            }
            else */
                conn.forward( msg.toString() );
        } catch (Exception e) {
            ColorsOut.outerr("RobotUtils | sendMsg ERROR:"+e.getMessage());
        }
    }
    public static void doThePath( String path ){
        try {
            String msg = ""+ CommUtils.buildRequest("webgui", "dopath", "dopath("+path+")", "pathexec");
            ColorsOut.outappl("RobotUtils | sendMsg msg:" + msg + " conn=" + conn, ColorsOut.BLUE);
            connToPathexec.forward( msg );
        } catch (Exception e) {
            ColorsOut.outerr("RobotUtils | sendMsg ERROR:"+e.getMessage());
        }
    }

}
