package it.unibo.radarSystem22_4.appl.usecases;

 
import it.unibo.radarSystem22_4.appl.RadarSystemConfig;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;
import it.unibo.radarSystem22.domain.interfaces.*;

public class LedAlarmUsecase {
 	public static void doUseCase(ILed led, IDistance d) {
 		try {
 			ColorsOut.out("LedAlarmUsecase | sonar distance=" + d.getVal() + "|"+RadarSystemConfig.DLIMIT );
			if( d.getVal() <  RadarSystemConfig.DLIMIT ) led.turnOn(); else  led.turnOff();
 		} catch (Exception e) {
 			ColorsOut.outerr("LedAlarmUsecase | ERROR " + e.getMessage() );
		}					
 	}
 
}
