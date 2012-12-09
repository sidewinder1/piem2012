package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class ScheduleDeleteTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	/* 
	 * This method is used for unit test deleting a schedule.
	 */
	public void testDeleteSchedule() {
		// Delete old data.
		SqlHelper.instance.delete("Schedule", "1 = 1");

		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("Schedule",
							new String[] { "Budget", "Type", "Start_date",
									"End_date" },
							new String[] { String.valueOf(i * 10000),
									String.valueOf(i % 2), "2012-10-" + i,
									"2012-11-" + (i + 20) });
		}
		
		Assert.assertEquals(true,
				SqlHelper.instance.delete("Schedule", "End_date = '2012-11-21 12:00:00'"));
		Assert.assertEquals(true,
				SqlHelper.instance.delete("Schedule", "Type = 1"));
		Assert.assertEquals(true, SqlHelper.instance.delete("Schedule", "1 = 1"));
	}
}
