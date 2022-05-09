package unibo.Robots.common;

import connQak.ConnQakBase;
import it.unibo.kactor.IApplMessage;
import unibo.Robots.basic.MainBasicRobot;
import unibo.Robots.cleaner.MainRobotCleaner;
import unibo.Robots.mapper.MainRobotMapper;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

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
    public static  void createBasicRobot(){
        CommSystemConfig.tracing = false;
        CommUtils.aboutThreads("Before start - ");
        MainBasicRobot appl = new MainBasicRobot( );
        appl.doJob();
        //appl.terminate();
    }
    public static  void createRobotMapper(){
        CommSystemConfig.tracing = false;
        CommUtils.aboutThreads("Before start - ");
        MainRobotMapper appl = new MainRobotMapper( );
        appl.doJob();
        //appl.terminate();
    }
    public static void connectWithRobot(String addr){
        ConnQakBase connToRobot = ConnQakBase.create( ProtocolType.tcp );
        conn = connToRobot.createConnection(addr, RobotUtils.robotPort);  //8083 is the cleaner robot
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
