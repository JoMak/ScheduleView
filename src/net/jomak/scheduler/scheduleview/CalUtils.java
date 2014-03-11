package net.jomak.scheduler.scheduleview;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalUtils {
	/**
	 * Calculates the difference in days, rounding the operands down to the nearest day.
	 * Ex. Jan 3rd 12:31 pm - Jan 2nd 4:00 pm
	 * =   Jan 3rd - Jan 2nd
	 * =   1 day 
	 * @return The difference in days.
	 */
	public static int differenceInDays(GregorianCalendar minuend, GregorianCalendar subtrahend){
		GregorianCalendar minuendFloor = new GregorianCalendar(minuend.get(Calendar.YEAR), minuend.get(Calendar.MONTH), minuend.get(Calendar.DAY_OF_MONTH));
		GregorianCalendar subtrahendFloor = new GregorianCalendar(subtrahend.get(Calendar.YEAR), subtrahend.get(Calendar.MONTH), subtrahend.get(Calendar.DAY_OF_MONTH));
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(minuendFloor.getTimeInMillis()-subtrahendFloor.getTimeInMillis());
		return result.get(Calendar.DAY_OF_YEAR)-1;
	}
}
