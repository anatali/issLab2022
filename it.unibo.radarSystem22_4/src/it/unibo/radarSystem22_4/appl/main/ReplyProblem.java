package it.unibo.radarSystem22_4.appl.main;

import it.unibo.radarSystem22_4.appl.handler.RequestHandler;
import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.enablers.EnablerContext;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.proxy.ProxyAsClient;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class ReplyProblem {
private int port = 8078;
private ProxyAsClient pxy;
private EnablerContext server;

	public void doJob() {
		configure();
		execute();
		terminate();
	}
	
	protected void configure() {
		//Attivo un server con Handler che attiva thread
		IApplMsgHandler rh1      = new RequestHandler("rh1"); 		 
		//TcpContextServer server  = new TcpContextServer( "srv", port );	
		server  = new EnablerContext( "ctx", ""+port, ProtocolType.tcp );
		server.addComponent("rh1", rh1);
 		server.activate();

		pxy = new ProxyAsClient("pxy", "localhost", ""+port, ProtocolType.tcp);
	    BasicUtils.aboutThreads( "After configure   "  );		
	}
 
	protected void execute() {
	    BasicUtils.aboutThreads( "Before execute  "  );
		//Attivo due Thread di richiesta		
		new Thread() {
			public void run() {
				String thname = getName().toLowerCase().replace("-", "");
			    BasicUtils.aboutThreads(thname + " | Before request   "  );
				IApplMessage m    = CommUtils.buildRequest(thname, "req", "r1","rh1");
				String answer     = pxy.sendRequestOnConnection(m.toString());
			    BasicUtils.aboutThreads(thname + " | After answer for " + m);
				ColorsOut.out(thname + " answer to r1:" + answer);
			}
		}.start();
		new Thread() {
			public void run() {
				String thname = getName().toLowerCase().replace("-", "");
			    BasicUtils.aboutThreads(thname + " | Before request   "  );
 				IApplMessage m = CommUtils.buildRequest(thname, "req", "r2","rh1");
				String answer  = pxy.sendRequestOnConnection(m.toString());
			    BasicUtils.aboutThreads(thname + " | After answer for " + m);
				ColorsOut.out(thname + " answer to r2:" + answer);
			}
		}.start();
		
		
	}
	
	protected void terminate() {
		BasicUtils.delay(5000);
		ColorsOut.outappl("TERMINATE" , ColorsOut.BLUE);
		server.deactivate();
		pxy.close();
		
	}
	
	public static void main( String[] args) throws Exception {
		//ColorsOut.out("Please set RadarSystemConfig.pcHostAddr in RadarSystemConfig.json");
		new ReplyProblem().doJob( );
 	}

	
}
