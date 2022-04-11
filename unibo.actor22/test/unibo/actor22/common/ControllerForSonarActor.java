package unibo.actor22.common;
 
import it.unibo.kactor.IApplMessage;
import it.unibo.radarSystem22.domain.DeviceFactory;
import it.unibo.radarSystem22.domain.interfaces.IRadarDisplay;
import unibo.actor22.*;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
 

/*
 * Il controller conosce SOLO I NOMI dei dispositivi 
 * (non ha riferimenti ai dispositivi-attori)
 */
public class ControllerForSonarActor extends QakActor22{
 
protected IRadarDisplay radar;
protected boolean on = true;

	public ControllerForSonarActor(String name  ) {
		super(name);
		radar = DeviceFactory.createRadarGui();
  	}

	@Override
	protected void handleMsg(IApplMessage msg) {  
		if( msg.isEvent() )        elabEvent(msg);
		else if( msg.isDispatch()) elabCmd(msg) ;	
		else if( msg.isReply() )   elabReply(msg) ;	
 	}
	

	
	protected void elabCmd(IApplMessage msg) {
		String msgCmd = msg.msgContent();
		ColorsOut.outappl( getName()  + " | elabCmd=" + msgCmd + " obs=" + RadarSystemConfig.sonarObservable, ColorsOut.BLUE);
		if( msgCmd.equals(ApplData.cmdActivate)  ) {
 				sendMsg( ApplData.activateSonar);
				doControllerWork();
 		}		
	}
	
	protected void elabReply(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabReply=" + msg, ColorsOut.GREEN);
		//if( msg.msgId().equals(ApplData.reqDistance ))
		String dStr = msg.msgContent();
		int d = Integer.parseInt(dStr);
		//Radar
		radar.update(dStr, "60");
		//LedUse case
		if( d <  RadarSystemConfig.DLIMIT ) {
			//forward(ApplData.turnOnLed); 		
			forward(ApplData.deactivateSonar);
		}
		else {
			//forward(ApplData.turnOffLed); 
	    	doControllerWork();
		}
	}
	

	protected void elabEvent(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabEvent=" + msg, ColorsOut.GREEN);
		if( msg.isEvent()  ) {  //defensive
			String dstr = msg.msgContent();
			int d       = Integer.parseInt(dstr);
			radar.update(dstr, "60");
			if( d <  RadarSystemConfig.DLIMIT ) {
				//forward(ApplData.turnOnLed); 		
				forward(ApplData.deactivateSonar);
			}
			else {
				//forward(ApplData.turnOffLed); 
			}
		}
	}

    protected void doControllerWork() {
		CommUtils.aboutThreads(getName()  + " |  Before doControllerWork " + RadarSystemConfig.sonarObservable );
    	if( ! RadarSystemConfig.sonarObservable)  request( ApplData.askDistance );
	}	

}
