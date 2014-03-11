package net.jomak.scheduler.scheduleview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ScheduleView extends LinearLayout{
	private final ScheduleViewController mScheduleViewController;
	public ScheduleView(Context context) {
		super(context);
		mScheduleViewController = new ScheduleViewController(getContext(), this);
	}
	public ScheduleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScheduleViewController = new ScheduleViewController(getContext(), this);
	}
	public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScheduleViewController = new ScheduleViewController(getContext(), this);
	}
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		mScheduleViewController.onDraw(canvas);
	}
}
