package it.unibo.radarSystem22_4.appl.handler;

import it.unibo.radarSystem22.domain.utils.ColorsOut;
import it.unibo.radarSystem22_4.comm.ApplMsgHandler;
import it.unibo.radarSystem22_4.comm.interfaces.IApplMessage;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.CommUtils;
import it.unibo.radarSystem22_4.comm.utils.BasicUtils;


/*
 * Il gestore elabora una richiesta attivando un thread che
 * invia la risposta sulla connessione dopo un certo tempo.
 * 
 * Facciamo in modo che per la prima richiesta il tempo di risposta
 * sia di 3 secondi.
 * 
 * Un caller che invia due richieste 'in parallelo' riceve le risposte
 * in ordine inverso.
 */
public class RequestHandler extends ApplMsgHandler {
private boolean firstRequest = true;
	public RequestHandler(String name) {
		super(name);
 	}

	@Override
	public void elaborate(IApplMessage message, Interaction2021 conn) {
 		if( message.isRequest() ) {
			 elabRequest(message,conn);
 		}
	}
	
	protected void elabRequest(IApplMessage message, Interaction2021 conn) {
		new Thread() {
			public void run() {
	 			try {
				    if(  firstRequest ) {
						firstRequest = false;
						BasicUtils.delay(3000);
					}
	 		 		ColorsOut.outappl(name + " elaborate "+message, ColorsOut.GREEN);
	 			    IApplMessage answer = CommUtils.prepareReply( message, message.msgContent()+"_done");	 				
	 		 		ColorsOut.outappl(name + " sending "+answer, ColorsOut.GREEN);
					conn.reply(answer.toString());
				} catch (Exception e) {
					ColorsOut.outerr(name + "elaborate ERROR:" + e.getMessage());
				}			 
			}//run
		}.start();
	}

}
