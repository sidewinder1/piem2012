package money.Tracker.common.utilities;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.graphics.Color;

public class Converter {
	private static final String dateFormatString = "yyyy-MM-dd kk:mm:ss";

	public static String toString(Date date, String format) {
		return String.valueOf(android.text.format.DateFormat.format(format,
				date));
	}

	public static String toString(double value, String format) {
		return new DecimalFormat(format).format(value);
	}
	
	public static double toDouble(String value) {
		try{
			String testComma = toString(1000, "#,##0");
			if (testComma.contains(","))
			{
				value = value.replace(",", "");
			}
			else
			{
				value = value.replace(".", "").replace(",", ".");
			}
			
			return Double.parseDouble(value);
		}
		catch(Exception e)
		{
			return 0.0;
		}
	}

	public static long toLong(String value) {
		try{
			String testComma = toString(1000, "#,##0");
			if (testComma.contains(","))
			{
				value = value.replace(",", "").split("[.]")[0];
			}
			else
			{
				value = value.replace(".", "").split("[,]")[0];
			}
			
			return Long.parseLong(value);
		}
		catch(Exception e)
		{
			return 0;
		}
	}

	
	public static String toString(double value) {
		return toString(value, "#,##0");
	}

	public static String toString(long value) {
		return toString(value, "#,##0");
	}
	
	public static String toString(Color color) {
		return "#99000000";
	}

	public static String toString(Date date) {
		return String.valueOf(android.text.format.DateFormat.format(
				dateFormatString, date));
	}

	public static Date toDate(String date, String inputFormatString) {
		SimpleDateFormat inputFormat = new SimpleDateFormat(inputFormatString);
		Date parsed = new Date();
		try {
			parsed = inputFormat.parse(date);

		} catch (ParseException e) {
			// TODO Auto-generated catch block
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
