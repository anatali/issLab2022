package it.unibo.radarSystem22_4.appl.counter;

import it.unibo.radarSystem22_4.appl.RadarSystemConfig;
import it.unibo.radarSystem22_4.comm.ApplMessage;
import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.context.ContextMsgHandler;
import it.unibo.radarSystem22_4.comm.context.TcpContextServer;
import it.unibo.radarSystem22_4.comm.enablers.EnablerContext;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.IContext;
import it.unibo.radarSystem22_4.comm.proxy.ProxyAsClient;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;

/*
 * Un oggetto contatore di nome 'counter' (classe CounterWithDelay) con valore iniziale 2 
 * esegue l'operazione dec rilasciando il controllo per un certo tempo.
 * Questo contatore viene reso capace di gestire messaggi da un CounterApplHandler che lo incapsula.
 * Due client inviano il comando (dispatch) dec a 'counter', che però non va a 0
 * Il sistema attiva 4 thread (main, TcpContextSerer e due client)
 */
public class SharedCounterExampleMain  {
private int ctxServerPort   = 7070;
private String delay        = "100"; //con delay = 0 funziona
private ProtocolType protocol;
IApplMessage msgDec = new ApplMessage(
	      "msg( dec, dispatch, main, counter, dec(DELAY), 1 )"
	      .replace("DELAY", delay));
 
	public void configure(  ) {
 		BasicUtils.aboutThreads("Before configure - ");
 		protocol = ProtocolType.tcp;
		CounterWithDelay counter         = new CounterWithDelay("counter");
 		CounterApplHandler counterH      = new CounterApplHandler("counterH", counter);
// 		TcpContextServer contextServer   = new TcpContextServer("TcpContextServer",  ctxServerPort );
		IContext  contextServer = new EnablerContext("ctx",""+ctxServerPort,protocol,
	                 new ContextMsgHandler("ctxH"));		
 		contextServer.addComponent(counter.getName(),counterH);	
 		contextServer.activate();    
 		BasicUtils.aboutThreads("After configure - ");
 	}
	
	public void execute() throws Exception {
		ProxyAsClient client1 = new ProxyAsClient("client1","localhost", ""+ctxServerPort, protocol);
 		ProxyAsClient client2 = new ProxyAsClient("client2","localhost", ""+ctxServerPort, protocol);
 		client1.sendCommandOnConnection(msgDec.toString());  
 		client2.sendCommandOnConnection(msgDec.toString());		
 		BasicUtils.aboutThreads("After client send - ");
	}
 
 
	public static void main( String[] args) throws Exception {	
		SharedCounterExampleMain sys = new SharedCounterExampleMain();		
		sys.configure();
		sys.execute();
 		Thread.sleep(2500);
 		BasicUtils.aboutThreads("Before end - ");
		System.exit(0);
	}
}
