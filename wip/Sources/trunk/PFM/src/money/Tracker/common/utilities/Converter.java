package money.Tracker.common.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
public class Converter {
	public static Date toDate(String date)
	{
	    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	    Date parsed = new Date();
	    try
	    {
	        parsed = inputFormat.parse(date);
	    }
	    catch (ParseException e)
	    {
	        // TODO Auto-generated catch block
	    }

	    return parsed;
	}
}
