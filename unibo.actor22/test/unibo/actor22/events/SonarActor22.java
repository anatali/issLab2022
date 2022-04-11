package unibo.actor22.events;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import it.unibo.kactor.IApplMessage;
import it.unibo.kactor.MsgUtil;
import it.unibo.radarSystem22.domain.Distance;
import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import unibo.actor22.Qak22Util;
import unibo.actor22.QakActor22;
import unibo.actor22.common.ApplData;
import unibo.actor22.common.RadarSystemConfig;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;


/*
 * Il SonarActor22 NON riusa gli oggetti del dominio
 * ma ridefinisce l'entità come RISORSA proattiva e reattiva
 */
public class SonarActor22 extends QakActor22{
	private IDistance curVal ;	 
	private Process p ;
	private  BufferedReader reader ;
	private boolean stopped   = false;
	private int delta = 1;
	
	public SonarActor22(String name) {
		super(name);
		setup();
	}
	
	protected void setup() {
 		curVal = new Distance(90);		
 		if( ! DomainSystemConfig.simulation )  {
	 		if( p == null ) { 
	 	 		try {
	 				p       = Runtime.getRuntime().exec("sudo ./SonarAlone");
	 		        reader  = new BufferedReader( new InputStreamReader(p.getInputStream()));
	 		        ColorsOut.out(getName()  + " | SonarConcrete | sonarSetUp done");
	 	       	}catch( Exception e) {
	 	       		ColorsOut.outerr(getName()  + " | SonarConcrete | sonarSetUp ERROR " + e.getMessage() );
	 	    	}
	 		}			
		}else
			ColorsOut.out(getName()  + " | SonarMock| sonarSetUp done ");

	}

	@Override
	protected void handleMsg(IApplMessage msg) {
		ColorsOut.out( getName()  + " | handleMsgg " + msg);
		if( msg.isRequest() ) elabRequest(msg);
		else elabCmd(msg);
	}

/*
* REACTIVE PART	
*/
	protected void elabCmd(IApplMessage msg) {
		//ColorsOut.outappl(getName() + " | elabCmd "+ msg, ColorsOut.BLUE);
		String msgCmd = msg.msgContent();
 		switch( msgCmd ) {
			case ApplData.cmdActivate  : sonarActivate();  break;
			case ApplData.cmdDectivate : sonarDeactivate();break;
 			default: ColorsOut.outerr(getName()  + " | unknown " + msgCmd);
		}
	}

	protected void elabRequest(IApplMessage msg) {
		String msgReq = msg.msgContent();
		//ColorsOut.out( getName()  + " | elabRequest " + msgCmd, ColorsOut.CYAN);
		switch( msgReq ) {
			case ApplData.reqDistance  :{
				int d = curVal.getVal();
				IApplMessage reply = MsgUtil.buildReply(getName(), ApplData.reqDistance, ""+d, msg.msgSender());
				ColorsOut.out( getName()  + " | reply= " + reply, ColorsOut.CYAN);
 				sendReply(msg, reply );				
				break;
			}
 			default: ColorsOut.outerr(getName()  + " | unknown " + msgReq);
		}
	}

	protected void sonarDeactivate() {
		stopped = true;
	}
	
	protected void sonarActivate() {
		if( DomainSystemConfig.simulation ) sonarStepAsMock();
		else sonarStepAsConcrete();
	}

/*
 * PROACTIVE PART	
 */
	
	
	protected void sonarStepAsMock() {		
		int v = curVal.getVal() - delta;
		updateDistance( v );		
 		if( v > 0 && ! stopped) {
 	 		CommUtils.delay( DomainSystemConfig.sonarDelay );
 			autoMsg(ApplData.activateSonar);   
 		}
 	}
	
	protected void sonarStepAsConcrete() {
        try {
			String data = reader.readLine();
			if( data == null ) return;
			int v = Integer.parseInt(data);
			ColorsOut.out(getName()  + " SonarConcrete | v=" + v );
			int lastSonarVal = curVal.getVal();
			if( lastSonarVal != v && v < DomainSystemConfig.sonarDistanceMax) {	
				//Eliminiamo dati del tipo 3430 //TODO: filtri in sottosistemi a stream
  	 			updateDistance( v );	 			
			}
			if( ! stopped ) {
				stopped = ( v <= 0 );	
 				this.autoMsg(ApplData.activateSonar);  //cedo il controllo ???
			}
      }catch( Exception e) {
       		ColorsOut.outerr(getName()  + " SonarConcrete |  " + e.getMessage() );
       }		
		
	}
 
	protected void updateDistance( int d ) {
		curVal = new Distance( d );
		ColorsOut.out(getName() + " | updateDistance "+ d, ColorsOut.BLUE);
		if( RadarSystemConfig.sonarObservable ) {
			IApplMessage distanceEvent = Qak22Util.buildEvent(getName(), ApplData.evDistance, ""+d );
			//Qak22Util.sendAMsg(distanceEvent, EventMsgHandler.myName);
			this.emit(distanceEvent);
		}	
	}	

}
