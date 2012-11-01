package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class EntryDeleteTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testDeleteEntry() {
		// Delete old data.
		SqlHelper.instance.delete("Entry", "1 = 1");

		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("Entry", new String[] { "Type", "Date" },
							new String[] { String.valueOf(i % 2),
									"2012-11-" + (i + 20) });
		}

		Assert.assertEquals(true, SqlHelper.instance.delete("Entry", "Date = '2012-11-21'"));
		Assert.assertEquals(true, SqlHelper.instance.delete("Entry", "Type = 1"));
		Assert.assertEquals(true, SqlHelper.instance.delete("Entry", "1 = 1"));
	}
}
