package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.BorrowLendRepository;
import money.Tracker.repository.DataManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BorrowLendViewActivity extends Activity {
	private TextView displayText;
	private ListView borrowLendList;
	private BorrowLendAdapter borrowLendAdapter;

	boolean checkBorrowing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_view);
		Bundle extras = getIntent().getExtras();
		checkBorrowing = extras.getBoolean("Borrow");
		// displayText = (TextView) findViewById(R.id.no_borrow_lend_data);
		borrowLendList = (ListView) findViewById(R.id.borrow_lend_list_view);
		bindData();
		Log.d("On Click Item", "Check 1");
		borrowLendList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Log.d("On Click Item", "Check 3");
				BorrowLend borrowLend = (BorrowLend) borrowLendList.getAdapter().getItem(position);
				Log.d("On Click Item", "Check 4");
				if (borrowLend != null)
				{
					Intent borrowLendDetail =new Intent(BorrowLendViewActivity.this, BorrowLendViewDetailActivity.class);
					borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
					borrowLendDetail.putExtra("checkBorrowing", checkBorrowing);
					startActivity(borrowLendDetail);
				}
				Log.d("On Click Item", "Check 5");
			}
		});
		Log.d("On Click Item", "Check 2");
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

		// if (values.size() == 0) {
		// displayText.setVisibility(View.VISIBLE);
		// return;
		// }
		//
		// displayText.setVisibility(View.GONE);
		borrowLendAdapter = new BorrowLendAdapter(this, values);
		borrowLendAdapter.notifyDataSetChanged();

		borrowLendList.setAdapter(borrowLendAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_borrow_lend_view, menu);
		return true;
	}
}
