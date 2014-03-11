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
import android.widget.LinearLayout.LayoutParams;

public class ScheduleViewController {
	private final Context mContext;
	private final ScheduleView mScheduleView
	
	private LayoutInflater mLayoutInflater;
	private Vector<SchedulerEvent> eventList;
	private int xNum = 7;
	private int yNum = 24;
	private LayoutParams layoutParams;
	// Measurements
	private int cellHeight;
	private int cellWidth;
	private int eventDrawAreaWidth;
	private int eventDrawAreaHeight;
	
	// View Ids
	private int cellViewId;
	private int xLabelsId;
	private int yLabelsId;
	// Views 
	private View cellView;
	private View xLabels;
	private View yLabels;
	
	//Subviews
	private LinearLayout bottomHoursLinearLayout;
	private LinearLayout cellGridLinearLayout;
	private RelativeLayout eventDrawAreaRelativeLayout;
	
	/**
	 * Base Constructor
	 */
	public ScheduleViewController(Context context, ScheduleView scheduleView){
		mContext = context;
		mScheduleView = scheduleView;
	}

	public void initialize(int cellViewId, int xLabelsId, int yLabelsId, int xNum, int yNum){
		this.cellViewId = cellViewId;
		this.xLabelsId = xLabelsId;
		this.yLabelsId = yLabelsId;
		this.xNum = xNum;
		this.yNum = yNum;
		mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutParams = (LayoutParams) mScheduleView.getLayoutParams();
		cellView = mLayoutInflater.inflate(cellViewId, null);
		xLabels = mLayoutInflater.inflate(xLabelsId, null);
		yLabels = mLayoutInflater.inflate(yLabelsId, null);
		mScheduleView.setOrientation(LinearLayout.VERTICAL);
		bottomHoursLinearLayout =  new LinearLayout(mContext);
		bottomHoursLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		cellGridLinearLayout = new LinearLayout(mContext);
		cellGridLinearLayout.setOrientation(LinearLayout.VERTICAL);
		eventDrawAreaRelativeLayout = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams eventDrawAreaRelativeLayoutParams = ((RelativeLayout.LayoutParams)eventDrawAreaRelativeLayout.getLayoutParams());
		eventDrawAreaRelativeLayoutParams.addRule(RelativeLayout.BELOW, xLabelsId);
		eventDrawAreaRelativeLayoutParams.addRule(RelativeLayout.RIGHT_OF, yLabelsId);
	}
	
	protected void onDraw(Canvas canvas) {
		measureSizes();
		// Add the basic layout (everything but the event boxes)
		mScheduleView.addView(xLabels);	// add the top bar (if applicable)
		// Lay out the grid
		for(int i=0; i<yNum; i++){
			LinearLayout newRow = new LinearLayout(mContext);
			newRow.setOrientation(LinearLayout.HORIZONTAL);
			for (int j=0; j<xNum; j++){
				newRow.addView(cellView);
			}
			cellGridLinearLayout.addView(newRow);
		}
		bottomHoursLinearLayout.addView(yLabels);	
		bottomHoursLinearLayout.addView(cellGridLinearLayout);
		mScheduleView.addView(bottomHoursLinearLayout);	// add the bottom half
		drawEvents(canvas);
	}
	private final Thread ComputationThread = new Thread(new Runnable(){
		@Override
		public void run(){
			computeEventWidth(eventList);
		}
		private boolean computeEventWidth(List<SchedulerEvent> events){
			ArrayList<Edge> masterEdges = new ArrayList<Edge>();
			for (int i=0;i<events.size();i++){
				// TODO: Better threading
				if (Thread.currentThread().isInterrupted()){
					return false; 
				}
				addEdgesFromSchedulerEvent(events.get(i), masterEdges, i);
			}
			Collections.sort(masterEdges);
			int runningCount = 0;
			int maxRunningConcurrent = 0;
			HashSet<Integer> runningEventsIndices = new HashSet<Integer>();
			for (Edge edge:masterEdges){
				// TODO: Better threading
				if (Thread.currentThread().isInterrupted()){
					return false; 
				}
				runningCount+=edge.getDelta();
				runningEventsIndices.add(edge.getOriginalIndex());
				maxRunningConcurrent = Math.max(runningCount, maxRunningConcurrent);
				if (runningCount == 0){
					for (Integer i:runningEventsIndices){
						events.get(i).setOverlapWidth(maxRunningConcurrent);
					}
					// Reset for next "group" of events
					runningEventsIndices.clear();
					maxRunningConcurrent = 0;
					runningCount = 0;
				}
			}
			return true;
		}
	});
	private void addEdgesFromSchedulerEvent(SchedulerEvent schedulerEvent, List<Edge> dest, int originalIndex){
		dest.add(new Edge(Edge.START,schedulerEvent.getStartTime().getTimeInMillis(), originalIndex));
		dest.add(new Edge(Edge.END, schedulerEvent.getEndTime().getTimeInMillis(), originalIndex));
	}
	private void measureSizes(){
		post(new Runnable() {
			public void run() {
				eventDrawAreaWidth = mScheduleView.getWidth()-yLabels.getWidth();
				eventDrawAreaHeight = mScheduleView.getHeight()-xLabels.getHeight();
				cellWidth = eventDrawAreaWidth/xNum;
				cellHeight = eventDrawAreaHeight/yNum;
			}
		});
	}
	/**
	 * Draw the event boxes.
	 */
	private void drawEvents(Canvas canvas){
		for (int i=0;i<eventList.size();i++){
			eventDrawAreaRelativeLayout.addView(makeEventBox(eventList.get(i), ));
		}
	}
	private LinearLayout makeEventBox(SchedulerEvent schedulerEvent, int maxWidth, int maxHeight){
		LinearLayout eventBoxLinearLayout = new LinearLayout(mContext());
		LinearLayout.LayoutParams eventBoxLinearLayoutParams = (LayoutParams) eventBoxLinearLayout.getLayoutParams();
		eventBoxLinearLayoutParams.setMargins(, top, right, bottom);
		return eventBoxLinearLayout;
	}
	// TODO: Better threading
	private void updateData(){
		if(ComputationThread.isAlive()){
			ComputationThread.interrupt();
		}
		ComputationThread.start();
	}
	@Override
	public void invalidate(){
		updateData();
		super.invalidate();
	}
	public void setEventList(Vector<SchedulerEvent> eventList){
		this.eventList = eventList;
	}
}