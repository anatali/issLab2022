package it.unibo.radarSystem22_4.comm.proxy;

import it.unibo.radarSystem22_4.comm.ProtocolType;
import it.unibo.radarSystem22_4.comm.tcp.*;
import it.unibo.radarSystem22_4.comm.udp.*;
import it.unibo.radarSystem22_4.comm.interfaces.Interaction2021;
import it.unibo.radarSystem22_4.comm.utils.ColorsOut;

public class ProxyAsClient {
private Interaction2021 conn; 
protected String name ;		//could be a uri
protected ProtocolType protocol ;

/*
 * Realizza la connessione di tipo Interaction2021 (concetto astratto)
 * in modo diverso, a seconda del protocollo indicato (tecnologia specifica)
 */
 
	public ProxyAsClient( String name, String host, String entry, ProtocolType protocol ) {
		try {
			ColorsOut.outappl(name+"  | CREATING entry= "+entry+" protocol=" + protocol, ColorsOut.BLUE );
			this.name     = name;
			this.protocol = protocol;			 
			setConnection(host,  entry,  protocol);
			ColorsOut.out(name+"  | CREATED entry= "+entry+" conn=" + conn, ColorsOut.BLUE );
		} catch (Exception e) {
			ColorsOut.outerr( name+"  |  ERROR " + e.getMessage());		}
	}
	
 	
	protected void setConnection( String host, String entry, ProtocolType protocol  ) throws Exception {
		switch( protocol ) {
			case tcp : {
				int port = Integer.parseInt(entry);
				//conn = new TcpConnection( new Socket( host, port ) ) ; //non fa attempts
				conn = TcpClientSupport.connect(host,  port, 10); //10 = num of attempts
				ColorsOut.out(name + " |  setConnection "  + conn, ColorsOut.BLUE );		
				break;
			}
			case udp : {
				int port = Integer.parseInt(entry);
 				conn = UdpClientSupport.connect(host,  port );  
				break;
			}
			case coap : {
 				//conn = new CoapConnection( host,  entry );
				break;
			}
			case mqtt : {
				//La connessione col Broker viene stabilita in fase di configurazione
				//La entry è quella definita per ricevere risposte;
				//ColorsOut.out(name+"  | ProxyAsClient connect MQTT entry=" + entry );
				//conn = MqttConnection.getSupport();					
 				break;
			}	
			default :{
				ColorsOut.outerr(name + " | Protocol unknown");
			}
		}
	}
  	
	public void sendCommandOnConnection( String cmd )  {
 		ColorsOut.out( name+"  | sendCommandOnConnection " + cmd + " conn=" + conn, ColorsOut.BLUE );
		try {
			conn.forward(cmd);
		} catch (Exception e) {
			ColorsOut.outerr( name+"  | sendCommandOnConnection ERROR=" + e.getMessage()  );
		}
	}
	
	public String sendRequestOnConnection( String request )  {
 		ColorsOut.out( name+"  | sendRequestOnConnection request=" + request + " conn=" + conn, ColorsOut.BLUE );
		try {
			String answer = conn.request(request);
			ColorsOut.out( name+"  | sendRequestOnConnection-answer=" + answer, ColorsOut.BLUE  );
			return  answer  ;
			//return CommUtils.getContent( answer );
 		
		} catch (Exception e) {
			ColorsOut.outerr( name+"  | sendRequestOnConnection ERROR=" + e.getMessage()  );
			return null;
		}
 	}	
	public Interaction2021 getConn() {
		return conn;
	}
	
	public void close() {
		try {
			conn.close();
			ColorsOut.out(name + " |  CLOSED " + conn   );
		} catch (Exception e) {
			ColorsOut.outerr( name+"  | sendRequestOnConnection ERROR=" + e.getMessage()  );		}
	}
}
