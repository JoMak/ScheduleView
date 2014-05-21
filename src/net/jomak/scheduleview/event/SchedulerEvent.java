package net.jomak.scheduleview.event;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class SchedulerEvent {
	private DateTime startTime;
	private DateTime endTime;
	private Duration duration;

	/**
	 * Compressed times are used to compress event spans in one day. Ex. 9 pm -
	 * 10 am (next day) compressed to 9 pm - 11:59 pm for day 1 and 12:00 am -
	 * 10 am for day 2
	 */
	private DateTime compressedStartTime;
	private DateTime compressedEndTime;
	private int overlapWidth = 1;
	private int overlapIndex = 0;

	public SchedulerEvent(DateTime startTime, DateTime endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		this.duration = new Duration(startTime, endTime);
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
		this.duration = new Duration(startTime, endTime);
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
		this.duration = new Duration(startTime, endTime);
	}

	public Duration getDuration() {
		return duration;
	}

	public DateTime getCompressedStartTime() {
		return compressedStartTime;
	}

	public void setCompressedStartTime(DateTime compressedStartTime) {
		this.compressedStartTime = compressedStartTime;
	}

	public DateTime getCompressedEndTime() {
		return compressedEndTime;
	}

	public void setCompressedEndTime(DateTime compressedEndTime) {
		this.compressedEndTime = compressedEndTime;
	}

	public int getOverlapWidth() {
		return overlapWidth;
	}

	public void setOverlapWidth(int overlapWidth) {
		this.overlapWidth = overlapWidth;
	}

	public int getOverlapIndex() {
		return overlapIndex;
	}

	public void setOverlapIndex(int overlapIndex) {
		this.overlapIndex = overlapIndex;
	}
}
