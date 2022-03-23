package it.unibo.radarSystem22.sprint3;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.enablers.EnablerAsServer;
import it.unibo.comm2022.utils.BasicUtils;
import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.interfaces.ISonar;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22.sprint1.RadarSystemConfig;
import it.unibo.radarSystem22.sprint2a.proxy.LedProxyAsClient;
import it.unibo.radarSystem22.sprint2a.proxy.SonarProxyAsClient;
import it.unibo.radarSystem22.sprint3.handlers.LedApplHandler;
import it.unibo.radarSystem22.sprint3.handlers.SonarApplHandler;
import it.unibo.radarSystem22.domain.DeviceFactory;

public class TestEnablers {
	private ISonar sonar;
	private ILed  led ;
	private EnablerAsServer sonarServer, ledServer;
	private ISonar sonarPxy;
	private ILed ledPxy;
	
	@Before
	public void setup() {
 	  DomainSystemConfig.simulation = true;
 	  DomainSystemConfig.ledGui     = true;
 	  RadarSystemConfig.ledPort     = 8015;
 	  RadarSystemConfig.sonarPort   = 8011;	  
	  DomainSystemConfig.sonarDelay = 100;
	  
	  RadarSystemConfig.DLIMIT      = 70;
 	  RadarSystemConfig.tracing    = false;
//	  RadarSystemConfig.testing    = false;

	  ProtocolType protocol = ProtocolType.udp;
	  //I devices
	  sonar   = DeviceFactory.createSonar();
	  led     = DeviceFactory.createLed();

	  //I server
	  sonarServer = new EnablerAsServer("sonarSrv",
	              RadarSystemConfig.sonarPort,
	              protocol, SonarApplHandler.create("sonarH", sonar) );
	  ledServer   = new EnablerAsServer("ledSrv",
	              RadarSystemConfig.ledPort,
	              protocol, LedApplHandler.create("ledH", led)  );

	  //I proxy
	  sonarPxy = new SonarProxyAsClient( "sonarPxy", "localhost",
	            ""+RadarSystemConfig.sonarPort, protocol );
	  ledPxy   = new LedProxyAsClient( "ledPxy",   "localhost",
	           ""+RadarSystemConfig.ledPort,   protocol );
	}

	@After
	public void down() {
	  ledServer.stop();
	  sonarServer.stop();
	}
	
	@Test
    public void testEnablers() {

       sonarServer.start();
       ledServer.start();
       System.out.println(" ==================== testEnablers "  );

       //Simulo il Controller
  
       //Attivo il sonar
       sonarPxy.activate();

       for( int i=1; i<=30; i++ ) {
    	   int v = sonarPxy.getDistance().getVal();
           System.out.println("testEnablers v=" + v);
    	   BasicUtils.delay(DomainSystemConfig.sonarDelay);
    	   if( v < RadarSystemConfig.DLIMIT ) {
    		   ledPxy.turnOn();
    		   boolean ledState = ledPxy.getState();
    		   assertTrue( ledState );
    	   } 
    	   else {
    		   	ledPxy.turnOff();
    		   	boolean ledState = ledPxy.getState();
    		   	assertTrue( ! ledState );
    	   	}
       }
    }	
 	
	
}
