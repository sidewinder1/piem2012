package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class BorrowLendDeleteTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	public void testDeleteBorrowLend() {
		// Delete old data.
		SqlHelper.instance.delete("BorrowLend", "1 = 1");

		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance.insert("BorrowLend", new String[] { "Debt_type",
					"Money", "Interest_type", "Interest_rate", "Start_date",
					"Expired_date", "Person_name", "Person_phone",
					"Person_address" },
					new String[] { "Borrowing", String.valueOf(i * 10000),
							"Simple", "", "2012-10-" + i,
							"2012-11-" + (i + 20), "A" + i, "", "" });
		}

		Assert.assertEquals(true, SqlHelper.instance.delete("BorrowLend","Start_date = '2012-11-21 12:00:00'"));
		Assert.assertEquals(true, SqlHelper.instance.delete("Schedule","Debt_type like 'Borrowing'"));
		Assert.assertEquals(true, SqlHelper.instance.delete("BorrowLend", "1 = 1"));
	}
}
