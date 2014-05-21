package net.jomak.scheduleview.utils;

import android.content.Context;

public class ViewUtils {
	public static int dpToPx(Context context, int dps){
		final float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (dps * scale + 0.5f);
		return pixels;
	}
}
