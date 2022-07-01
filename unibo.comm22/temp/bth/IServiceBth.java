package it.unibo.platform.bth;

import javax.microedition.io.StreamConnection;
import it.unibo.is.interfaces.IOutputView;
import it.unibo.is.interfaces.protocols.IBthInteraction;

public interface IServiceBth {	
	public IBthInteraction getBthConnSupport( String logo, StreamConnection conn, IOutputView view );
	public IBthConnection getBthSupport(String logo, IOutputView view);
}
