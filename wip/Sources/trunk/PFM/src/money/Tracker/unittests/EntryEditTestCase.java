package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class EntryEditTestCase extends AndroidTestCase {
	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	/*
	 * This method is used to test adding a entry.
	 */
	public void testAddEntry() {
		Assert.assertNotSame(-1,
				SqlHelper.instance.insert("Entry", new String[] { "Date",
						"Type" }, new String[] {
						 "2012-10-31", "1" }));
	}

	/*
	 * This method is used to test editing a entry.
	 */
	public void testEditEntry() {
		// Add dummy data.
		SqlHelper.instance.insert("Entry", new String[] { "Id",
				"Type", "Date" }, new String[] { "100",
				"1", "2012-11-2" });

		// Assert.
		Assert.assertEquals(1,
				SqlHelper.instance.update("Entry", new String[] { 
						"Type", "Date"}, new String[] {
						"0", "2012-10-1" }, "Id = 100"));
	}
}
