package money.Tracker.presentation.activities;

//import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
//import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BorrowLendViewDetailActivity extends Activity {

	private double totalInterestCaculate = 0;
	private double totalMoney = 0;
	private long leftDate = 0;
	private BorrowLend values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("View Detail", "Check 00");
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		setContentView(R.layout.activity_borrow_lend_view_detail);
		TextView personName = (TextView) findViewById(R.id.borrow_lend_detail_view_name);
		TextView personPhone = (TextView) findViewById(R.id.borrow_lend_detail_view_phone);
		TextView personAddress = (TextView) findViewById(R.id.borrow_lend_detail_view_address);
		TextView total = (TextView) findViewById(R.id.borrow_lend_detail_view_total);
		TextView interest = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_rate);
		TextView interestType = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_type);
		TextView startDate = (TextView) findViewById(R.id.borrow_lend_detail_view_start_date);
		TextView expriedDate = (TextView) findViewById(R.id.borrow_lend_detail_view_expired_date);
		Button editButton = (Button) findViewById(R.id.borrow_lend_detail_view_edit_button);

		Log.d("View Detail", "Check 1");
		Bundle extras = getIntent().getExtras();
		final long borrow_lend_id = extras.getLong("borrowLendID");

		BorrowLendRepository bolere = new BorrowLendRepository();
		values = bolere.getDetailData("ID=" + borrow_lend_id);
		Log.d("View Detail", "Check 4");
		personName.setText(String.valueOf(values.getPersonName()));
		personPhone.setText(String.valueOf(values.getPersonPhone()));
		personAddress.setText(String.valueOf(values.getPersonAddress()));
		total.setText(Converter.toString(values.getMoney()));
		interest.setText(String.valueOf(values.getInterestRate()));
		interestType.setText(String.valueOf(values.getInterestType()));
		Log.d("View Detail", String.valueOf(values.getStartDate()));
		startDate.setText(Converter.toString(values.getStartDate(),
				"dd/MM/yyyy"));
		Log.d("View Detail", String.valueOf(values.getExpiredDate()));
		if (values.getExpiredDate() != null) {
			expriedDate.setText(Converter.toString(values.getExpiredDate(),
					"dd/MM/yyyy"));
		} else {
			expriedDate.setText("");
		}
		Log.d("View Detail", "Check 5");

		caculateInterest();

		TextView totalMoneyTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_total_money);
		TextView totalInterestTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_total_interest);
		//TODO: TuanNA
//		TextView leftDayTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_left_day);

		totalMoneyTextView.setText(Converter.toString(totalMoney));
		Log.d("View detail", String.valueOf(totalMoney));
		totalInterestTextView
				.setText(Converter.toString(totalInterestCaculate));
		Log.d("View detail", "" + totalInterestCaculate);
		if (leftDate != 0)
			//TODO: TuanNA
//			leftDayTextView.setText("You only have " + leftDate + " day");
		// Handle edit button
		editButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent borrowLendEdit = new Intent(
						BorrowLendViewDetailActivity.this,
						BorrowLendInsertActivity.class);
				borrowLendEdit.putExtra("borrowLendID", borrow_lend_id);
				startActivity(borrowLendEdit);
			}
		});
	}

	private void caculateInterest() {
		Date currentDate = new Date();
		Date startDate = values.getStartDate();
		if (values.getExpiredDate() != null) {
			Date expiredDate = values.getExpiredDate();
			long caculateInterestDate = 0;

			double money = values.getMoney();
			double interestRate = 0;
			if (daysBetween(startDate, expiredDate) != 0)
				interestRate = values.getInterestRate()
						/ daysBetween(startDate, expiredDate);

			if (compareDate(currentDate, expiredDate)) {
				caculateInterestDate = daysBetween(startDate, currentDate);
				leftDate += daysBetween(currentDate, expiredDate);
			} else {
				caculateInterestDate = daysBetween(startDate, expiredDate);
			}

			if (values.getInterestType().equals("Simple")) {
				totalInterestCaculate += money * interestRate
						* caculateInterestDate;
				totalMoney += money + totalInterestCaculate;
			} else {
				totalMoney += 1;
				for (long i = 0; i < leftDate; i++) {
					totalMoney = totalMoney * totalMoney * interestRate;
				}
				totalMoney = money * totalMoney;

				totalInterestCaculate += totalMoney - money;
			}
		} else {
			totalMoney = values.getMoney();
		}
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater()
				.inflate(R.menu.activity_borrow_lend_view_detail, menu);
		return true;
	}
}
