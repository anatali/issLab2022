package unibo.actor22;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import it.unibo.kactor.IApplMessage;
import unibo.actor22.annotations.ActorLocal;
import unibo.actor22.annotations.ActorRemote;
import unibo.actor22comm.ProtocolInfo;
import unibo.actor22comm.ProtocolType;
import unibo.actor22comm.events.EventMsgHandler;
import unibo.actor22comm.proxy.ProxyAsClient;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;

 

public class Qak22Context {
	private static HashMap<String,QakActor22> ctxMap      = new HashMap<String,QakActor22>();
    private static HashMap<String,ProxyAsClient> proxyMap = new HashMap<String,ProxyAsClient>();

    public static final String actorReplyPrefix = "arply_";
    
    public static void setActorAsRemote(
    		String actorName, String entry, String host, ProtocolType protocol ) {
    	    ProxyAsClient pxy = proxyMap.get(host+"Pxy");
     		if( pxy == null ) { //un solo proxy per contesto remoto
	    		pxy = new ProxyAsClient(host+"Pxy", host, entry, protocol);
	    		ColorsOut.outappl("Qak22Context | CREATED proxy for " + host + " since:" + actorName, ColorsOut.MAGENTA);
	    		proxyMap.put(host+"Pxy", pxy);
    		}else {
	    		ColorsOut.outappl("Qak22Context | EXISTS proxy for " + host + " since:" + actorName, ColorsOut.MAGENTA);  			
    		}
    		proxyMap.put(actorName, pxy);
    }
    

	public static void addActor(QakActor22 a) {	
        ctxMap.put(a.getName(), a);
    }
	public static void removeActor(QakActor22 a) {	
        ctxMap.remove( a.getName() );
    }	
    public static QakActor22 getActor(String actorName) {
        return ctxMap.get(actorName);
    }
    
//proxy
 
	public static ProxyAsClient getProxy(String actorName) {
        return proxyMap.get(actorName);
    }

	
	
//Annotations
	
    public static void handleLocalActorDecl(Object element) {   	
        Class<?> clazz            = element.getClass();
        Annotation[] annotations  = clazz.getAnnotations();
         for (Annotation annot : annotations) {
        	 if (annot instanceof ActorLocal) {
        		 ActorLocal a = (ActorLocal) annot;
        		 for( int i=0; i<a.name().length; i++) {
        			 String name     = a.name()[i];
        			 Class  impl     = a.implement()[i];
            		 try {
						impl.getConstructor( String.class ).newInstance( name );
	            		 ColorsOut.outappl( "Qak22Context | CREATED LOCAL ACTOR: "+ name, ColorsOut.MAGENTA );
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
 						e.printStackTrace();
					}
         		 }
        	 }
         }
    }
    public static void handleRemoteActorDecl(Object element) {
        Class<?> clazz            = element.getClass();
        Annotation[] annotations  = clazz.getAnnotations();
        for (Annotation annot : annotations) {
        	 if (annot instanceof ActorRemote) {
        		 ActorRemote a = (ActorRemote) annot;
        		 for( int i=0; i<a.name().length;i++) {
        			 String name     = a.name()[i];
        			 String host     = a.host()[i];
        			 String port     = a.port()[i];
        			 String protocol = a.protocol()[i];        			 
        			 Qak22Context.setActorAsRemote(name, port, host, ProtocolInfo.getProtocol(protocol));
//            		 ColorsOut.outappl(
//            				 "Qak22Context | CREATE REMOTE ACTOR PROXY:"+ name + " host:" + host + " port:"+port
//            						 + " protocol:" + protocol, ColorsOut.MAGENTA);        			 
        		 }
        	 }
        }
    }
    public static void handleActorDeclaration(Object element) {
    	handleLocalActorDecl(element);
    	handleRemoteActorDecl(element);
   }  
    
 

   
//Events
    
    public static final String registerForEvent   = "registerForEvent";
    public static final String unregisterForEvent = "unregisterForEvent";
    
	
	public static void registerAsEventObserver(String observer, String evId) {
		QakActor22 a = getActor(EventMsgHandler.myName);
		if( a == null ) new EventMsgHandler();
		IApplMessage m =
				CommUtils.buildDispatch(observer, registerForEvent, evId, EventMsgHandler.myName);
		ColorsOut.outappl( "Qak22Context | registerAsEventObserver m="+ m, ColorsOut.MAGENTA );
		Qak22Util.sendAMsg( m, EventMsgHandler.myName );  //Redirection to store
	}
	
	public static void unregisterAsEventObserver(String observer, String evId) {
		IApplMessage m =
				CommUtils.buildDispatch(observer, unregisterForEvent, evId, EventMsgHandler.myName);
		Qak22Util.sendAMsg( m, EventMsgHandler.myName );  //Redirection to store
	}
	

    
}
