/**
 *
 */
package it.unibo.platform.bth;

import it.unibo.is.interfaces.protocols.IConnInteraction;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.BluetoothStateException;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Simone
 *
 */
public class BthSupport implements DiscoveryListener {
	//near devices
		private static Vector devices       ;
	//found services
	 	private static Vector serviceRecords ;

	private LocalDevice localDevice;		
	private DiscoveryAgent discoveryAgent;
	
	private Hashtable<String,IConnInteraction> ht = new Hashtable<String,IConnInteraction>();
	/**
	 * Every Bluetooth service and service attribute has its own Universally Unique Identifier
	 * The UUID class represents short (16- or 32-bit) and long (128-bit) UUIDs.
	 */
 	private UUID[] uuid = {new UUID("8848", true)};  
 	private boolean discoveryCompleted = false;
 
	private boolean debug=true;
	private String logo;
	
	public BthSupport(String logo, boolean asServer){
		this.logo = logo;
		btInit(asServer);
		if( System.getProperty("tcpTrace") != null ) 
			debug = System.getProperty("tcpTrace").equals("set") ;		
	}
	
	protected void btInit(boolean asServer){
		try{
			localDevice =   LocalDevice.getLocalDevice();
			println("localDevice=" + localDevice.getFriendlyName());
			if( asServer ){
				localDevice.setDiscoverable(DiscoveryAgent.GIAC); 
			}else{
				discoveryAgent = localDevice.getDiscoveryAgent();
				initServiceRecord();
			}
			}catch(Exception e){
				e.printStackTrace();
			}		
	}
	
	//Initialize the service record (to avoid client search)
	public void initServiceRecord(){
		serviceRecords = SearchServicesAndDevices(uuid);
		println("initServiceRecord numDev= "+serviceRecords.size());
	}
	

/*
 * ------------------------------------------
 * SERVER (service)
 * ------------------------------------------
 */
 	public StreamConnectionNotifier connectAsReceiver(String p_servicename) throws Exception{
 		println("connectAsReceiver " + p_servicename);
 		//defines and instantiates an RFCOMM connection notifier, resulting in the creation of the service record
			StreamConnectionNotifier server = 
				(StreamConnectionNotifier) Connector.open( 
							"btspp://localhost:"
							+ uuid[0]
							+ ";name="
 							+ p_servicename
							+ ";authorize=false;authenticate=false;encrypt=false");
			return server;
 	}
 	
 	public IConnInteraction startService(String servicename) throws Exception{
 		StreamConnectionNotifier serverConn = connectAsReceiver(servicename);  
 		StreamConnection conn               = acceptAConnection(serverConn);	
		return new BthConnSupport( "bthConn",conn );
 	}
 	
	public StreamConnection acceptAConnection(StreamConnectionNotifier server) throws Exception{
		println("acceptAConnection " + server);
	 	// Insert service record into SDDB and wait for an incoming client
		return (StreamConnection)server.acceptAndOpen();						
	}
	public IConnInteraction setAConnection(StreamConnectionNotifier server) throws Exception{
		println("acceptAConnection " + server);
	 	// Insert service record into SDDB and wait for an incoming client
		StreamConnection conn = (StreamConnection)server.acceptAndOpen();		
		return new BthConnSupport( "bthConn",conn );
	}

	public void closeConnection(StreamConnectionNotifier server) throws Exception{
		server.close();						
	}
	
	public void closeConnection(StreamConnection conn) throws Exception{
		conn.close();						
	}
	
	/*
	 * ------------------------------------------
	 * CLIENT 
	 * ------------------------------------------
	 */

//	@Override
//	public StreamConnection connectAsClient(String devName, String p_servicename) throws Exception {
// 		return null;
//	}

