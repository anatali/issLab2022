package it.unibo.radarSystem22.sprint2.main.sysOnRasp;

import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.utils.ColorsOut;
import it.unibo.radarSystem22.IApplication;
import it.unibo.radarSystem22.domain.interfaces.*;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22.sprint1.Controller;
import it.unibo.radarSystem22.sprint1.RadarSystemConfig;
import it.unibo.radarSystem22.sprint2.proxy.RadarGuiProxyAsClient;
 
 
/*
 * Attiva il sistema su Raspberry, Controller compreso.
 * Accede al RadarDisplay su PC tramite proxy.
 * 
 */
public class RadarSysSprint2RadarCallOnRaspMain implements IApplication{
	private IRadarDisplay radar;

	@Override
	public void doJob(String domainConfig, String systemConfig) {
		setup(domainConfig,   systemConfig);
		configure();
		execute();
	}
	
	public void setup( String domainConfig, String systemConfig )  {
	    BasicUtils.aboutThreads("Before setup ");
		if( domainConfig != null ) {
			DomainSystemConfig.setTheConfiguration(domainConfig);
		}
		if( systemConfig != null ) {
			RadarSystemConfig.setTheConfiguration(systemConfig);
		}
		if( domainConfig == null && systemConfig == null) {
 	    	
			RadarSystemConfig.RadarGuiRemote    = true;		
			RadarSystemConfig.serverPort        = 8080;		
			RadarSystemConfig.hostAddr          = "localhost";
	    	RadarSystemConfig.DLIMIT            = 75;
		}
	}
	protected void configure() {		
 	    radar      = new  RadarGuiProxyAsClient("radarPxy", 
	    		      RadarSystemConfig.hostAddr, 
		              ""+RadarSystemConfig.serverPort, 
		              ProtocolType.tcp); 	
	}
	
	protected void execute() {
		while(true) {
			for( int i = 1; i <= 40; i++) {
				int d = 5+i*2;
				radar.update(""+d, "90");
		 		ColorsOut.outappl("CURRENT DISTANCE=" + d,ColorsOut.MAGENTA );
		 		BasicUtils.delay(1000);
			}
			BasicUtils.waitTheUser();		 		
		}
	}
	public void terminate() {
		System.exit(0);
	}	
	@Override
	public String getName() {
		return this.getClass().getName() ;  
	}

	public static void main( String[] args) throws Exception {
		BasicUtils.aboutThreads("At INIT with NO CONFIG files| ");
		new RadarSysSprint2RadarCallOnRaspMain().doJob(null,null);
  	}
}
