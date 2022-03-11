package it.unibo.radarSystem22.domain;

import static org.junit.Assert.assertTrue;
import org.junit.*;

import it.unibo.radarSystem22.domain.interfaces.ILed;
import it.unibo.radarSystem22.domain.mock.LedMock;
import it.unibo.radarSystem22.domain.utils.BasicUtils;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
 
public class TestLed {
 
	@Before
	public void up() {
		System.out.println("up");
	}
	
	@After
	public void down() {
		System.out.println("down");		
	}	
	
	@Test 
	public void testLedMock() {
		
		System.out.println("testLedMock");
		DomainSystemConfig.simulation = true; 
		
		ILed led = DeviceFactory.createLed();
		assertTrue( ! led.getState() );
		
 		led.turnOn();
		assertTrue(  led.getState() );
		
		BasicUtils.delay(1000);		//to see the ledgui
		
 		led.turnOff();
		assertTrue(  ! led.getState() );	
		
		BasicUtils.delay(1000);		//to see the ledgui
	}	
	
	//@Test 
	public void testLedConcrete() {
		
		System.out.println("testLedConcrete");
		DomainSystemConfig.simulation = false; 
		
		ILed led = DeviceFactory.createLed();
		assertTrue( ! led.getState() );
		
 		led.turnOn();
		assertTrue(  led.getState() );
		
 		led.turnOff();
		assertTrue(  ! led.getState() );		
	}		
	
 
}
