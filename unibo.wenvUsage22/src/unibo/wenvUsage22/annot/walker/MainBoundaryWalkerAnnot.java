/*
ClientUsingHttp.java
*/
package unibo.wenvUsage22.annot.walker;
import unibo.actor22comm.utils.CommUtils;
import unibo.actor22.Qak22Context;
import unibo.actor22.annotations.Actor22;
import unibo.actor22.annotations.Context22;
import unibo.wenvUsage22.common.ApplData;

@Context22(name = "pcCtx", host = "localhost", port = "8083")
@Actor22(name = ApplData.robotName,contextName="pcCtx",implement = BoundaryWalkerAnnot.class)
public class MainBoundaryWalkerAnnot { 
 	

	protected void configure() throws Exception {
		//new BoundaryWalkerAnnot(ApplData.robotName);
		//AnnotUtil.handleRepeatableActorDeclaration(this);
		Qak22Context.configureTheSystem(this);
		CommUtils.delay(1500);  //Give time to start ...
		Qak22Context.showActorNames();
 	}
 
 /*
MAIN
 */
	public static void main(String[] args) throws Exception   {
		CommUtils.aboutThreads("Before start - ");
 		new MainBoundaryWalkerAnnot().configure();
 		//CommUtils.delay(1000);
 		CommUtils.aboutThreads("At end - ");
	}
	
 
	
 }
