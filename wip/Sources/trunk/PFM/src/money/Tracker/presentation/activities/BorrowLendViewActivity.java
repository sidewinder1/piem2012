package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;
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
import android.widget.ListView;
import android.widget.TextView;
import money.Tracker.presentation.activities.BorrowLendViewDetailActivity;

public class BorrowLendViewActivity extends Activity {
	private TextView displayText;
	private ListView borrowLendList;
	private BorrowLendAdapter borrowLendAdapter;
	private BorrowLend borrowLend;
	private String debtType = "";
	private boolean checkBorrowing;
	private TextView totalMoneyTextView;
	private TextView mCurrentInterest;
	private ArrayList<Object> values;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_view);
		Bundle extras = getIntent().getExtras();
		checkBorrowing = extras.getBoolean("Borrow");
		displayText = (TextView) findViewById(R.id.no_borrow_lend_data);
		borrowLendList = (ListView) findViewById(R.id.borrow_lend_list_view);

		bindData();
		// borrowLendList.setTextFilterEnabled(true);
		// borrowLendList.setClickable(true);
		// borrowLendList.setItemsCanFocus(false);
		// borrowLendList.setFocusableInTouchMode(false);
		borrowLendList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
				borrowLend = (BorrowLend) borrowLendList.getAdapter().getItem(position);				
				if (borrowLend != null) {
					Intent borrowLendDetail = new Intent(BorrowLendViewActivity.this, BorrowLendViewDetailActivity.class);
					borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
					borrowLendDetail.putExtra("checkBorrowing", checkBorrowing);
					startActivity(borrowLendDetail);
				}
			}
		});

		totalMoneyTextView = (TextView) findViewById(R.id.borrow_lend_view_total_money);
		mCurrentInterest = (TextView) findViewById(R.id.borrow_lend_view_total_revenue);
		if (checkBorrowing) {
			debtType = "Borrowing";
		} else {
			debtType = "Lending";
		}

		getTotalInformatation();

		registerForContextMenu(borrowLendList);

	}

	private void getTotalInformatation() {
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like '" + debtType + "'");

		double totalMoney = 0;
		double totalInterest = 0;		

		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					double money = borrowLendData.getDouble(borrowLendData.getColumnIndex("Money"));
					totalMoney += money;
					if (!borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim().equals("")) {
						String interestType = borrowLendData.getString(borrowLendData.getColumnIndex("Interest_type"));
						long interestRate = borrowLendData.getLong(borrowLendData.getColumnIndex("Interest_rate"));
						Date startDate = Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Start_date")).trim());
						Date expiredDate = Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim());
						
						totalInterest += caculateInterest(money, interestType, interestRate, startDate, expiredDate);
					}
					
				} while (borrowLendData.moveToNext());
			}
		}

		totalMoneyTextView.setText(Converter.toString(totalMoney));
		mCurrentInterest.setText(Converter.toString(totalInterest));
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

		TextView totalMoneyTitle = (TextView) findViewById(R.id.borrow_lend_total_money_title);
		totalMoneyTitle.setText(getResources().getString(checkBorrowing ? R.string.total_borrow_money: R.string.total_lend_money));
		BorrowLendRepository bolere = new BorrowLendRepository();
		values = bolere.getData("Debt_type like '" + debtType + "' order by expired_date DESC");

		if (values.size() == 0) {
			displayText.setVisibility(View.VISIBLE);
		} else {
			displayText.setVisibility(View.GONE);
		}

		sort();
		borrowLendAdapter = new BorrowLendAdapter(this, R.layout.activity_borrow_lend_view_item, values);

		borrowLendList.setVisibility(View.VISIBLE);
		borrowLendAdapter.notifyDataSetChanged();
		borrowLendList.setAdapter(borrowLendAdapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.borrow_lend_list_view) {
			menu.setHeaderTitle(getResources().getString(R.string.schedule_menu_title));
			String[] menuItems = getResources().getStringArray(R.array.schedule_context_menu_item);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		int menuItemIndex = item.getItemId();
		borrowLend = (BorrowLend) borrowLendList.getAdapter().getItem(info.position);
		switch (menuItemIndex) {
		case 0: // Edit
			Intent borrowLendDetail = new Intent(BorrowLendViewActivity.this, BorrowLendInsertActivity.class);
			borrowLendDetail.putExtra("borrowLendID", borrowLend.getId());
			startActivity(borrowLendDetail);
			break;
		case 1: // Delete
			String debtType = borrowLend.getDebtType();
			Alert.getInstance().showDialog(getParent(), "Delete selected " + debtType + "?", new OnClickListener() {
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

	private void sort() {
		int i, j;
		int length = values.size();
		BorrowLend t = new BorrowLend();
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				Date j1ExpiredDate;
				Date jExpiredDate;
				if (((BorrowLend) values.get(j - 1)).getExpiredDate() != null)
					j1ExpiredDate = ((BorrowLend) values.get(j - 1)).getExpiredDate();
				else {
					j1ExpiredDate = Converter.toDate("1/1/9999", "dd/MM/yyyy");
				}

				if (((BorrowLend) values.get(j)).getExpiredDate() != null)
					jExpiredDate = ((BorrowLend) values.get(j)).getExpiredDate();
				else {
					jExpiredDate = Converter.toDate("1/1/9999", "dd/MM/yyyy");					
				}
				if (j1ExpiredDate.compareTo(jExpiredDate) > 0) {
					Log.d("Check sort", String.valueOf(j1ExpiredDate.compareTo(jExpiredDate)));
					t.setValue((BorrowLend) values.get(j - 1));
					((BorrowLend) values.get(j - 1)).setValue((BorrowLend) values.get(j));
					((BorrowLend) values.get(j)).setValue(t);
				}
			}
		}

	}
	
	private double caculateInterest(double money, String interestType, long interestRateData, Date startDate, Date expiredDate) {		
		Date currentDate = new Date();
		long caculateInterestDate = 0;
		long leftDate = 0;
		double interestRate = 0;
		double totalInterestCaculate = 0;
		double totalMoney = 0;
		
		if (daysBetween(startDate, expiredDate) != 0)
			interestRate = (double)interestRateData / (double)daysBetween(startDate, expiredDate) /100;
			
			if (compareDate(currentDate, expiredDate)) {
				caculateInterestDate = daysBetween(startDate, currentDate);
				leftDate = daysBetween(currentDate, expiredDate);
			} else {
				caculateInterestDate = daysBetween(startDate, expiredDate);
			}
			
			Log.d("Check caculate interest Date", leftDate + " - " + caculateInterestDate);
			
			if (interestType.equals("Simple")) {
				totalInterestCaculate = money * interestRate * caculateInterestDate;
				totalMoney = money + totalInterestCaculate;
			} else {
				totalMoney += 1;
				
				for (long i = 0; i < leftDate; i++) {
					totalMoney = totalMoney * (1 + interestRate);
				}
				
				totalMoney = money * totalMoney;

				totalInterestCaculate += totalMoney - money;
			}
			
			return totalInterestCaculate;
	}

	/**
	 * This method also assumes endDate >= startDate
	 **/
	private long daysBetween(Date startDate, Date endDate) {
		Calendar sDate = getDatePart(startDate);
		Calendar eDate = getDatePart(endDate);

		long daysBetween = 0;
		while (sDate.before(eDate)) {
			sDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}

	private Calendar getDatePart(Date date) {
		Calendar cal = Calendar.getInstance(); // get calendar instance
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0); // set hour to midnight
		cal.set(Calendar.MINUTE, 0); // set minute in hour
		cal.set(Calendar.SECOND, 0); // set second in minute
		cal.set(Calendar.MILLISECOND, 0); // set millisecond in second

		return cal; // return the date part
	}

	private boolean compareDate(Date date1, Date date2) {
		Long dateNumber1 = date1.getTime();
		Long dateNumber2 = date2.getTime();

		if (dateNumber1 < dateNumber2)
			return true;
		else
			return false;
	}
}
