package money.Tracker.presentation.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LendViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lend_view, menu);
        return true;
    }
}
