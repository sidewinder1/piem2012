package money.Tracker.presentation.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class BorrowingLendingInsert extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrowing_lending_insert);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrowing_lending_insert, menu);
        return true;
    }
}
