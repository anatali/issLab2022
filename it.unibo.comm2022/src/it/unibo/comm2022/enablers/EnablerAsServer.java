package it.unibo.comm2022.enablers;

import it.unibo.comm2022.ProtocolType;
import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.tcp.TcpServer;
import it.unibo.comm2022.utils.ColorsOut;
/*
 * Attiva un server relativo al protocollo specificato (se non null)
 */
 
public class EnablerAsServer   {  
private static int count=1;
protected String name;
protected ProtocolType protocol;
protected TcpServer serverTcp;
protected boolean isactive = false;

	public EnablerAsServer( String name, int port, ProtocolType protocol, IApplMsgHandler handler )   { 
 		try {
			this.name     			= name;
			this.protocol 			= protocol;
 			if( protocol != null ) {
				setServerSupport( port, protocol, handler  );
			}else ColorsOut.out(name+" |  CREATED no protocol"  );
		} catch (Exception e) {
			ColorsOut.outerr(name+" |  CREATE Error: " + e.getMessage()  );
		}
	}
	
 	protected void setServerSupport( int port, ProtocolType protocol, IApplMsgHandler handler   ) throws Exception{
		if( protocol == ProtocolType.tcp || protocol == ProtocolType.udp) {
			serverTcp = new TcpServer( "EnabSrvTcp_"+count++, port,  handler );
			ColorsOut.out(name+" |  CREATED  on port=" + port + " protocol=" + protocol + " handler="+handler);
		}else if( protocol == ProtocolType.coap ) {
			//CoapApplServer.getTheServer();	//Le risorse sono create alla configurazione del sistema
			ColorsOut.out(name+" |  CREATED  CoapApplServer"  );
		}
		else if( protocol == ProtocolType.mqtt ) {  
			ColorsOut.out(name+" |  Do nothing for mqtt" );
		}
	}	
 	
 	public String getName() {
 		return name;
	}
 	public boolean isActive() {
 		return isactive;
 	}
	public void  start() {
		if( protocol == ProtocolType.tcp || protocol == ProtocolType.udp ) {
	 		//Colors.out(name+" |  ACTIVATE"   );
			serverTcp.activate();
			isactive = true;
		} 			
 	}
 
 	public void stop() {
 		//Colors.out(name+" |  deactivate  "  );
		if( protocol == ProtocolType.tcp ) {
			serverTcp.deactivate();
			isactive = false;
		} 		
 	}
  	 
}
