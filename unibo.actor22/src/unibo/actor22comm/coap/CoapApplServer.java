package unibo.actor22comm.coap;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.Resource;

import it.unibo.kactor.CoapResourceCtx;
import unibo.actor22comm.interfaces.IApplMsgHandler;
import unibo.actor22comm.interfaces.IContext;
import unibo.actor22comm.utils.ColorsOut;
 
public class CoapApplServer extends CoapServer implements IContext{
	
	
/*
 * Viene creato da Qak22Context.InitCoap
 */

	private static CoapResource root      = new CoapResource("actors"); //new CoapResourceCtx("actors", null);  
	private static CoapApplServer server  = null;

	public CoapApplServer( int port ) {
		super(port);
		add( root );
		ColorsOut.outappl("CoapApplServer STARTEDDDD root=" + root, ColorsOut.YELLOW_BACKGROUND );
		server = this;
		start();
	}
	
	public static CoapApplServer getTheServer() {
		//if( server == null ) server = new CoapApplServer(8083);
		return server;
	}
	public static void stopTheServer() {
		if( server != null ) {
			server.stop();
			server.destroy();
			ColorsOut.out("CoapApplServer STOPPED", ColorsOut.GREEN );
		}
	}
	
//	private CoapApplServer(){
////		CoapResource outputRes= new CoapResource("output");
////		outputRes.add( new CoapResource("lights"));
////		root.add(outputRes);
////		root.add(new CoapResource("input"));
//		add( root );
//		ColorsOut.outappl("CoapApplServer STARTEDDDD root=" + root, ColorsOut.YELLOW_BACKGROUND );
// 
//		start();
//	}
	
	public static Resource getResource( String uri ) {
		return getResource( root, uri );		
	}
	
	//Depth-first research
	private static Resource getResource(Resource root, String uri) {
		if( root != null ) {
			ColorsOut.out("getResource checks in: " + root.getName() + " for uri=" + uri);
			Collection<Resource> rootChilds = root.getChildren();
			Iterator<Resource> iter         = rootChilds.iterator();
			ColorsOut.out("child size:"+rootChilds.size());
			while( iter.hasNext() ) {
				Resource curRes = iter.next();
				String curUri   = curRes.getURI();
				ColorsOut.out("getResource curUri:"+curUri);
				if( curUri.equals(uri) ){
					ColorsOut.out("getResource finds "+ curRes.getName() + " for " + curUri, ColorsOut.ANSI_YELLOW);
					return  curRes;
				}else { 
					//Colors.out("getResource restart from:"+curRes.getName());
					Resource subRes = getResource(curRes,uri); 
					if( subRes != null ) return subRes;					
				}
			}//while  (all sons explored)
		}
		return null;
	}
 
	public  void addCoapResource( CoapResource resource, String fatherUri  )   {
		ColorsOut.outappl("CoapApplServer | addCoapResource resource=" + resource.getName(), ColorsOut.YELLOW_BACKGROUND );
		root.add(resource);
//		Resource res = getResource("/"+fatherUri);
//		ColorsOut.outappl("CoapApplServer | addCoapResource res=" + res, ColorsOut.YELLOW_BACKGROUND );
//		if( res != null ) {
//			res.add( resource );
//			ColorsOut.outappl("CoapApplServer | added " + resource.getName() + " father=" + fatherUri, ColorsOut.YELLOW_BACKGROUND );
//		}else {
//			ColorsOut.outerr("addCoapResource FAILS for " + fatherUri);
//		}
	}
	
//	@Override
//	public void addComponent(String name, IApplMsgHandler h) {
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void removeComponent(String name) {
//		// TODO Auto-generated method stub
//		
//	}
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}
 
 

}
