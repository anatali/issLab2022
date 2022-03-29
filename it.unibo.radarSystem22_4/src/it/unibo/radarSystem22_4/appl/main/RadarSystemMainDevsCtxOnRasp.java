package it.unibo.radarSystem22_4.appl.main;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22_4.appl.RadarSystemConfig;
import it.unibo.radarSystem22_4.appl.handler.LedApplHandler;
import it.unibo.radarSystem22_4.appl.handler.SonarApplHandler;
import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.context.ContextMsgHandler;
import it.unibo.radarSystem22_4.comm.enablers.EnablerContext;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplication;
import it.unibo.radarSystem22_4.comm.interfaces.IContext;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;
import it.unibo.radarSystem22_4.comm.utils.CommSystemConfig;
import it.unibo.radarSystem22.domain.DeviceFactory;

public class RadarSystemMainDevsCtxOnRasp implements IApplication{
	private ISonar sonar;
	private ILed  led ;
  	
 	private IContext contextServer;

	
	@Override
	public String getName() {
		return this.getClass().getName() ; 
	}
	@Override
	public void doJob(String domainConfig, String systemConfig ) {
		setup(domainConfig,   systemConfig);
		configure();
		execute();		
	}
	
	public void setup( String domainConfig, String systemConfig )  {
	    BasicUtils.aboutThreads(getName() + " | Before setup ");
		if( domainConfig != null ) {
			DomainSystemConfig.setTheConfiguration(domainConfig);
		}
		if( systemConfig != null ) {
			RadarSystemConfig.setTheConfiguration(systemConfig);
		}
		if( domainConfig == null && systemConfig == null) {
			DomainSystemConfig.simulation  = true;
	    	DomainSystemConfig.testing     = false;			
	    	DomainSystemConfig.tracing     = false;			
			DomainSystemConfig.sonarDelay  = 200;
	    	DomainSystemConfig.ledGui      = true;		//se siamo su PC	
	    	
		    CommSystemConfig.tracing       = true;

		    RadarSystemConfig.RadarGuiRemote   = true;		
 			RadarSystemConfig.ctxServerPort    = 8756;
 			RadarSystemConfig.protcolType      = ProtocolType.tcp;
		}
 
	}
	protected void configure() {		
 	   led   = DeviceFactory.createLed(); 
	   sonar = DeviceFactory.createSonar();
   
 	   //contextServer  = new TcpContextServer("TcpCtxServer",RadarSystemConfig.ctxServerPort); 	   
	   contextServer = new EnablerContext("ctx",""+RadarSystemConfig.ctxServerPort,
 			                  RadarSystemConfig.protcolType, new ContextMsgHandler("ctxH"));
		//Registrazione dei componenti presso il contesto
 	   IApplMsgHandler sonarHandler = SonarApplHandler.create("sonarH",sonar); 
	   IApplMsgHandler ledHandler   = LedApplHandler.create("ledH",led);		  
	   contextServer.addComponent("sonar", sonarHandler);	//sonar NAME mandatory
	   contextServer.addComponent("led",   ledHandler);		//led NAME mandatory
	}
	
	protected void execute() {		
		contextServer.activate();
	}
	
	public static void main( String[] args) throws Exception {
		//ColorsOut.out("Please set RadarSystemConfig.pcHostAddr in RadarSystemConfig.json");
		new RadarSystemMainDevsCtxOnRasp().doJob(null,null);
 	}

}
