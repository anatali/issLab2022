package it.unibo.supports.serial;
import it.unibo.is.interfaces.IOutputEnvView;
import it.unibo.system.SituatedPlainObject;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class PortReader extends SituatedPlainObject implements SerialPortEventListener {
protected SerialPort serialPort;
	public PortReader(SerialPort serialPort, IOutputEnvView outEnvView){
		super(outEnvView);
		this.serialPort = serialPort;
	}
	@Override
	public void serialEvent(SerialPortEvent event) {
		if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                String receivedData = serialPort.readString(event.getEventValue());
                println("Received response: " + receivedData);
            }
            catch (SerialPortException ex) {
                println("Error in receiving string from COM-port: " + ex);
            }
        }		 
		
	}

}
