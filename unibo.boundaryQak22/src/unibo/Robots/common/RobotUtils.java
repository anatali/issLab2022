package unibo.Robots.common;

 
import it.unibo.kactor.IApplMessage;
import unibo.Robots.MainRobotCleaner;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.IObserver;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22comm.ws.WsConnection;

public class RobotUtils {
    public static final String robotCmdId   = "move";
    public static final int robotPort       = 8083;
    public static final String robotPortStr = "8083";
    private static Interaction2021 conn;

    public static void createRobotCleaner(){
        CommSystemConfig.tracing = false;
        CommUtils.aboutThreads("Before start - ");
        MainRobotCleaner appl = new MainRobotCleaner( );
        appl.doJob();
        //appl.terminate();
    }
 
 
    public static void connectWithRobot(String addr){
        ConnQakBase connToRobot = ConnQakBase.create( ProtocolType.tcp );
        conn = connToRobot.createConnection(addr, RobotUtils.robotPort);  //8083 is the cleaner robot
    }
    public static Interaction2021 connectWithVirtualRobot(String name){
		ColorsOut.outappl(name + " | ws connecting ...." ,  ColorsOut.BLUE);
		conn = WsConnection.create("localhost:8091" );
		//robotMoveObserver != null quando riattivamo al termine del lavoro
		IObserver robotMoveObserver = new WsConnApplObserver( name, false);
		((WsConnection) conn).addObserver(robotMoveObserver);
 		ColorsOut.outappl( name + " | conn:" + conn,  ColorsOut.BLUE);
 		return conn;
    }
    public static  IApplMessage moveAril(String robotName, String cmd  ) {
        //ColorsOut.outappl("HIController | moveAril cmd:" + cmd , ColorsOut.BLUE);
        switch( cmd ) {
            case "w" : return CommUtils.buildDispatch("webgui", robotCmdId, "w", robotName);
            case "s" : return CommUtils.buildDispatch("webgui", robotCmdId, "s", robotName);
            case "a" : return CommUtils.buildDispatch("webgui", robotCmdId, "a", robotName);
            case "d" : return CommUtils.buildDispatch("webgui", robotCmdId, "d", robotName);
            case "h" : return CommUtils.buildDispatch("webgui", robotCmdId, "h", robotName);

            case "start"  : return CommUtils.buildDispatch("webgui", robotCmdId, "start",  robotName);
            case "stop"   : return CommUtils.buildDispatch("webgui", "stop", "do",   robotName);
            case "resume" : return CommUtils.buildDispatch("webgui", "resume", "do", robotName);
            default:   return CommUtils.buildDispatch("webgui",   robotCmdId, "h",robotName);
        }
    }

    public static void startRobot( String sender, String robotName ){
        Qak22Util.sendAMsg( SystemData.startSysCmd(sender,robotName) );
    }

    public static void sendMsg(String robotName, String cmd){
        try {
            String msg =  moveAril(robotName,cmd).toString();
            ColorsOut.outappl("RobotUtils | doMove msg:" + msg , ColorsOut.BLUE);
            conn.forward( msg );
        } catch (Exception e) {
            ColorsOut.outerr("RobotUtils | doMove ERROR:"+e.getMessage());
        }
    }


}
