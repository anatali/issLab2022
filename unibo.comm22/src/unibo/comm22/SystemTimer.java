package unibo.comm22;

public class SystemTimer { //extends Thread
	
	private long startTime;
	private long endTime;
	private long duration;
	
	public void startTime() {
		startTime = System.currentTimeMillis();
	}
	public void stopTime() {
		endTime = System.currentTimeMillis();
		duration = endTime - startTime ;
	}
	
	public long getDuration() {
		return duration;
	}
 
}
