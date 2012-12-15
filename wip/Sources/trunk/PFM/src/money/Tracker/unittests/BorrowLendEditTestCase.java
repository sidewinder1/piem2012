package money.Tracker.unittests;

import junit.framework.Assert;
import money.Tracker.common.sql.SqlHelper;
import android.test.AndroidTestCase;

public class BorrowLendEditTestCase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		SqlHelper.instance = new SqlHelper(getContext());
		SqlHelper.instance.initializeTable();
	}

	public void testAddBorrowLend() {
		Assert.assertNotSame(
				-1,
				SqlHelper.instance.insert("BorrowLend", 
						new String[] { "Id", "Debt_type", "Money", "Interest_type", "Interest_rate",
						"Start_date", "Expired_date", "Person_name", "Person_phone", "Person_address" }, 
						new String[] { "2","Borrowing", String.valueOf(10000), "Simple", "10",
						"2012-10-14", "2012-11-30", "A1", "", "" }));
	}

	public void testEditBorrowLend() {
		// Add dummy data.
		SqlHelper.instance.insert("BorrowLend",
				new String[] { "Debt_type", "Money", "Interest_type", "Interest_rate", "Start_date", "Expired_date",
						"Person_name", "Person_phone", "Person_address" },
				new String[] { "Borrowing", String.valueOf(15000), "Simple",
						"10", "2012-10-14", "2012-11-30", "A1", "", "" });

		// Assert.
		Assert.assertEquals(
				1,
				SqlHelper.instance.update("BorrowLend", new String[] { "Money",
						"Person_name" }, new String[] { "0", "A1" }, "Id = 2"));
	}
}
