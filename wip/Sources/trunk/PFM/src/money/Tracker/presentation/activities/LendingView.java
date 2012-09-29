package money.Tracker.presentation.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LendingView extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lending_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lending_view, menu);
        return true;
    }
}
