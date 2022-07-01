package unibo.comm22;

import unibo.comm22.interfaces.IApplMsgHandler;
import unibo.comm22.interfaces.Interaction2021;
import unibo.comm22.utils.ColorsOut;

public abstract class ApplMsgHandler  implements IApplMsgHandler {  
protected String name;
   
 	public ApplMsgHandler( String name  ) {  
		this.name = name;
	}
 	
 	public String getName() {
		return name;
	}	 
   	
 	public void sendMsgToClient( String message, Interaction2021 conn  ) {
		try {
  			ColorsOut.outappl(name + " | ApplMsgHandler sendMsgToClient conn=" + conn, ColorsOut.BLUE);
			conn.forward( message );
		} catch (Exception e) {
 			ColorsOut.outerr(name + " | ApplMsgHandler sendMsgToClient ERROR " + e.getMessage());;
		}
 	} 
 	
 	
 	@Override
 	public void sendAnswerToClient( String reply, Interaction2021 conn   ) {
		//sendMsgToClient(reply, conn);		
 		try {
			conn.reply(reply);
		} catch (Exception e) {
			ColorsOut.outerr(name + " | ApplMsgHandler sendAnswerToClient ERROR " + e.getMessage() );
		}
 	}
/* 	
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
 */	
 	public abstract void elaborate(String message, Interaction2021 conn) ;
 	
}
