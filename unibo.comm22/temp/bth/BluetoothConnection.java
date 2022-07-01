package it.unibo.platform.bth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BluetoothConnection implements IBluetoothConnection {
 	/**
	 * Connessione tra due dispositivi
	 */
	protected StreamConnection conn=null;
	/**
	 * Stream di uscita
	 */
	protected DataOutputStream dos=null;
	/**
	 * Stream di ingresso
	 */
	protected DataInputStream dis=null;    
	protected String NameService="";
	protected StreamConnectionNotifier server=null;

	protected int indexConn=-1;
	
	public BluetoothConnection(
			String NameService , StreamConnectionNotifier server,
			StreamConnection conn) throws IOException{
		this.server=server;
		this.conn=conn;
		this.NameService=NameService;
		dos = conn.openDataOutputStream();
		dis = conn.openDataInputStream();		
	}
	
	public BluetoothConnection(String NameService ,StreamConnection conn)throws IOException{
		this.conn=conn;
		this.NameService=NameService;
		dos = conn.openDataOutputStream();
		dis = conn.openDataInputStream();		
	}
	
	/**
	 * Chiude la connessione e gli stream aperti
	 */
	public synchronized void Close(){
		try {			
			dos.close();
			dis.close();
			conn.close();
			if(server!=null)server.close();
			System.out.println("Connessione conclusa");			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Restituisce gli lo stream d'ingresso
	 */
	public DataInputStream getInputStream(){		
		return dis;		
	}
	/**
	 * Restituisce gli lo stream d'uscita
	 */
	public DataOutputStream getOutputStream(){		
		return dos;		
	}
	
	/**
	 * Scrive un messaggio sullo Stream di uscita
	 */
	public void sendMsg(String msg)throws IOException{		
		dos = conn.openDataOutputStream();
		dos.writeUTF(msg);
		dos.close();
	}
	/**
	 * Legge un messaggio sullo Stream di uscita
	 * ***** BLOCCANTE *******
	 */
	public String readMsg(){
		for(;;){
			try{
				return dis.readUTF();					
			}catch(NullPointerException e){
			}catch(Exception e){				
				try{
					System.out.println(" BluetoothConnection readMsg ??? E= "+e.getMessage());
					conn.openDataOutputStream().writeUTF("");
				}catch(IOException e1){					
					return null;
				}				
			}
			try{
				System.out.println(" BluetoothConnection readMsg sleeping ???  ");
				Thread.sleep(500);
			}catch(Exception e1){}			
		}
	}	

	public String getNameService(){		
		return NameService;		
	}
	
	public StreamConnection getStreamConnection(){		
		return conn;		
	}
	
	public void setIndex(int index){		
		indexConn=index;
	}
	
	public int getIndex(){		
		return indexConn;
	}

 		
}
