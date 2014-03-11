package net.jomak.scheduler.scheduleview;

public class Edge implements Comparable<Edge>{
	final static int START = 1;
	final static int END = -1;
	/**
	 * delta = 0 means it has not been initialized
	 * delta = 1 means it is a starting point
	 * delta = -1 means it is an ending point
	 */
	private Integer delta = 0; 
	private Long timeInMillis = 0L;
	private int originalIndex = -1;
	public Edge(Integer delta, Long timeInMillis, int originalIndex){
		this.delta = delta;
		this.timeInMillis = timeInMillis;
		this.originalIndex = originalIndex;
	}
	
	public Integer getDelta(){
		return delta;
	}
	public void setDelta(Integer delta){
		this.delta = delta;
	}
	public Long getTimeInMillis(){
		return timeInMillis;
	}
	public void setTimeInMillis(Long timeInMillis){
		this.timeInMillis = timeInMillis;
	}
	public int getOriginalIndex(){
		return originalIndex;
	}
	public void setOriginalIndex(int originalIndex){
		this.originalIndex = originalIndex;
	}
	@Override
	public int compareTo(Edge another) {
		int compareTimes = timeInMillis.compareTo(another.getTimeInMillis());
		if (compareTimes == 0){
			// If the times are equal, compare the delta values
			return delta.compareTo(another.getDelta());
		}
		return compareTimes;
	}
	
	
}
