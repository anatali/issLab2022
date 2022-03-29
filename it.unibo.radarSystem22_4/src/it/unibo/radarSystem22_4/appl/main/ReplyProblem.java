package it.unibo.radarSystem22_4.appl.main;

import it.unibo.radarSystem22_4.appl.handler.RequestHandler;
import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.context.TcpContextServer;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMsgHandler;
import it.unibo.radarSystem22_4.comm.proxy.ProxyAsClient;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class ReplyProblem {
private int port = 8078;
 
	public void doJob() {
		//Attivo un server con Handler che attiva thread
		IApplMsgHandler rh1      = new RequestHandler("rh1"); 		 
		TcpContextServer server  = new TcpContextServer( "srv", port );		
		server.addComponent("rh1", rh1);
 		server.activate();

		ProxyAsClient pxy = new ProxyAsClient("pxy1", "localhost", ""+port, ProtocolType.tcp);

		//Attivo due Thread di richiesta
 		
		new Thread() {
			public void run() {
			    BasicUtils.aboutThreads(getName() + " | Before request   "  );
				IApplMessage m    = CommUtils.buildRequest("main", "req", "r1","rh1");
				String answer     = pxy.sendRequestOnConnection(m.toString());
			    BasicUtils.aboutThreads(getName() + " | After answer for " + m);
				ColorsOut.out("answer to r1:" + answer);
			}
		}.start();
		new Thread() {
			public void run() {
			    BasicUtils.aboutThreads(getName() + " | Before request   "  );
 				IApplMessage m = CommUtils.buildRequest("main", "req", "r2","rh1");
				String answer  = pxy.sendRequestOnConnection(m.toString());
			    BasicUtils.aboutThreads(getName() + " | After answer for " + m);
				ColorsOut.out("answer to r2:" + answer);
			}
		}.start();
		
		
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
