package unibo.wenvUsage22.basicRobot.prototype0;

import it.unibo.kactor.sysUtil;
import unibo.actor22.Qak22Context;
import unibo.actor22.Qak22Util;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.actor22comm.SystemData;
import unibo.actor22comm.coap.CoapSupport;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommSystemConfig;
import unibo.actor22comm.utils.CommUtils;
import unibo.wenvUsage22.common.ApplData;
 
@Context22(name = "ctx", host = "localhost", port = "8083")
@Actor22(name = MainBasicRobot.robotName, contextName = "ctx", implement = BasicRobotActor.class)
public class MainBasicRobot {
	
	public static final String robotName = "basicrobot";
	
	public void doJob() {
		CommSystemConfig.tracing = true;
		sysUtil.INSTANCE.setTrace(true);  //mondo qak
		Qak22Context.configureTheSystem(this);
		Qak22Context.showActorNames();
		//Activate viene dalla GUI
//  		Qak22Util.sendAMsg( SystemData.startSysCmd("main",robotName) );
//  		Qak22Util.sendAMsg( ApplData.w("", robotName) );
	};

	public void terminate() {
		CommUtils.aboutThreads("Before end - ");		
		CommUtils.delay(600000); //Give time to work ...
		CommUtils.aboutThreads("At exit - ");		
		System.exit(0);
	}
	
	public static void main( String[] args) throws Exception {
		CommUtils.aboutThreads("Before start - ");
		MainBasicRobot appl = new MainBasicRobot( );
		
		new Thread() {
			public void run() {
				CoapSupport cs = new CoapSupport("coap://localhost:8020","ctx/basicrobot");
				//CoapApplObserver obs = new CoapApplObserver("localhost:8020", "actor/basicrobot");
				//cs.observeResource( obs );
				CommUtils.delay(2000);
				String result = cs.readResource();
				ColorsOut.out( " result=" + result );
			}
		}.start();
		
		
		appl.doJob();
		appl.terminate();
	}

}
