package unibo.actor22comm.serial;

 
 
 
import jssc.SerialPort;
import unibo.actor22comm.interfaces.Interaction2021;

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

	@Override
	public void sendALine(String msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receiveALine() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void closeConnection() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
