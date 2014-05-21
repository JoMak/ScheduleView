package net.jomak.scheduler.scheduleview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

public class ScheduleView extends LinearLayout {
	private final ScheduleViewController mScheduleViewController;

	public ScheduleView(Context context) {
		this(context, null, 0);
	}

	public ScheduleView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScheduleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOrientation(LinearLayout.VERTICAL);
		Log.i("ScheduleView", "Constructor called.");
		mScheduleViewController = new ScheduleViewController(getContext(), this);
		mScheduleViewController.draw();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.i("ScheduleView", "onDraw called.");
	}

	public ScheduleViewController getController() {
		return mScheduleViewController;
	}
}
