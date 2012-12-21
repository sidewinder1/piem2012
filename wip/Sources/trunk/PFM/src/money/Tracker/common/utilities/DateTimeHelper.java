package money.Tracker.common.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import android.util.Log;

public class DateTimeHelper {
	static Calendar calendar;

	public static Date getLastDateOfMonth(int year, int month) {
		calendar = new GregorianCalendar(year, month, Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getDate(int year, int month, int day) {
		return new Date(year - 1900, month, day);
	}

	public static Date addHours(Date currentDate, int hour){
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		calendar.setTime(currentDate);
		calendar.add(Calendar.HOUR, hour);
		return calendar.getTime();
	}
	
	public static Date now(boolean checkTimeZone) {
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		
		if (checkTimeZone) {
			System.currentTimeMillis();
			calendar.set(1970, 0, 1,0,0,0);
			calendar.add(Calendar.SECOND, (int) (System.currentTimeMillis()/1000));
		}
		return calendar.getTime();
	}

	public static long nowInMillis() {
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		return calendar.getTimeInMillis();
	}

	public static int getWeekInMonth(Date date) {
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
	}

	public static int getDayOfMonth(int year, int month) {
		calendar = new GregorianCalendar(year, month, Calendar.DAY_OF_MONTH);

		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static Date getLastDayOfWeek(Date date) {
		calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		calendar.setTime(date);
		// Hot fix with current day is sunday.
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			return calendar.getTime();
		}
		
		calendar.add(Calendar.DATE, 8 - calendar.get(Calendar.DAY_OF_WEEK));
		return calendar.getTime();
	}
	
	public static Date getFirstDayOfWeek(Date date) {
		calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
		
		calendar.setTime(date);
		
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
			calendar.add(Calendar.DATE, calendar.get(Calendar.DAY_OF_WEEK) - 8);
			calendar.setTime(calendar.getTime());
		}
		/*
		// Hot fix with current day is monday.
		if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
			return calendar.getTime();
		}
		
		Log.d("Check get first date of week", "" + calendar.get(Calendar.DAY_OF_WEEK));
		calendar.add(Calendar.DATE, calendar.get(Calendar.DAY_OF_WEEK) - 4);
		*/
		Log.d("Check week of year", "" + calendar.get(Calendar.WEEK_OF_YEAR));
		calendar.set(Calendar.WEEK_OF_YEAR, calendar.get(Calendar.WEEK_OF_YEAR));        
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

		return calendar.getTime();
	}
}
