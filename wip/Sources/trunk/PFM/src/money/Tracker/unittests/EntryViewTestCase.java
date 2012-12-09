package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class EntryViewTestCase extends AndroidTestCase {
	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	/*
	 * This case will check the number of entries in database.
	 */
	public void testNumberOfEntry() {
		// Delete old data.
		SqlHelper.instance.delete("Entry", "1 = 1");

		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("Entry", new String[] { "Type", "Date" },
							new String[] { String.valueOf(i % 2),
									"2012-11-" + (i + 20) });
		}

		Assert.assertNotSame(10,
				SqlHelper.instance.select("Entry", "*", "1 = 1"));
	}

	/*
	 * This method is used to verify data that will be displayed.
	 */
	public void testVerifyData() {
		// Delete old data.
		SqlHelper.instance.delete("Entry", "1 = 1");

		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("Entry", new String[] { "Type", "Date" },
							new String[] { String.valueOf(i % 2),
									"2012-11-" + (i + 20) });
		}

		Cursor entry = SqlHelper.instance.select("Entry", "*", "Type = 1");
		Assert.assertNotNull(entry);
		Assert.assertEquals(true, entry.moveToFirst());
		Assert.assertEquals(5, entry.getCount());
		Assert.assertEquals("2012-11-21",
				entry.getString(entry.getColumnIndex("Date")));
	}
}
