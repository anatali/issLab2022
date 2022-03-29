package it.unibo.radarSystem22_4.appl.usecases;

import it.unibo.radarSystem22.domain.interfaces.IDistance;
import it.unibo.radarSystem22.domain.interfaces.IRadarDisplay;

public class RadarGuiUsecase {
 
	public static void doUseCase( IRadarDisplay radar, IDistance d ) {	    
		//ColorsOut.out("RadarGuiUsecase |  doUseCase  d=" + d.getVal(), Colors.ANSI_YELLOW);
		if( radar != null ) {
			int v = d.getVal() ;
			radar.update(""+v, "30");
		}
  	}	
}
