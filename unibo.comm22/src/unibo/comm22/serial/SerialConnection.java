package unibo.comm22.serial;

import unibo.comm22.interfaces.Interaction2021;
import jssc.SerialPort;

public class SerialConnection implements Interaction2021{

	public SerialConnection(SerialPort port) {
		
	}

	@Override
	public void forward(String msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String request(String msg) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reply(String reqid) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receiveMsg() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
