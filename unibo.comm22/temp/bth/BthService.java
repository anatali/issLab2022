package it.unibo.platform.bth;

import it.unibo.is.interfaces.protocols.IConnInteraction;

/**
  *  
 */
public class BthService implements Runnable{
   	private IConnInteraction connection;
 	private boolean debug = true;
	
	/**
	 * This reads from a connection to receive answer messages only.
	 */
	public BthService( IConnInteraction con ) throws Exception {		
  		this.connection=con;
		System.out.println(" *** BthService activated on "  ) ;
		this.run();
  	}
	
	public void run() {
	    try {
			String request;
			println(" *** BthService waiting for some request " ) ;
			//Read and stores a message
			request = connection.receiveALine();
			while( request != null && !request.equals("byeBye") ){
					println(" *** BthService received \n"+ request) ;
//	 		     	String header =  getHeader( request );
//	 		     	String body   =  getBody( request );
	 		     	connection.sendALine( "AnswerTo_"+request);
	// 		     	tspace.out("message("+header+" ,'"+body+"')" );		     	
	 		     	//this message is read by AnswerBth or by kernel.inAnswerString
	    			request = connection.receiveALine();
			} 			
		} catch (Exception e) {
			println(" *** BthService ENDS " +  e.getMessage()) ;
 		}
	}
 	
	public String getHeader( String msg ){
	String header = msg.substring(0,msg.indexOf("|"));
		//println(" *** BthService header= " +  header) ;
		return header;
	}
	
	public String getBody( String msg ){
	String body = msg.substring(msg.indexOf("|")+1);
		//println(" *** BthService body= " +  body) ;
		return body;
	}
	
	protected void print( String msg ){
		if( debug ) System.out.print(msg);
	}

	protected void println( String msg ){
		if( debug ) System.out.println(msg);
	}
 	
 }

