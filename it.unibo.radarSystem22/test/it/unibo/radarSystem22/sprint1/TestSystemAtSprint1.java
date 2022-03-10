package it.unibo.radarSystem22.sprint1;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.*;

import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22.domain.concrete.RadarDisplay;
import it.unibo.radarSystem22.domain.interfaces.IRadarDisplay;
import it.unibo.radarSystem22.domain.utils.BasicUtils; 

public class TestSystemAtSprint1 {
private RadarSystemSprint1Main sys;

	@Before
	public void setUp() {
		System.out.println("setUp");
		try {
			sys = new RadarSystemSprint1Main();
			sys.setup( null,null );
			sys.configure();
			DomainSystemConfig.testing    		= true;   
			DomainSystemConfig.tracing    		= true; 
			RadarSystemConfig.tracing    		= true; 
		} catch (Exception e) {
			fail("setup ERROR " + e.getMessage() );
 		}
	}
	
	@After
	public void endtest() {
		System.out.println("endtest");		
	}	
	
	
	@Test 
	public void testFarDistance() {
		DomainSystemConfig.testingDistance = DomainSystemConfig.DLIMIT +20;
		testTheDistance( false );
 	}	
	
	@Test 
	public void testNearDistance( ) {
		DomainSystemConfig.testingDistance = DomainSystemConfig.DLIMIT - 5;
		testTheDistance( true );
	}	
	
	protected void testTheDistance( boolean ledStateExpected ) {
		System.out.println("testDistance " + DomainSystemConfig.testingDistance );
		IRadarDisplay radar = RadarDisplay.getRadarDisplay();  //singleton
		
		ActionFunction endFun = (n) -> { 
			System.out.println(n);
			boolean ledState           = sys.getLed().getState();
			int radarDisplayedDistance = radar.getCurDistance();
			
			ColorsOut.out("ledState=" + ledState + " ledStateExpected=" + ledStateExpected + " radarDisplayedDistance=" + radarDisplayedDistance, ColorsOut.MAGENTA);
	 	    assertTrue(  ledState == ledStateExpected
	 	    		&& radarDisplayedDistance == DomainSystemConfig.testingDistance);
		};
		
		sys.getController().start( endFun, 1 ); //one-shot
  	    BasicUtils.delay(1000) ; //give time to see ... 		
	}
 
}
