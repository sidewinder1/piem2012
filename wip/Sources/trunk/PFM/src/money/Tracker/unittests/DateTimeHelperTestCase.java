package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.utilities.DateTimeHelper;
import android.test.AndroidTestCase;

public class DateTimeHelperTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetDate() {
		Assert.assertEquals(20,
				DateTimeHelper.getDate(1990, 10, 20).getDate());
		
		Assert.assertEquals(1990-1900,
				DateTimeHelper.getDate(1990, 10, 20).getYear());
		Assert.assertEquals(10,
				DateTimeHelper.getDate(1990, 10, 20).getMonth());
	}

	public void testGetLastDateOfMonth() {
		Assert.assertEquals(31,
			DateTimeHelper.getLastDateOfMonth(2012, 2).getDate());
		Assert.assertEquals(2,
				DateTimeHelper.getLastDateOfMonth(2012, 2).getMonth());
		Assert.assertEquals(2012-1900,
				DateTimeHelper.getLastDateOfMonth(2012, 2).getYear());
	}

	public void testGetWeekInMonth() {
		Assert.assertEquals(3, DateTimeHelper.getWeekInMonth(DateTimeHelper.getDate(2013,1,20)));
	}

	public void testDoubleToStringWithFormat() {
		Assert.assertEquals(27, DateTimeHelper.getLastDayOfWeek(DateTimeHelper.getDate(2013,0,20)).getDate());
	}
	
	public void testGetDayOfMonth(){
		Assert.assertEquals(29, DateTimeHelper.getDayOfMonth(2012, 1));
	}
}
