package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class ScheduleViewTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	/*
	 * This is used to test number of schedules.
	 */
	public void testNumberOfSchedule() {
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

		Assert.assertNotSame(10,
				SqlHelper.instance.select("Schedule", "*", "1 = 1"));
	}

	/*
	 * This method is used to verify data that will be displayed.
	 */
	public void testVerifyData() {
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

		Cursor schedule = SqlHelper.instance.select("Schedule", "*",
				"Budget = 30000");
		Assert.assertNotNull(schedule);
		Assert.assertEquals(true, schedule.moveToFirst());
		Assert.assertEquals(1, schedule.getCount());
		Assert.assertEquals(1, schedule.getInt(schedule.getColumnIndex("Type")));
		Assert.assertEquals("2012-10-3",
				schedule.getString(schedule.getColumnIndex("Start_date")));
		Assert.assertEquals("2012-11-23",
				schedule.getString(schedule.getColumnIndex("End_date")));
	}
}
