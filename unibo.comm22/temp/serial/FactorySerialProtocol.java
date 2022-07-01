package it.unibo.supports.serial;
import jssc.SerialPort;
import jssc.SerialPortEventListener;
import it.unibo.is.interfaces.IObserver;
import it.unibo.is.interfaces.IOutputView;
import it.unibo.is.interfaces.protocols.IConnInteraction;
import it.unibo.supports.FactoryProtocol;
import it.unibo.system.SituatedPlainObject;

public class FactorySerialProtocol extends FactoryProtocol{
private SerialPortSupport serialSupport ;

	public FactorySerialProtocol(IOutputView outView, String worker ){
		super(outView, "SERIAL", worker);
		serialSupport = new SerialPortSupport( outView );
	 	if( System.getProperty("SerialTrace") != null ) 
			debug = System.getProperty("SerialTrace").equals("set") ;
	}
 
//	public  IConnInteraction createSerialProtocolSupport( 
//			String portName, SerialPortEventListener listener, boolean notifyOnData ) throws Exception{
//		showMsg("FactorySerialProtocol createSerialProtocolSupport " + portName);
////		SerialPort serialPort = serialSupport.connect( portName, listener.getClass()  );	//bloccante
//		SerialPort serialPort = serialSupport.connect( portName  );	//bloccante
// 		if( notifyOnData ){
// 			serialPort.addEventListener( listener );
////			serialPort.notifyOnDataAvailable(true);	
////			serialPort.notifyAll();
// 		}
// 		IConnInteraction connection  = new SerialPortConnSupport(serialPort, outView);	
//		println("*** FactorySerialProtocol: " + this.worker + " is now connected on serial port " + portName );
//		return connection;		
//	}
	
	public  IConnInteraction createSerialProtocolSupport( 
			String portName, IObserver observer ) throws Exception{
		showMsg("FactorySerialProtocol createSerialProtocolSupport " + portName);
 		SerialPort serialPort = serialSupport.connect( portName  );	//bloccante
 		IConnInteraction connection  = new SerialPortConnSupport(serialPort, outView);	
		println("*** FactorySerialProtocol: " + worker + " now connected on port " + portName + " observer=" + observer);
		if(observer!=null) ((SituatedPlainObject) connection).addObserver(observer);
		return connection;		
	}
	
}
