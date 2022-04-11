package unibo.actor22.common;
 
import it.unibo.kactor.IApplMessage;
import unibo.actor22.*;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

/*
 * Il controller conosce SOLO I NOMI dei dispositivi 
 * (non ha riferimenti ai dispositivi-attori)
 */
public class ControllerForLedActor extends QakActor22{
protected int numIter = 1;
//protected IApplMessage getStateRequest ;  //Eliminato per osservazione Filoni
protected boolean on = true;

	public ControllerForLedActor(String name  ) {
		super(name);
		//getStateRequest  = Qak22Util.buildRequest(name,"ask", ApplData.reqLedState, ApplData.ledName);
 	}

	@Override
	protected void handleMsg(IApplMessage msg) {  
		if( msg.isReply() ) {
			elabAnswer(msg);
		}else { 
			elabCmd(msg) ;	
		}
 	}
	
	protected void elabCmd(IApplMessage msg) {
		String msgCmd = msg.msgContent();
		ColorsOut.outappl( getName()  + " | elabCmd=" + msgCmd, ColorsOut.GREEN);
		switch( msgCmd ) {
			case ApplData.cmdActivate : {
				forward(ApplData.activateSonar);
				doControllerWork();
	 			break;
			}
			default:break;
		}		
	}
	
	protected void wrongBehavior() {
  	    //WARNING: Inviare un treno di messaggi VA EVITATO
		//mantiene il controllo del Thread degli attori (qaksingle)		
		for( int i=1; i<=3; i++) {
			forward( ApplData.turnOffLed );
			CommUtils.delay(500);
			forward( ApplData.turnOnLed );
			CommUtils.delay(500);		
		}
		forward( ApplData.turnOffLed );
	}
    protected void doControllerWork() {
		CommUtils.aboutThreads(getName()  + " |  Before doControllerWork on=" + on );
		//wrongBehavior();
  		//ColorsOut.outappl( getName()  + " | numIter=" + numIter  , ColorsOut.GREEN);		
	    if( numIter++ < 5 ) {
	        if( numIter%2 == 1)  forward( ApplData.turnOnLed ); //accesione
	        else forward( ApplData.turnOffLed ); //spegnimento
	        //Messaggio ri creato per osservazione di Filoni
	        IApplMessage getStateRequest  = Qak22Util.buildRequest(getName(),"ask", ApplData.reqLedState, ApplData.ledName);
	        request(getStateRequest);
	      }else {
	    	  forward( ApplData.turnOffLed );
	  		  forward( ApplData.deactivateSonar );
			  ColorsOut.outappl(getName() + " | emit " + ApplData.endWorkEvent, ColorsOut.MAGENTA);
	    	  emit( ApplData.endWorkEvent );
	      }
		
	}
	
	protected void elabAnswer(IApplMessage msg) {
		ColorsOut.outappl( getName()  + " | elabAnswer numIter=" + numIter + " "+ msg, ColorsOut.MAGENTA);
 		CommUtils.delay(500);
		doControllerWork();
	}

}
