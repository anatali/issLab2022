package it.unibo.supports.serial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import it.unibo.is.interfaces.IOutputView;
import it.unibo.system.SituatedPlainObject;

public class SerialPortConnSupport extends SituatedPlainObject implements ISerialPortInteraction, SerialPortEventListener{
final static int SPACE_ASCII = 32;
final static int DASH_ASCII = 45;
final static int NEW_LINE_ASCII = 10;
final static int CR_ASCII = 13;
	

private SerialPort serialPort;
private List<String> list;
private String curString = "";
 

//private BufferedReader input;

	public SerialPortConnSupport( SerialPort serialPort, IOutputView outView ) {
		super(outView);
		this.serialPort = serialPort;
		init();
	}	
	protected void init( ){
		try {
			serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
			list          = new ArrayList<String>();			
 		} catch (Exception e) {
 			e.printStackTrace();
		}		
	}
	@Override
	public void sendALine(String msg) throws Exception {
  		serialPort.writeBytes(msg.getBytes());
  		serialPort.writeString("\n");
//		println("SerialPortConnSupport has sent   " + msg);
	}
	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
		sendALine( msg );		
 	}	
 	@Override 
	public synchronized String receiveALine() throws Exception { 	
// 		println(" SerialPortConnSupport receiveALine   " );
 		String result = "no data";
 		while( list.size() == 0 ){
// 			println("list empty. I'll wait" );
 			wait();
 		} 
		result = list.remove(0);
//		println(" SerialPortConnSupport receiveALine  result= " + result);
		return result;
  	}
	@Override
	public void closeConnection() throws Exception {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.closePort();
		}
	}
	
	@Override
	public void serialEvent(SerialPortEvent event) {		
		 if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String data = serialPort.readString(event.getEventValue());   
//                println("SerialPortConnSupport serialEvent ... " + data );                                           
	            this.notifyTheObservers(data);  //an observer can see the received data as it is
	            updateLines(data);
              }
            catch (SerialPortException ex) {
                println("Error in receiving string from COM-port: " + ex);
            }
        }		
	}
	
	protected synchronized void updateLines(String data){
		if( data.length()>0) {
            curString = curString + data;
            if( data.endsWith("\n")    ) {
// 	 			println("serialEvent data = " +  curString + " / " + curString.length());
				list.add(curString);
				this.notifyAll();
				curString = "";
			}
		}
	}
	
}
