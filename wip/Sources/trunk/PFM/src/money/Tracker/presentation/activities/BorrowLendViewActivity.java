package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.BorrowLendRepository;
import money.Tracker.repository.DataManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
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
	private BorrowLend borrowLend;
	private String debtType = "";
	private boolean checkBorrowing;
	private TextView totalMoneyTextView;
	private TextView latesExpiredDateTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_view);
		Bundle extras = getIntent().getExtras();
		checkBorrowing = extras.getBoolean("Borrow");
		displayText = (TextView) findViewById(R.id.no_borrow_lend_data);
		borrowLendList = (ListView) findViewById(R.id.borrow_lend_list_view);
		bindData();		
		//borrowLendList.setTextFilterEnabled(true);
		//borrowLendList.setClickable(true);
		//borrowLendList.setItemsCanFocus(false);
		//borrowLendList.setFocusableInTouchMode(false);
		borrowLendList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Log.d("On Click Item", "Check 1");
				borrowLend = (BorrowLend) borrowLendList.getAdapter().getItem(position);
				Log.d("On Click Item", "Check 2");
				if (borrowLend != null)
				{
					Intent borrowLendDetail =new Intent(BorrowLendViewActivity.this, BorrowLendViewDetailActivity.class);
					Log.d("On Click Item", "Check 3");
					borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
					Log.d("On Click Item", "Check 4 - " + borrowLend.getId());
					borrowLendDetail.putExtra("checkBorrowing", checkBorrowing);
					Log.d("On Click Item", "Check 5 - " + checkBorrowing);
					startActivity(borrowLendDetail);
				}				
				Log.d("On Click Item", "Check 6");
			}
		});		
		
		totalMoneyTextView = (TextView) findViewById(R.id.borrow_lend_view_total_money);
		latesExpiredDateTextView = (TextView) findViewById(R.id.borrow_lend_view_lates_expired_date);
		
		if (checkBorrowing) {
			debtType = "Borrowing";
		} else {
			debtType = "Lending";
		}
		
		getTotalInformatation();
		
		registerForContextMenu(borrowLendList);

	}
	
	private void getTotalInformatation()
	{
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like '" + debtType + "'");
		
		double totalMoney = 0;
		String latesExpiredDateString = "1/1/1900";
		
		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					totalMoney += borrowLendData.getDouble(borrowLendData
							.getColumnIndex("Money"));
					
					if (!borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Expired_date")).trim().equals(""))
					{
						String expiredDateString = borrowLendData
								.getString(borrowLendData
										.getColumnIndex("Expired_date")).trim();
						Log.d("errorDateTime", expiredDateString);
						Date _expiredDate = Converter.toDate(expiredDateString,
								"dd/MM/yyyy");						
						Date _latesExpiredDate = Converter.toDate(latesExpiredDateString, "dd/MM/yyyy");
						Long latesExpiredDate = _latesExpiredDate.getTime();						
						Long expiredDate = _expiredDate.getTime();
						
						if (expiredDate > latesExpiredDate)
						{
							latesExpiredDateString = expiredDateString;
						}
					}
				} while (borrowLendData.moveToNext());
			}
		}
		
		totalMoneyTextView.setText(Converter.toString(totalMoney));
		if (!latesExpiredDateString.equals("1/1/1900"))
			latesExpiredDateTextView.setText(latesExpiredDateString);
		else
			latesExpiredDateTextView.setText("");
	}


	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
		getTotalInformatation();
	}

	private void bindData() {
		if (checkBorrowing) {
			debtType = "Borrowing";
		} else {
			debtType = "Lending";
		}

		BorrowLendRepository bolere = new BorrowLendRepository();
		ArrayList<Object> values = bolere.getData("Debt_type like '" + debtType + "' order by expired_date");

		if (values.size() == 0) {
			Log.d("Check display", "Check 1");
			displayText.setVisibility(View.VISIBLE);
		}
		else
		{
			Log.d("Check display", "Check 2");
			displayText.setVisibility(View.GONE);
		}
		
		borrowLendAdapter = new BorrowLendAdapter(this, R.layout.activity_borrow_lend_view_item, values);
		
		borrowLendList.setVisibility(View.VISIBLE);
		borrowLendAdapter.notifyDataSetChanged();
		borrowLendList.setAdapter(borrowLendAdapter);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.borrow_lend_list_view) {
			menu.setHeaderTitle(getResources().getString(
					R.string.schedule_menu_title));
			String[] menuItems = getResources().getStringArray(
					R.array.schedule_context_menu_item);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		borrowLend = (BorrowLend) borrowLendList.getAdapter().getItem(info.position);
		switch (menuItemIndex) {
		case 0: // Edit
			Intent borrowLendDetail =new Intent(BorrowLendViewActivity.this, BorrowLendInsertActivity.class);
			borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
			startActivity(borrowLendDetail);
			break;
		case 1: // Delete
			Alert.getInstance().showDialog(getParent(),
					"Delete selected schedule?", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SqlHelper.instance.delete("BorrowLend", "Id = " + borrowLend.getId());
							bindData();
							getTotalInformatation();
						}
					});
			getTotalInformatation();
			break;
		}

		return true;
	}
}
