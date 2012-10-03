package money.Tracker.common.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTimeHelper {
	public static Date getLastDateOfMonth(int year, int month) {
		Calendar calendar = new GregorianCalendar(year, month,
				Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getDate(int year, int month, int day)
	{
		return new Date(year - 1900, month, day);
	}
	
	public static Date getLastDayOfWeek(Date date) {
		Calendar calendar = new GregorianCalendar(date.getYear(), date.getMonth(),
				Calendar.DAY_OF_MONTH);
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_WEEK, 1);
		calendar.add(Calendar.DAY_OF_WEEK, 7);
		return calendar.getTime();
	}
}
