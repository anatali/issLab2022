package it.unibo.radarSystem22.sprint2a.main.devicesOnRasp;

 
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.tcp.TcpServer;
import it.unibo.comm2022.utils.CommSystemConfig;
import it.unibo.radarSystem22.IApplication;
import it.unibo.radarSystem22.domain.DeviceFactory;
import it.unibo.radarSystem22.domain.interfaces.*;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22.sprint1.RadarSystemConfig;
import it.unibo.radarSystem22.sprint2a.handlers.LedApplHandler;
import it.unibo.radarSystem22.sprint2a.handlers.SonarApplHandler;

 
 
/*
 * Attiva il TCPServer.
 * 
 */
public class RadarSysSprint2aDevicesOnRaspMain implements IApplication{
	private ISonar sonar;
	private ILed  led ;
	private TcpServer ledServer ;
	private TcpServer sonarServer ;

	@Override
	public void doJob(String domainConfig, String systemConfig) {
		setup(domainConfig,   systemConfig);
		configure();
		execute();
	}
	
	public void setup( String domainConfig, String systemConfig )  {
	    BasicUtils.aboutThreads(getName() + " | Before setup ");
	    CommSystemConfig.tracing            = true;
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
	
			RadarSystemConfig.tracing           = false;		
			RadarSystemConfig.RadarGuiRemote    = true;		
		}
 
	}
	protected void configure() {		
 	   led        = DeviceFactory.createLed();
 	   IApplMsgHandler ledh = LedApplHandler.create("ledh", led);
 	   ledServer     = new TcpServer("ledServer",RadarSystemConfig.ledPort,ledh );

	   sonar      = DeviceFactory.createSonar();
 	   IApplMsgHandler sonarh = SonarApplHandler.create("sonarh", sonar);
 	   sonarServer   = new TcpServer("sonarServer",RadarSystemConfig.sonarPort,sonarh );

 	   
	}
	
	protected void execute() {		
		ledServer.activate();
		sonarServer.activate();
	}
	
	@Override
	public String getName() {
		return this.getClass().getName() ;  
	}

	public static void main( String[] args) throws Exception {
		BasicUtils.aboutThreads("At INIT with NO CONFIG files| ");
		new RadarSysSprint2aDevicesOnRaspMain().doJob(null,null);
  	}
}
