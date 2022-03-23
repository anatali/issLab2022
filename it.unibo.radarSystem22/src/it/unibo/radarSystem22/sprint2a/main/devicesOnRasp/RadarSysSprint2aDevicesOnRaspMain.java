package it.unibo.radarSystem22.sprint2a.main.devicesOnRasp;

 
import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.tcp.TcpServer;
import it.unibo.comm2022.udp.giannatempo.UdpServer;
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
	private UdpServer ledServerUdp ;
	private UdpServer sonarServerUdp ;

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
			RadarSystemConfig.protcolType       = ProtocolType.tcp;		
		}
 
	}
	protected void configure() {		
	  ProtocolType protocol = RadarSystemConfig.protcolType;
	   led        = DeviceFactory.createLed();
 	   IApplMsgHandler ledh = LedApplHandler.create("ledh", led);
 	   switch( protocol ) {
 	   	case tcp :  { ledServer     = new TcpServer("ledServer",RadarSystemConfig.ledPort,ledh );break;}
 	   	case udp:   { ledServerUdp  = new UdpServer("ledServerUdp",RadarSystemConfig.ledPort,ledh);break;}
 	   	default: break;
 	   }
// 	   ledServer     = new TcpServer("ledServer",RadarSystemConfig.ledPort,ledh );
  	  
	   sonar      = DeviceFactory.createSonar();
 	   IApplMsgHandler sonarh = SonarApplHandler.create("sonarh", sonar);
// 	   sonarServer   = new TcpServer("sonarServer",RadarSystemConfig.sonarPort,sonarh );
 	   switch( protocol ) {
	   	case tcp : { sonarServer   = new TcpServer("sonarServer",RadarSystemConfig.sonarPort,sonarh);break;}
	   	case udp:  { sonarServerUdp= new UdpServer("sonarServerUdp",RadarSystemConfig.sonarPort,sonarh);break;}
	   	default: break;
	   }
 	}
	
	protected void execute() {		
		switch( RadarSystemConfig.protcolType ) {
			case tcp : { ledServer.activate();    sonarServer.activate();    break;}
			case udp : { ledServerUdp.activate(); sonarServerUdp.activate(); break;}
			default: break;
		}
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
