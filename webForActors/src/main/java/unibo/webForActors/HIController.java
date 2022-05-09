package unibo.webForActors;

import connQak.ConnQakBase;
import it.unibo.kactor.IApplMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import unibo.Robots.basic.MainBasicRobot;
import unibo.Robots.cleaner.MainRobotCleaner;
import unibo.Robots.common.RobotCleanaerObserver;
import unibo.Robots.common.RobotUtils;
import unibo.Robots.mapper.MainRobotMapper;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;

@Controller
public class HIController {
    //private static final String robotCmdId = "move";
    protected static  String robotName     = ""; //visibility in package
    private static boolean cleanerAppl     = false;

    private Interaction2021 conn;
    private String mainPage = "RobotNaiveGui";

    public HIController(){
        ColorsOut.outappl("HIController: CREATE"   , ColorsOut.WHITE_BACKGROUND);
    }
    @Value("${spring.application.name}")
    String appName;

    @GetMapping("/")
    public String homePage(Model model) {
        model.addAttribute("arg", appName);
        if(cleanerAppl)  mainPage = "RobotCleanerGui"; //"RobotCmdGuiWs";//
        else mainPage = "RobotNaiveGui";
        return mainPage;
    }

    //Dopo click sul pulsante Configure
    @PostMapping("/configure")
    public String configure(Model viewmodel  , @RequestParam String move, String addr ){
        ColorsOut.outappl("HIController | configure:" + move, ColorsOut.BLUE);

        //modo locale di creazione del componente applicativo
        if(cleanerAppl){
            RobotUtils.createRobotCleaner();
            robotName  = MainRobotCleaner.robotName;
            mainPage   = "RobotCleanerGui";
        } else {
            RobotUtils.createRobotMapper();
            robotName  = MainRobotMapper.robotName;
            mainPage   = "RobotNaiveGui";
        }

        RobotUtils.connectWithRobot(addr);

        RobotCleanaerObserver obs = new RobotCleanaerObserver(""+RobotUtils.robotPort,robotName);
        obs.setWebSocketHandler(WebSocketConfiguration.wshandler);

        //To allow ... todo
        //Qak22Context.setActorAsRemote(robotName, "8083", "localhost", ProtocolType.tcp);
        return mainPage;
    }

    //Dopo click sul pulsante start/stop/resume
    @PostMapping("/robotcmd")
    public String doMove(Model viewmodel  , @RequestParam String cmd ){
                     //String @RequestParam(name="move", required=false, defaultValue="h")robotmove  )  {
        //sysUtil.colorPrint("HIController | param-move:$robotmove ", Color.RED)
        ColorsOut.outappl("HIController | doMove:" + cmd + " robotName=" + robotName, ColorsOut.BLUE);
        WebSocketConfiguration.wshandler.sendToAll("HIController | doMove:" + cmd); //disappears
        if( cmd.equals("t")){  //Start
            RobotUtils.startRobot("hicontroller",robotName);
            //Qak22Util.sendAMsg( SystemData.startSysCmd("hicontroller",robotName) );
        }else{ RobotUtils.sendMsg(robotName,cmd);
        /*
            try {
                String msg = RobotUtils.moveAril(robotName,cmd).toString();
                ColorsOut.outappl("HIController | doMove msg:" + msg , ColorsOut.BLUE);
                conn.forward( msg );
            } catch (Exception e) {
                ColorsOut.outerr("HIController | doMove ERROR:"+e.getMessage());
            }*/
        }
        return mainPage;
    }

    @ExceptionHandler
    public ResponseEntity handle(Exception ex) {
        HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity(
                "BaseController ERROR " + ex.getMessage(),
                responseHeaders, HttpStatus.CREATED);
    }
}
