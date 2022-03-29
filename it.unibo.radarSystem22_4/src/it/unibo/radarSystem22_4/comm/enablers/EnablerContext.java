package it.unibo.radarSystem22_4.comm.enablers;

import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IContext;
import it.unibo.radarSystem22_4.comm.interfaces.IContextMsgHandler;
import it.unibo.radarSystem22_4.comm.tcp.TcpServer;
import it.unibo.radarSystem22_4.comm.udp.UdpServer;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class EnablerContext  implements IContext {  
protected IContextMsgHandler ctxMsgHandler; 
protected String name;
protected ProtocolType protocol;
protected TcpServer serverTcp;
protected UdpServer serverUdp;
protected boolean isactive = false;

	public EnablerContext( String name, String port, ProtocolType protocol, IContextMsgHandler handler )   { 
 		try {
			this.name     			= name;
			this.protocol 			= protocol;
			ctxMsgHandler           = handler;
 			if( protocol != null ) {
				setServerSupport( port, protocol, handler  );
			}else ColorsOut.out(name+" |  CREATED no protocol"  );
		} catch (Exception e) {
			ColorsOut.outerr(name+" |  CREATE Error: " + e.getMessage()  );
		}
	}
	
 	protected void setServerSupport( String portStr, ProtocolType protocol, IContextMsgHandler handler   ) throws Exception{
		if( protocol == ProtocolType.tcp  ) {
			int port = Integer.parseInt(portStr);
			serverTcp = new TcpServer( "ctxTcp" , port,  handler );
			ColorsOut.out(name+" |  CREATED  on port=" + port + " protocol=" + protocol + " handler="+handler);
		}
		else if( protocol == ProtocolType.udp ) {  
			int port = Integer.parseInt(portStr);
			serverUdp = new UdpServer("ctxUdp" ,port,handler);
		}
		else if( protocol == ProtocolType.coap ) {
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
 	@Override
	public void addComponent( String devname, IApplMsgHandler h) {
		ctxMsgHandler.addComponent(devname, h);
	}
 	@Override
	public void removeComponent( String devname ) {
		ctxMsgHandler.removeComponent( devname );
	}
	@Override
	public void activate() {
		ColorsOut.out(name+" |  activate ..." );
		switch( protocol ) {
	   		case tcp :  { serverTcp.activate();break;}
	   		case udp:   { serverUdp.activate();break;}
	   		default: break;
	    }
		isactive = true;		
	}

	@Override
	public void deactivate() {
 		//ColorsOut.out(name+" |  deactivate  "  );
		switch( protocol ) {
	   		case tcp :  { serverTcp.deactivate();break;}
	   		case udp:   { serverUdp.deactivate();break;}
	   		default: break;
	    }
		isactive = false;
	}
  	 
}
