package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.database.Cursor;
import android.test.AndroidTestCase;

public class BorrowLendViewTestCase  extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	public void testNumberOfBorrowLend() {
		// Delete old data.
		SqlHelper.instance.delete("BorrowLend", "1 = 1");
		
		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("BorrowLend",
							new String[] { "Debt_type", "Money", "Interest_type", "Interest_rate", "Start_date", "Expired_date", "Person_name", "Person_phone", "Person_address"},
							new String[] { "Borrowing", String.valueOf(i * 10000), "Simple", "", "2012-10-" + i, "2012-11-" + (i + 20), "A" + i, "", ""});
		}
		
		Assert.assertNotSame(
				10,
				SqlHelper.instance.select("BorrowLend", "*", "1 = 1"));
	}

	public void testVerifyData() {
		// Delete old data.
		SqlHelper.instance.delete("BorrowLend", "1 = 1");
		
		// Create dummy data.
		for (int i = 0; i < 10; i++) {
			SqlHelper.instance
					.insert("BorrowLend",
							new String[] { "Debt_type", "Money", "Interest_type", "Interest_rate", "Start_date", "Expired_date", "Person_name", "Person_phone", "Person_address"},
							new String[] { "Borrowing", String.valueOf(i * 10000), "Simple", "", "2012-10-" + i, "2012-11-" + (i + 20), "A" + i, "", ""});
		}
		
		Cursor entry = SqlHelper.instance.select("BorrowLend", "*", "Type = 1");
		Assert.assertNotNull(entry);
		Assert.assertEquals(true, entry.moveToFirst());
		Assert.assertEquals(5, entry.getCount());
		Assert.assertEquals("2012-11-10", entry.getString(entry.getColumnIndex("Start_date")));
	}
}
