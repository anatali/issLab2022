package it.unibo.radarSystem22.domain;

import it.unibo.radarSystem22.domain.concrete.RadarDisplay;
import it.unibo.radarSystem22.domain.interfaces.*;
import it.unibo.radarSystem22.domain.models.LedModel;
import it.unibo.radarSystem22.domain.models.SonarModel;
import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22.domain.utils.DomainSystemConfig;
//import it.unibo.radarSystem22.domain.concrete.SonarConcreteObservable;
//import it.unibo.radarSystem22.domain.mock.SonarMockObservable;

public class DeviceFactory {

	public static ILed createLed() {
		//Colors.out("DeviceFactory | createLed simulated="+RadarSystemConfig.simulation);
		if( DomainSystemConfig.simulation)  {
			return LedModel.createLedMock();
		}else {
			return LedModel.createLedConcrete();
		}
	}
	public static ISonar createSonar(boolean observable) {
//		if( observable ) return createSonarObservable();
//		else 
			return createSonar();
	}

	public static ISonar createSonar() {
		//Colors.out("DeviceFactory | createSonar simulated="+RadarSystemConfig.simulation);
		if( DomainSystemConfig.simulation)  {
			return SonarModel.createSonarMock();
		}else { 
			return SonarModel.createSonarConcrete();
		}
	}
//	public static ISonarObservable createSonarObservable() {
//		ColorsOut.out("DeviceFactory | createSonarObservable simulated="+DomainSystemConfig.simulation);
//		if( DomainSystemConfig.simulation)  {
//			return new SonarMockObservable();
//		}else { 
//			return new SonarConcreteObservable();
//		}	
//	}
	
	//We do not have mock for RadarGui
	public static IRadarDisplay createRadarGui() {
		return RadarDisplay.getRadarDisplay();
	}
	
}