 	public IConnInteraction connectAsClient(String p_servicename) throws Exception{
 			IConnInteraction conn = ht.get(p_servicename) ;
 			if( conn != null ) return conn;
 			
 			Vector record;
			if( serviceRecords == null ){
		 		println("connectAsClient  "+ p_servicename + " uuid=" + uuid);
	 			record = SearchServicesAndDevices(uuid);
				println("connectAsClient numDev= "+record.size());
			}else{
				record = serviceRecords;
				println("connectAsClient numDev= "+record.size());
			}
			
			for(int i=record.size()-1 ; i>=0 ; i--){
				String service=((ServiceRecord)record.get(i)).getAttributeValue(0x0100).toString();
				println("searchConnection  has found  service="+service + " vs. " + p_servicename);
 				if( service.contains(p_servicename) ){
					try{
						String str=((ServiceRecord)record.get(i)).getConnectionURL(
										ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
						println("ServiceRecord str= "+str);
						StreamConnection streamConn = (StreamConnection) Connector.open(
														str,Connector.READ_WRITE);
						 IConnInteraction kernel = new BthConnSupport( "",streamConn ) ;
						 ht.put(p_servicename, kernel) ;
//						 conn=new BluetoothConnection(service , streamConn);
//						 registerConnection(conn , conn.toString());
						return kernel;
					}catch(Exception e){
						println("searchConnection ERROR " + e);
						throw new Exception("connection error " +e.getMessage());
					}
 				}
			}//for
			throw new Exception("connection not found " );
//		  }else{
//		  	//println( " *** DeviceManager " + p_servicename + " HAS ALREADY A CONNECTION " );
//		  	return conn;
//		  }
	 	}

 	
	public String receiveALine(InputStream inputChannel) throws Exception {
		InputStreamReader inputReader = new InputStreamReader( inputChannel );
		BufferedReader inChannel      = new BufferedReader( inputReader );	
		DataInputStream  indata       = new DataInputStream(inputChannel);
//		String	cmd = inChannel.readLine();  //like a receive
		String	cmd =indata.readUTF();
			println( "has read ... " +cmd );
			return cmd;		
	}

	public void sendALine(OutputStream outputChannel, String msg)throws Exception {
	DataOutputStream outChannel = new DataOutputStream(outputChannel);
		outChannel.writeUTF( msg );		
	}


//	public synchronized IBluetoothConnection getConnection(
//			String ConnectionHandle, String ServiceName) throws Exception {
//		return currentConn;
//	}

	/**
	 * Search for devices and services
	 */
	public Vector SearchServicesAndDevices(UUID[] uuid) {
		this.uuid = uuid;
		println("SearchServicesAndDevices " + uuid);

		devices       = new Vector();  
		Vector devSet = new Vector();

		synchronized (this) {
			try {
//				LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, this);
				discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
				try {
					println("		SearchServicesAndDevices waits");
					wait();
					println("		SearchServicesAndDevices resumes");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch (BluetoothStateException e) {
				e.printStackTrace();
			}
		}
		println("SearchServicesAndDevices finds n=" + devices.size());
		println("------------------------------------------");

		for (Enumeration enum_d = devices.elements(); enum_d.hasMoreElements();) {
			RemoteDevice d = (RemoteDevice) enum_d.nextElement();
 			try {
				println("Device name=" +      d.getFriendlyName(false));
				println("BluetoothAddress=" + d.getBluetoothAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}

			serviceRecords = new Vector();

			synchronized (this) {
				try {
					LocalDevice
							.getLocalDevice()
							.getDiscoveryAgent()
							.searchServices(new int[] { 0x0100 }, uuid, d, this);
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} catch (BluetoothStateException e) {
					e.printStackTrace();
				}
			}
 			println("num of services === " + serviceRecords.size());

//			devSet.removeAllElements();
			for (Enumeration enum_r = serviceRecords.elements(); enum_r.hasMoreElements();) {
				ServiceRecord r = (ServiceRecord) enum_r.nextElement();
				devSet.add(r);
				println(" *** ServiceName="+r.getConnectionURL(r.NOAUTHENTICATE_NOENCRYPT, false)+ 
						" AttributeValue="+r.getAttributeValue(0x0100));
			}
		}
		return devSet;
	}

	/**
	 * Viene richiamato una volta che si trova un dispositivo
	 */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		println("discovers = " + cod);
		devices.addElement(btDevice);
 	}

	/**
	 * Ricerca dispositivi conclusa
	 */
	public synchronized void inquiryCompleted(int discType) {
		println(
				"inquiry completed: discType = "
				+ discType);
		notifyAll();
	}

	/**
	 * Viene richiamato quando si trovano dei servizi
	 */
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		for (int i = 0; i < servRecord.length; i++)
			serviceRecords.addElement(servRecord[i]);

	}

	/**
	 * Ricerca servizi conclusa
	 */
	public synchronized void serviceSearchCompleted(int transID, int respCode) {
		println(
				"service search completed: respCode = "
				+ respCode);
		discoveryCompleted = true;
		notifyAll();
	}


//	public synchronized void registerConnection(IBluetoothConnection conn , String id) throws Exception{
//		if(conn.getIndex()==-1)	{
//			conn.setIndex(indexConnection);
//			indexConnection++;
//		}
// 		println(" *** DeviceManager registerConnection " + conn.getIndex());
// 		currentConn = conn;
// 		// ..................
// 		startAReader(conn);
// 	}

	
	private Thread currentReader = null;
	
 	
//	protected void startAReader(IConnInteraction conn) throws Exception{
//		currentReader = new Thread( new ReaderServiceBth( conn ) );
//		currentReader.start();
//	}
	
	//....................................
//	public synchronized void unregisterConnection(IBluetoothConnection conn , String id) throws Exception{
//		int index=conn.getIndex();
//		if(index==-1)return;
//		println(" *** DeviceManager unregisterConnection " + conn.getIndex());
//		currentConn = null;
// 	}



	protected void print( String msg ){
		if( debug ) System.out.print(" ### BthSupport "+ msg);
	}

	protected void println( String msg ){
		if( debug ) System.out.println(" ### BthSupport "+msg);
	}
	
}

