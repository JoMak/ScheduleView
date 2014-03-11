package net.jomak.scheduler.scheduleview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import net.jomak.scheduler.event.SchedulerEvent;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
		mScheduleViewController.onDraw(canvas);
		super.onDraw(canvas);
	}
}
