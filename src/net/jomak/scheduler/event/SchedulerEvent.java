package net.jomak.scheduler.event;

import java.util.GregorianCalendar;

public class SchedulerEvent {
	private GregorianCalendar startTime;
	private GregorianCalendar endTime;
	private GregorianCalendar duration;
	
	/**
	 * Compressed times are used to compress event spans in one day.
	 * Ex. 9 pm - 10 am (next day) compressed to 9 pm - 11:59 pm for day 1
	 * and 12:00 am - 10 am for day 2
	 */
	private GregorianCalendar compressedStartTime;
	private GregorianCalendar compressedEndTime;
	private int overlapWidth = 1;
	
	public SchedulerEvent(GregorianCalendar startTime, GregorianCalendar endTime, GregorianCalendar duration) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = duration;
	}

	public GregorianCalendar getStartTime() {
		return startTime;
	}

	public void setStartTime(GregorianCalendar startTime) {
		this.startTime = startTime;
	}

	public GregorianCalendar getEndTime() {
		return endTime;
	}

	public void setEndTime(GregorianCalendar endTime) {
		this.endTime = endTime;
	}

	public GregorianCalendar getDuration() {
		return duration;
	}

	public void setDuration(GregorianCalendar duration) {
		this.duration = duration;
	}

	public GregorianCalendar getCompressedStartTime() {
		return compressedStartTime;
	}

	public void setCompressedStartTime(GregorianCalendar compressedStartTime) {
		this.compressedStartTime = compressedStartTime;
	}

	public GregorianCalendar getCompressedEndTime() {
		return compressedEndTime;
	}

	public void setCompressedEndTime(GregorianCalendar compressedEndTime) {
		this.compressedEndTime = compressedEndTime;
	}
	
	public int getOverlapWidth() {
		return overlapWidth;
	}

	public void setOverlapWidth(int overlapWidth) {
		this.overlapWidth = overlapWidth;
	}
}
