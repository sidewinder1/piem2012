package money.Tracker.common.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Converter {
	private static final String dateFormatString = "yyyy-MM-dd hh:mm:ss";

	public static String toString(Date date, String format) {
		return String.valueOf(android.text.format.DateFormat.format(format,
				date));
	}

	public static String toString(Date date) {
		return String.valueOf(android.text.format.DateFormat.format(
				dateFormatString, date));
	}

	public static Date toDate(String date, String inputFormatString) {
		java.text.DateFormat inputFormat = new SimpleDateFormat(
				inputFormatString);
		Date parsed = new Date();
		try {
			parsed = inputFormat.parse(date);
		} catch (ParseException e) {
			// TODO Add log
		}

		return parsed;
	}
	
	public static Date toDate(String date) {
		java.text.DateFormat inputFormat = new SimpleDateFormat(
				dateFormatString);
		Date parsed = new Date();
		try {
			parsed = inputFormat.parse(date);
		} catch (Exception e) {
			// TODO Add log
			
		}

		return parsed;
	}
}