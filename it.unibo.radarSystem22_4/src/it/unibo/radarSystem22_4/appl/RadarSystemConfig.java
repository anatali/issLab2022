package it.unibo.radarSystem22_4.appl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.json.JSONObject;
import org.json.JSONTokener;

import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22_4.comm.ProtocolType;


public class RadarSystemConfig {
 	public static boolean tracing         = false;	
	public static boolean testing         = false;			
	public static int DLIMIT              =  15;     	
	public static boolean  RadarGuiRemote = false;
	
 
 	public static String hostAddr         = "localhost";		
	public static String raspAddr         = "localhost";		
 
	//Aggiunte dello SPRINT4 	
	public static ProtocolType protcolType= ProtocolType.tcp;		
	public static int  ctxServerPort      = 8018;
	
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
//	        JSONTokener tokener = new JSONTokener(fis);
			Reader reader       = new InputStreamReader(fis);
			JSONTokener tokener = new JSONTokener(reader);      
	        JSONObject object   = new JSONObject(tokener);
	 		
   	        tracing          = object.getBoolean("tracing");
	        testing          = object.getBoolean("testing");
	        RadarGuiRemote   = object.getBoolean("RadarGuiRemote");
	        DLIMIT           = object.getInt("DLIMIT");	
 //Aggiunte dello SPRINT4 	        
	        ctxServerPort    = object.getInt("ctxServerPort");
	        
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
