package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.repository.BorrowLendDataManager;
import money.Tracker.repository.DataManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BorrowLendViewActivity extends Activity {
	TextView displayText;
	
	private ScheduleViewAdapter scheduleAdapter;
	
	boolean checkBorrowing;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_lend_view);
        
        Bundle extras = getIntent().getExtras();
		checkBorrowing = extras.getBoolean("Borrow");
		
		displayText = (TextView) findViewById(R.id.no_data_borrow_lend_found);

		bindData();
    }
    
    @Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		ArrayList<Object> values;
		if (checkBorrowing == true)
			values = BorrowLendDataManager.getObjects("Borrowing");
		else
			values = BorrowLendDataManager.getObjects("Lending");
		if (values.size() == 0) {
			displayText.setVisibility(View.VISIBLE);
			return;
		}

		displayText.setVisibility(View.GONE);
		scheduleAdapter = new ScheduleViewAdapter(this,
				R.layout.schedule_edit_item, values);
		
		scheduleAdapter.notifyDataSetChanged();
		final ListView list = (ListView) findViewById(R.id.borrow_lend_view_list);
		list.setAdapter(scheduleAdapter);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_lend_view, menu);
        return true;
    }
}
