package it.unibo.radarSystem22_4.comm.interfaces;

public interface IContext {
	public void addComponent( String name, IApplMsgHandler h);
	public void removeComponent( String name );
	public void activate();
	public void deactivate();
}
