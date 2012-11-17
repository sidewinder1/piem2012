package money.Tracker.common.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

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

	public static Date now() {
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
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
		calendar = Calendar.getInstance(TimeZone.getDefault(),
				Locale.getDefault());
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 8 - calendar.get(Calendar.DAY_OF_WEEK));
		return calendar.getTime();
	}
}
