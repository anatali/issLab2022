package it.unibo.radarSystem22.sprint2a;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.*;

import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
import it.unibo.radarSystem22.sprint1.ActionFunction;
import it.unibo.radarSystem22.sprint1.RadarSystemConfig;
import it.unibo.radarSystem22.sprint2a.main.devicesOnRasp.RadarSysSprint2aControllerOnPcMain;
import it.unibo.radarSystem22.domain.concrete.RadarDisplay;
import it.unibo.radarSystem22.domain.interfaces.IRadarDisplay;
import it.unibo.radarSystem22.domain.utils.BasicUtils; 

public class TestSystemAtSprint2a {
	private RadarSysSprint2aControllerOnPcMain sys; 
	private int delta = 1;

	@Before
	public void setUp() {
		System.out.println("setUp");
		try {
			sys = new RadarSysSprint2aControllerOnPcMain();
			sys.setup(  );
			sys.configure();
			DomainSystemConfig.testing    		= true;   
			DomainSystemConfig.tracing    		= true; 
			RadarSystemConfig.tracing    		= true; 
			RadarSystemConfig.DLIMIT    		= 16;   //OSTACOLO FISSO
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
 		testTheDistance( false );
 	}	
	
	//@Test 
	public void testNearDistance( ) {
 		testTheDistance( true );
	}	
	
	protected void testTheDistance( boolean ledStateExpected ) {
 		IRadarDisplay radar = RadarDisplay.getRadarDisplay();  //singleton
		ColorsOut.out("testTheDistance RadarSystemConfig.DLIMIT=" + RadarSystemConfig.DLIMIT);
		
		ActionFunction endFun = (n) -> { 
			System.out.println(n);
 			int radarDisplayedDistance = radar.getCurDistance();
 			
			ColorsOut.out("testTheDistance radarDisplayedDistance=" + radarDisplayedDistance);
 	 	    assertTrue(   radarDisplayedDistance >= RadarSystemConfig.DLIMIT - delta 
 	 	    		&& radarDisplayedDistance <= RadarSystemConfig.DLIMIT + delta );
		};

		sys.getController().start( endFun, 3 );  
		 
 	    BasicUtils.delay(1000) ; //give time to see ... 		
	}
 
}
