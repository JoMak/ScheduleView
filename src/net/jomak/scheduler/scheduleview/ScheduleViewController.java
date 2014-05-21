package net.jomak.scheduler.scheduleview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import net.jomak.scheduler.R;
import net.jomak.scheduleview.event.SchedulerEvent;
import net.jomak.scheduleview.utils.CalUtils;
import net.jomak.scheduleview.utils.ViewUtils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Duration;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleViewController {
	/**
	 * Tag used for logging.
	 */
	private static final String TAG = "ScheduleViewController";
	/**
	 * Keeps track of whether this was initialized. Throws an
	 * IllegalStateException if it is used before it is initialized.
	 */
	private boolean mInitialized = false;

	private final Context mContext;
	private final ScheduleView mScheduleView;

	private LayoutInflater mLayoutInflater;
	private Vector<SchedulerEvent> mEventList;
	private int mDaysInView = 7;
	private int mHoursInView = 24;

	// Measurements
	private int mCellHeight;
	private int mCellWidth;
	private int mEventDrawAreaWidth;
	private int mEventDrawAreaHeight;
	private int mDayLabelHeight;
	private int mHourLabelWidth;

	// Subviews
	private LinearLayout mHoursLinearLayout;
	private LinearLayout mDaysLinearLayout;
	private TableLayout mCellTableLayout;
	private RelativeLayout mEventDrawAreaRelativeLayout;

	// The number of days
	private SchedulerEvent mTotalTimeSpan;

	/**
	 * Base Constructor
	 */
	public ScheduleViewController(Context context, ScheduleView scheduleView) {
		mContext = context;
		mScheduleView = scheduleView;
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutInflater.inflate(R.layout.scheduleview_base, mScheduleView);
		initialize();
	}

	private void initialize() {
		mHoursLinearLayout = (LinearLayout) mScheduleView.findViewById(R.id.hour_labels);
		mDaysLinearLayout = (LinearLayout) mScheduleView.findViewById(R.id.day_labels);
		mCellTableLayout = (TableLayout) mScheduleView.findViewById(R.id.cell_table);
		mEventDrawAreaRelativeLayout = (RelativeLayout) mScheduleView
				.findViewById(R.id.event_draw_area);
		mInitialized = true;

	}

	public void draw() {
		if (!mInitialized) {
			initialize();
			Log.i(TAG,
					"ScheduleView has not been initialized - using default views. To customize, call ScheduleView.getController().initialize()");
		}
		measureSizesAndDraw();
	}

	/**
	 * Draw the base layout (everything but the event boxes) ie. Labels, grid,
	 * etc...
	 */
	private void drawBaseLayout() {
		// Draw the hour labels
		for (int i = 0; i < mHoursInView; i++) {
			TextView hourLabel = new TextView(mContext);
			int hour = (i % 12) == 0 ? 12 : (i % 12);
			hourLabel.setText(String.format("%d %s", hour, i < 12 ? "AM" : "PM"));
			hourLabel.setHeight(mCellHeight);
			mHoursLinearLayout.addView(hourLabel);
		}
		// Draw the day labels
		for (int i = 0; i <= mDaysInView; i++) {
			TextView dayLabel = new TextView(mContext);
			if (i == 0) {
				dayLabel.setWidth(mHourLabelWidth);
			} else {
				dayLabel.setWidth(mCellWidth);
				dayLabel.setText(CalUtils.DAYS_OF_WEEK[i - 1]);
			}
			mDaysLinearLayout.addView(dayLabel);
		}
		// Lay out the grid
		for (int i = 0; i < mHoursInView; i++) {
			TableRow newRow = new TableRow(mContext);
			for (int j = 0; j < mDaysInView; j++) {
				TextView cellView = new TextView(mContext);
				cellView.setHeight(mCellHeight);
				cellView.setWidth(mCellWidth);
				newRow.addView(cellView);
			}
			mCellTableLayout.addView(newRow);
		}
		android.widget.RelativeLayout.LayoutParams eventDrawAreaLayoutParams = (android.widget.RelativeLayout.LayoutParams) mEventDrawAreaRelativeLayout
				.getLayoutParams();
		eventDrawAreaLayoutParams.width = mEventDrawAreaWidth;
		eventDrawAreaLayoutParams.height = mEventDrawAreaHeight;
	}

	/**
	 * Remove all event boxes and redraw them all.
	 */
	public void redrawAllEvents(boolean recompute) {
		if (recompute) {
			if (mComputationThread.isAlive()) {
				mComputationThread.interrupt();
			}
			mComputationThread.start();
		} else {
			Log.i(TAG, "Post redraw to message queue.");
			mEventDrawAreaRelativeLayout.removeAllViews();
			drawEvents();
			mScheduleView.invalidate();
			mScheduleView.requestLayout();
		}
	}

	/**
	 * The thread that computes the overlapping edges
	 */
	private final Thread mComputationThread = new Thread(new Runnable() {
		@Override
		public void run() {
			if (computeEventWidth(mEventList)) {
				Log.i(TAG, "Computation finished.");
				// The thread successfully finished without being interrupted.
				redrawAllEvents(false);
			} else {
				Log.i(TAG, "Computation was interrupted.");
			}
		}

		private boolean computeEventWidth(List<SchedulerEvent> events) {
			ArrayList<Edge> masterEdges = new ArrayList<Edge>();
			for (int i = 0; i < events.size(); i++) {
				// TODO: Better threading
				if (Thread.currentThread().isInterrupted()) {
					return false;
				}
				addEdgesFromSchedulerEvent(events.get(i), masterEdges, i);
			}
			Collections.sort(masterEdges);
			int runningCount = 0;
			int maxRunningConcurrent = 1;
			HashSet<Integer> runningEventsIndices = new HashSet<Integer>();
			Stack<Integer> indexProvider = new Stack<Integer>();
			int nextAvailableIndex = 0;
			for (Edge edge : masterEdges) {
				// TODO: Better threading
				if (Thread.currentThread().isInterrupted()) {
					return false;
				}
				runningCount += edge.getDelta();
				if (runningEventsIndices.add(edge.getOriginalIndex())) {
					// Set index at the "START" edge
					events.get(edge.getOriginalIndex()).setOverlapIndex(
							indexProvider.size() == 0 ? nextAvailableIndex++ : indexProvider.pop());
				} else {
					// Return index at the "END" edge
					indexProvider.push(events.get(edge.getOriginalIndex()).getOverlapIndex());
				}
				maxRunningConcurrent = Math.max(runningCount, maxRunningConcurrent);
				if (runningCount == 0) {
					for (Integer i : runningEventsIndices) {
						events.get(i).setOverlapWidth(maxRunningConcurrent);
					}
					// Reset for next "group" of events
					runningEventsIndices.clear();
					maxRunningConcurrent = 0;
					runningCount = 0;
					indexProvider.clear();
					nextAvailableIndex = 0;

				}
			}
			return true;
		}
	});

	private void addEdgesFromSchedulerEvent(SchedulerEvent schedulerEvent, List<Edge> dest,
			int originalIndex) {
		dest.add(new Edge(Edge.START, schedulerEvent.getStartTime().getMillis(), originalIndex));
		dest.add(new Edge(Edge.END, schedulerEvent.getEndTime().getMillis(), originalIndex));
	}

	private void measureSizesAndDraw() {
		mScheduleView.post(new Runnable() {
			public void run() {
				mHourLabelWidth = mHoursLinearLayout.getWidth();
				mDayLabelHeight = mDaysLinearLayout.getHeight();
				mEventDrawAreaWidth = mScheduleView.getWidth() - mHourLabelWidth;
				mCellWidth = mEventDrawAreaWidth / mDaysInView;
				mCellHeight = ViewUtils.dpToPx(mContext, 60);
				mEventDrawAreaHeight = mCellHeight * mHoursInView;
				Log.i(TAG, "Hour Label Width: " + mHourLabelWidth);
				Log.i(TAG, "Day Label Height: " + mDayLabelHeight);
				Log.i(TAG, "Event Draw Area Width: " + mEventDrawAreaWidth);
				Log.i(TAG, "Event Draw Area Height: " + mEventDrawAreaHeight);
				Log.i(TAG, "Cell Width: " + mCellWidth);
				Log.i(TAG, "Cell Height: " + mCellHeight);
				drawBaseLayout();
				redrawAllEvents(false);
			}
		});
	}

	/**
	 * Draw the event boxes.
	 */
	private void drawEvents() {
		mEventDrawAreaRelativeLayout.post(new Runnable() {
			@Override
			public void run() {
				if (mEventList == null) {
					Log.i(TAG,
							"Event list is null, did you forget to call ScheduleViewController.setEventList()?");
					return;
				} else if (mTotalTimeSpan == null) {
					Log.i(TAG,
							"Total time span is null, did you forget to call ScheduleViewController.setEventList()?");
					return;
				}
				Log.i(TAG, "Start adding event boxes.");
				for (int i = 0; i < mEventList.size(); i++) {
					LinearLayout child = makeEventBox(mEventList.get(i), mCellWidth, mCellHeight);
					mEventDrawAreaRelativeLayout.addView(child);
				}
				Log.i(TAG,
						String.format("%d events added.",
								mEventDrawAreaRelativeLayout.getChildCount()));
			}
		});
	}

	private LinearLayout makeEventBox(final SchedulerEvent schedulerEvent, int cellWidth,
			int cellHeight) {
		DateTime startTime = schedulerEvent.getStartTime();
		Duration durationTime = schedulerEvent.getDuration();

		LinearLayout eventBoxLinearLayout = new LinearLayout(mContext);

		int left = Days.daysBetween(mTotalTimeSpan.getStartTime().toLocalDate(),
				startTime.toLocalDate()).getDays()
				* cellWidth;
		int top = (int) ((startTime.getHourOfDay() + startTime.getMinuteOfHour() / 60.0) * cellHeight);
		int width = cellWidth / schedulerEvent.getOverlapWidth();
		int height = (int) (durationTime.getStandardMinutes() / 60.0 * cellHeight);

		// Cascade overlapping boxes
		left += width * schedulerEvent.getOverlapIndex();

		RelativeLayout.LayoutParams eventBoxLinearLayoutParams = new RelativeLayout.LayoutParams(
				width, height);
		eventBoxLinearLayoutParams.leftMargin = left;
		eventBoxLinearLayoutParams.topMargin = top;
		eventBoxLinearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		eventBoxLinearLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		eventBoxLinearLayout.setLayoutParams(eventBoxLinearLayoutParams);

		eventBoxLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(
						mContext,
						String.format(
								"Overlap Width: %d, Ovelap Index: %d, Start time: %d:%2d, End time: %d:%2d",
								schedulerEvent.getOverlapWidth(), schedulerEvent.getOverlapIndex(),
								schedulerEvent.getStartTime().getHourOfDay(), schedulerEvent
										.getStartTime().getMinuteOfHour(), schedulerEvent
										.getEndTime().getHourOfDay(), schedulerEvent.getEndTime()
										.getMinuteOfHour()), Toast.LENGTH_SHORT).show();

			}
		});
		Log.i(TAG, String.format("Left: %s; Top: %s; Right: %s; Bottom: %s", left, top, left
				+ width, top + height));
		eventBoxLinearLayout.setBackgroundColor(Color.BLUE);
		return eventBoxLinearLayout;
	}

	// ----- Accessor Methods -----//
	/**
	 * The dataset corresponding to the current scheduleview.
	 * 
	 * @param eventList
	 */
	public void setEventList(Vector<SchedulerEvent> eventList, SchedulerEvent totalTimeSpan) {
		if (mComputationThread.isAlive()) {
			mComputationThread.interrupt();
		}
		mEventList = eventList;
		mTotalTimeSpan = totalTimeSpan;
	}

	public Vector<SchedulerEvent> getEventList() {
		return mEventList;
	}
}
