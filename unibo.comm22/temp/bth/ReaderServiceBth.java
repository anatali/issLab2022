package it.unibo.platform.bth;
/**
  *  
 */
public class ReaderServiceBth implements Runnable{
   	private IBluetoothConnection connection;
 	private boolean debug = true;
	
	/**
	 * This reads from a connection to receive answer messages only.
	 */
	public ReaderServiceBth( IBluetoothConnection con ) throws Exception {		
  		this.connection=con;
		System.out.println(" *** ReaderServiceBth activated on " + connection.getNameService() ) ;
  	}
	
	public void run() {
		String request;
		println(" *** ReaderServiceBth waiting for some request " ) ;
		//Read and stores a message
		request = connection.readMsg();
		while( request != null && !request.equals("byeBye") ){
 		     try {
				println(" *** ReaderServiceBth received \n"+ request) ;
 		     	String header =  getHeader( request );
 		     	String body   =  getBody( request );

// 		     	tspace.out("message("+header+" ,'"+body+"')" );		     	
 		     	//this message is read by AnswerBth or by kernel.inAnswerString
    			request = connection.readMsg();
			 } catch (Exception e) {
				println(" *** ReaderServiceBth E= " +  e) ;
				break;
 			 }
		} 			
	}
 	
	public String getHeader( String msg ){
	String header = msg.substring(0,msg.indexOf("|"));
		//println(" *** ReaderServiceBth header= " +  header) ;
		return header;
	}
	
	public String getBody( String msg ){
	String body = msg.substring(msg.indexOf("|")+1);
		//println(" *** ReaderServiceBth body= " +  body) ;
		return body;
	}
	
	protected void print( String msg ){
		if( debug ) System.out.print(msg);
	}

	protected void println( String msg ){
		if( debug ) System.out.println(msg);
	}
 	
 }

