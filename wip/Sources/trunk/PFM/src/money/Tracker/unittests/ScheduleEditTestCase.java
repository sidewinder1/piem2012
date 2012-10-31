package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class ScheduleEditTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	public void testAddSchedule() {
		Assert.assertNotSame(
				-1,
				SqlHelper.instance.insert("Schedule", new String[] { "Budget",
						"Type", "Start_date", "End_date" }, new String[] {
						"10000", "1", "2012-10-31", "2012-11-2" }));
	}

	public void testEditSchedule() {
		// Add dummy data.
		SqlHelper.instance.insert("Schedule", new String[] { "Id", "Budget",
				"Type", "Start_date", "End_date" }, new String[] { "100",
				"10000", "1", "2012-10-31", "2012-11-2" });

		// Assert.
		Assert.assertEquals(
				1,
				SqlHelper.instance.update("Schedule", new String[] { "Budget",
						"Type", "Start_date", "End_date" }, new String[] {
						"10000", "1", "2012-10-31", "2012-11-2" }, "Id = 100"));
	}
}
