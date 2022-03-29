package it.unibo.radarSystem22_4.appl.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import it.unibo.radarSystem22_4.comm.interfaces.IApplication;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class MainSelectOnRasp {
public HashMap<String,IApplication> programs = new HashMap<String,IApplication>();
	
	protected void outMenu() {
		for (String i : programs.keySet()) { //
			  System.out.println( ""+i + "    " + programs.get(i).getName() );
		}
 	}
	public void doChoice() {
		try {
			programs.put("1", new RadarSystemMainDevsCtxOnRasp() );				 
  			String i = "";
			outMenu();
			ColorsOut.outappl(">>>   ", ColorsOut.ANSI_PURPLE);
 			BufferedReader inputr = new BufferedReader(new InputStreamReader(System.in));
			i =  inputr.readLine();
 			programs.get( i ).doJob("DomainSystemConfig.json","RadarSystemConfig.json");
 		} catch ( Exception e) {
			 ColorsOut.outerr("ERROR:" + e.getMessage() );
		}
		
	}
	public static void main( String[] args) throws Exception {
		ColorsOut.outappl("---------------------------------------------------", ColorsOut.BLUE);
		ColorsOut.outappl("MainSelectOnRasp: this application uses Config Files", ColorsOut.BLUE);
		ColorsOut.outappl("---------------------------------------------------", ColorsOut.BLUE);
		new MainSelectOnRasp().doChoice();
	}
}
