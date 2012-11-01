package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import android.test.AndroidTestCase;

public class ConverterTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDateToString() {
		Assert.assertEquals("1990-11-20 12:00:00",
				Converter.toString(DateTimeHelper.getDate(1990, 10, 20)));
	}

	public void testDateToStringWithFormat() {
		Assert.assertEquals("11/20-1990", Converter.toString(
				DateTimeHelper.getDate(1990, 10, 20), "MM/dd-yyyy"));
	}

	public void testDoubleToString() {
		Assert.assertEquals("1200,00", Converter.toString(1200));
	}

	public void testDoubleToStringWithFormat() {
		Assert.assertEquals("1200,0000", Converter.toString(1200, "##0.0000"));
	}
	
	public void testStringToDate(){
		Assert.assertEquals(DateTimeHelper.getDate(2011, 9, 12), Converter.toDate("2011-10-12 12:00:00"));
	}
	
	public void testStringToDateWithFormat(){
		Assert.assertEquals(DateTimeHelper.getDate(2011, 9, 12), Converter.toDate("2011-10/12", "yyyy-MM/dd"));
	}
}
