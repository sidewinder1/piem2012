package money.Tracker.presentation.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BorrowLendViewDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_lend_view_detail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_lend_view_detail, menu);
        return true;
    }
}
