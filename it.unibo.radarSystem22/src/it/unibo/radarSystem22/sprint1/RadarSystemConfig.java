package it.unibo.radarSystem22.sprint1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.json.JSONObject;
import org.json.JSONTokener;

import it.unibo.comm2022.ProtocolType;
import it.unibo.radarSystem22.domain.utils.ColorsOut;


public class RadarSystemConfig {
 	public static boolean tracing         = false;	
	public static boolean testing         = false;			
	public static int DLIMIT              =  15;     	
	public static boolean  RadarGuiRemote = false;
	
//Aggiunte dello SPRINT2	
 	public static String hostAddr         = "localhost";		
	public static String raspAddr         = "localhost";		
	public static int serverPort          = 8080;
//Aggiunte dello SPRINT2a e 3
	public static int ledPort             = 8010;
	public static int sonarPort           = 8015;
	public static ProtocolType protcolType= ProtocolType.tcp;
	
	public static void setTheConfiguration(  ) {
		setTheConfiguration("../RadarSystemConfig.json");
	}
	
	public static void setTheConfiguration( String resourceName ) {
		//Nella distribuzione resourceName è in una dir che include la bin  
		FileInputStream fis = null;
		try {
			ColorsOut.out("%%% setTheConfiguration from file:" + resourceName);
			if(  fis == null ) {
 				 fis = new FileInputStream(new File(resourceName));
			}
	        JSONTokener tokener = new JSONTokener(fis);
	        JSONObject object   = new JSONObject(tokener);
	 		
   	        tracing          = object.getBoolean("tracing");
	        testing          = object.getBoolean("testing");
	        RadarGuiRemote   = object.getBoolean("RadarGuiRemote");
	        DLIMIT           = object.getInt("DLIMIT");	
//Aggiunte dello SPRINT2	
	        serverPort		= object.getInt("serverPort");
 	        hostAddr 		= object.getString("hostAddr");
	        raspAddr 		= object.getString("raspAddr");
//Aggiunte dello SPRINT2a
	        ledPort         = object.getInt("ledPort");
	        sonarPort       = object.getInt("sonarPort");
	        
	        switch( object.getString("protocolType") ) {
		        case "tcp"  : protcolType = ProtocolType.tcp; break;
		        case "coap" : protcolType = ProtocolType.coap; break;
		        case "mqtt" : protcolType = ProtocolType.mqtt; break;
	        }	        
	        
		} catch (FileNotFoundException e) {
 			ColorsOut.outerr("setTheConfiguration ERROR " + e.getMessage() );
		}

	}	
	 
}
