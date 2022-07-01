package unibo.comm22.mqtt;

 
 
public class NaiveMessageHandler extends ApplMsgHandler{
 	public NaiveMessageHandler(String name) {
		super(name);
 	}

	@Override
	public void elaborate( IApplMessage message, Interaction2021 conn ) {}

	@Override
	public void elaborate(String message, Interaction2021 conn) {
		ColorsOut.out(name + " | elaborate " + message );
 	}
	
 
}
