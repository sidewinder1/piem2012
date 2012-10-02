package money.Tracker.common.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.text.format.DateFormat;
import android.util.Log;

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
		SimpleDateFormat inputFormat = new SimpleDateFormat(
				inputFormatString);
		Log.d("Date Format", inputFormatString);
		Date parsed = new Date();
		try {
			parsed = inputFormat.parse(date);
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		
			
		
		Log.d("Date Format", String.valueOf(parsed));

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
	
	public static Date convertDate(Date dateConvert)
	{
		String formatDate = "MMM dd, yyyy";
		
		DateFormat df = new DateFormat();
		
		return (Date) df.format(formatDate, dateConvert); 
	}
	
	public static Date convertDate(String dateConvert)
	{
		SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
		Date dateObj = new Date();
		try {
			 dateObj = curFormater.parse(dateConvert);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return dateObj;
	}
}
