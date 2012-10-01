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
	private ListView list;
	private BorrowLendAdapter borrowLendAdapter;

	boolean checkBorrowing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_view);
		Bundle extras = getIntent().getExtras();
		checkBorrowing = extras.getBoolean("Borrow");
		// displayText = (TextView) findViewById(R.id.no_borrow_lend_data);
		list = (ListView) findViewById(R.id.borrow_lend_list_view);
		list.setOnItemClickListener(onListClick);			
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

		// if (values.size() == 0) {
		// displayText.setVisibility(View.VISIBLE);
		// return;
		// }
		//
		// displayText.setVisibility(View.GONE);
		borrowLendAdapter = new BorrowLendAdapter(this, values);
		borrowLendAdapter.notifyDataSetChanged();

		list.setAdapter(borrowLendAdapter);
	}
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			BorrowLend borrowLend = (BorrowLend)list.getAdapter().getItem(position);
			Log.d("BLV", "Check 1");
			if (borrowLend != null)
			{
				Intent borrowLendDetail =new Intent(BorrowLendViewActivity.this, BorrowLendViewDetailActivity.class);
				Log.d("BLV", "Check 2");
				borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
				Log.d("BLV", "Check 3");
				borrowLendDetail.putExtra("checkBorrowing", checkBorrowing);
				Log.d("BLV", "Check 4");
				startActivity(borrowLendDetail);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_borrow_lend_view, menu);
		return true;
	}
}
