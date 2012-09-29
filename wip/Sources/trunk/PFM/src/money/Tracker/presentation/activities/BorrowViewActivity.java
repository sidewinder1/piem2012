package money.Tracker.presentation.activities;

import money.Tracker.common.sql.SqlHelper;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.view.Menu;

public class BorrowViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_view);
    }
    
	@Override
	protected void onRestart() {
		bindData();
	}

	private void bindData() {
		Cursor data = SqlHelper.instance.select("Borrowing",
				"Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_Phone, Person_address", null);

		if (data != null) {
			if (data.moveToFirst()) {
				do {
					double budget = data
							.getFloat(data.getColumnIndex("Budget"));
					String a = budget + "";
				} while (data.moveToNext());
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_view, menu);
        return true;
    }
}
