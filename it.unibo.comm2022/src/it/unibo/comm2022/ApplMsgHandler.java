package it.unibo.comm2022;

import it.unibo.comm2022.interfaces.IApplMsgHandler;
import it.unibo.comm2022.interfaces.Interaction2021;
import it.unibo.comm2022.utils.ColorsOut;
import it.unibo.comm2022.utils.CommSystemConfig;

public abstract class ApplMsgHandler  implements IApplMsgHandler {  
protected String name;
   
 	public ApplMsgHandler( String name  ) {  
		this.name = name;
		//Colors.out(name + " | CREATING ... ", Colors.BLUE );
	}
 	
 	public String getName() {
		return name;
	}	 
   	
 	//Warning: le risposte sono messaggi 'unstructured'
 	public void sendMsgToClient( String message, Interaction2021 conn  ) {
 		try {
 			ColorsOut.out(name + " | ApplMsgHandler sendMsgToClient message=" + message + " conn=" + conn, ColorsOut.BLUE);
			conn.forward( message );
		} catch (Exception e) {
 			ColorsOut.outerr(name + " | ApplMsgHandler sendMsgToClient ERROR " + e.getMessage());;
		}
 	} 
 	
 	
 	@Override
 	public void sendAnswerToClient( String reply, Interaction2021 conn   ) {
		sendMsgToClient(reply, conn);		
 	}
 	
 	//@Override
 	public void sendAnswerToClient( String reply  ) {
		ColorsOut.out(name + " | ApplMsgHandler sendAnswerToClient reply=" + reply, ColorsOut.BLUE);
		try {
			if( CommSystemConfig.protcolType == ProtocolType.mqtt) {
				//TODO
			}else {
				ColorsOut.outerr(name + " | ApplMsgHandler sendAnswerToClient not implemented for  " 
							+ CommSystemConfig.protcolType);
			}
		} catch (Exception e) {
			ColorsOut.outerr(name + " | ApplMsgHandler sendAnswerToClient ERROR " + e.getMessage());
 		}
  	}
 	
 	public abstract void elaborate(String message, Interaction2021 conn) ;
 	
}
