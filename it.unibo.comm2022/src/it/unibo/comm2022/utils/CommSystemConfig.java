package it.unibo.comm2022.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.json.JSONObject;
import org.json.JSONTokener;
import it.unibo.comm2022.ProtocolType;


public class CommSystemConfig {
	
	public static  boolean withContext   = true;
	public static  String pcHostAddr     = "192.168.1.132";
	public static  String raspHostAddr   = "192.168.1.4";
	
	public static  int radarGuiPort      = 8014;
	public static  int ledPort           = 8010;
	public static  int sonarPort         = 8012;
	public static  int controllerPort    = 8016;
	public static  int ctxServerPort     = 8048;
	public static  String mqttBrokerAddr = "tcp://localhost:1883"; //: 1883  OPTIONAL  tcp://broker.hivemq.com
 	
	public static int serverTimeOut       =  600000;  //10 minuti
	
 	public static ProtocolType protcolType = ProtocolType.tcp;

	public static void setTheConfiguration(  ) {
		setTheConfiguration("../CommSystemConfig.json");
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
	        
	        pcHostAddr       = object.getString("pcHostAddr");
	        raspHostAddr     = object.getString("raspHostAddr");

	        ledPort          = object.getInt("ledPort");
	        radarGuiPort     = object.getInt("radarGuiPort");
	        sonarPort        = object.getInt("sonarPort");
	        controllerPort   = object.getInt("controllerPort");		
	        
	        ctxServerPort    = object.getInt("ctxServerPort");
	        mqttBrokerAddr   = object.getString("mqttBrokerAddr");
	        
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
