package it.unibo.platform.bth;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.StreamConnection;
 



public interface IBluetoothConnection{
	
	/**
	 * Chiude la connessione e gli stream aperti
	 */
	public void Close();
	
	/**
	 * Restituisce gli lo stream d'uscita
	 */
	public DataOutputStream getOutputStream();
	
	/**
	 * Restituisce gli lo stream d'ingresso
	 */
	public DataInputStream getInputStream();
	
	/**
	 * Scrive un messaggio sullo Stream di uscita
	 */
	public void sendMsg(String msg)throws IOException;
	
	/**
	 * legge un messaggio dallo Stream di ingresso
	 * ----BLOCCANTE-----
	 */
	public String readMsg();
	/**
	 * Ritorna il nome del servizio a cui si è connessi
	 * @return Nme del servizio
	 */
	public String getNameService();
	/**
	 * Ritorna la connessione
	 * @return Stream Connection
	 */
	public StreamConnection getStreamConnection();
	
	public void setIndex(int index);
	
	public int getIndex();
	
	//public int getId();
	
}
