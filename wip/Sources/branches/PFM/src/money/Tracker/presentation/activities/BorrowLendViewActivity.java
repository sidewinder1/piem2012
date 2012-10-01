package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;
import money.Tracker.repository.DataManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BorrowLendViewActivity extends Activity {
	private TextView displayText;
	private ListView list;
	private BorrowLendAdapter borrowLendAdapter;

	boolean checkBorrowing;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_lend_view);
        Bundle extras = getIntent().getExtras();
		//checkBorrowing = extras.getBoolean("Borrow");
        checkBorrowing = true;
		//displayText = (TextView) findViewById(R.id.no_borrow_lend_data);
		list = (ListView) findViewById(R.id.borrow_lend_list_view);
		
		bindData();
    }
    
    @Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		String tableName;
		if (checkBorrowing) {
			tableName = "Borrowing";
		} else {
			tableName = "Lending";
		}
		
		BorrowLendRepository bolere = new BorrowLendRepository();
		ArrayList<BorrowLend> values = bolere.getData(tableName);
		
//		if (values.size() == 0) {
//			displayText.setVisibility(View.VISIBLE);
//			return;
//		}
//
//		displayText.setVisibility(View.GONE);
		borrowLendAdapter = new BorrowLendAdapter(this, values);
		borrowLendAdapter.notifyDataSetChanged();
		
		list.setAdapter(borrowLendAdapter);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_lend_view, menu);
        return true;
    }
}
