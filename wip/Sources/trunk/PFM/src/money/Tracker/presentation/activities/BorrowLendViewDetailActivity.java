package money.Tracker.presentation.activities;

import java.util.Calendar;
import java.util.Date;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.BorrowLendViewDetailViewItem;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class BorrowLendViewDetailActivity extends BaseActivity {

	private double totalInterestCaculate;
	private double totalMoney;
	private long leftDate;
	private BorrowLend values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		setContentView(R.layout.activity_borrow_lend_view_detail);
		
		totalInterestCaculate = 0;
		totalMoney = 0;
		leftDate = 0;

		TextView borrowLendDetailTitle = (TextView) findViewById(R.id.borrow_lend_detail_title);
		LinearLayout listViewDetail = (LinearLayout) findViewById(R.id.borrow_lend_list_view_detail);

		Button editButton = (Button) findViewById(R.id.borrow_lend_detail_view_edit_button);

		Bundle extras = getIntent().getExtras();
		final long borrow_lend_id = extras.getLong("borrowLendID");

		BorrowLendRepository bolere = new BorrowLendRepository();
		values = bolere.getDetailData("ID=" + borrow_lend_id);
		
		if (values.getDebtType().equals("Borrowing"))
			borrowLendDetailTitle.setText(getResources().getString(R.string.borrow_view_title));
		else
			borrowLendDetailTitle.setText(getResources().getString(R.string.lend_view_title));

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		if (values.getDebtType().equals("Borrowing"))
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_lender), values.getPersonName()), params);
		else
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_borrower), values.getPersonName()), params);

		if (!values.getPersonPhone().equals(""))
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_phone), values.getPersonPhone()), params);

		if (!values.getPersonAddress().equals(""))
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_address), values.getPersonAddress()), params);

		if (values.getInterestRate() != 0) {
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_interest),new StringBuilder(String.valueOf(values.getInterestRate())).append(" ").append(getResources().getString(R.string.percent)).toString()), params);

			if (values.getInterestType().equals("Simple"))
				listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_interest_type),getResources().getString(R.string.simple_interest)), params);
			else
				listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_interest_type),getResources().getString(R.string.compound_interest)), params);
		}

		listViewDetail.addView(new BorrowLendViewDetailViewItem(this,getResources().getString(R.string.borrow_lend_start_date), Converter.toString(values.getStartDate(), "dd/MM/yyyy")), params);

		if (values.getExpiredDate() != null)
			listViewDetail.addView(new BorrowLendViewDetailViewItem(this, getResources().getString(R.string.borrow_lend_end_date), Converter.toString(values.getExpiredDate(), "dd/MM/yyyy")), params);

		caculateInterest();

		TextView totalMoneyTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_total_money);
		TextView totalInterestTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_total_interest);
		TextView currentInterestTitle = (TextView) findViewById(R.id.borrow_lend_detail_view_total_interest_title);
		// TODO: TuanNA
		// TextView leftDayTextView = (TextView)
		// findViewById(R.id.borrow_lend_detail_view_left_day);

		totalMoneyTextView.setText(Converter.toString(values.getMoney()));
		if(values.getInterestType().equals("Simple"))
		{
			totalInterestTextView.setText(Converter.toString(totalInterestCaculate));
		}else
		{
			currentInterestTitle.setText(getResources().getString(R.string.borrow_lend_total_money_plus_current_interest));
			if (totalMoney != 0)
				totalInterestTextView.setText(Converter.toString(totalMoney));
			else
				totalInterestTextView.setText(getResources().getString(R.string.borrow_lend_no_interest));
		}

		// TODO: TuanNA
		// if (leftDate != 0)
		// leftDayTextView.setText("You only have " + leftDate + " day");
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
				interestRate = (double) values.getInterestRate() / 360 / 100;

			if (compareDate(currentDate, expiredDate)) {
				caculateInterestDate = daysBetween(startDate, currentDate);
			} else {
				caculateInterestDate = daysBetween(startDate, expiredDate);
			}
			
			Log.d("Check caculate interest Date", leftDate + " - " + caculateInterestDate);
			if (values.getInterestType().equals("Simple")) {
				Log.d("Check caculate interest Date", money + " - " + interestRate + " - " + caculateInterestDate);
				totalInterestCaculate = money * interestRate * caculateInterestDate;
				totalMoney = money + totalInterestCaculate;
			} else {
				if(daysBetween(startDate, expiredDate) > 360)
				{
					if(daysBetween(startDate, currentDate) > 360)
					{
						totalMoney = money;
						int numberYear = 0;
						
						if (currentDate.compareTo(expiredDate) < 0)
						{
							numberYear = daysBetween(startDate, currentDate ) / 360; 
						} else
						{
							if (daysBetween(startDate, currentDate ) % 360 == 0)
							{
								numberYear = daysBetween(startDate, currentDate ) / 360;
							} else
							{
								numberYear = (daysBetween(startDate, currentDate ) / 360) + 1;
							}
						}
						
						for (int i = 0; i < numberYear; i++)
						{
							totalInterestCaculate = totalMoney * interestRate * 360;
							totalMoney = totalMoney + totalInterestCaculate;
						}
					}
					
				} else if (daysBetween(startDate, expiredDate) > 30 && daysBetween(startDate, expiredDate) < 360)
				{
					if(daysBetween(startDate, currentDate) > 30)
					{
						totalMoney = money;
						int numberMonth = 0;
						
						if (currentDate.compareTo(expiredDate) < 0)
						{
							numberMonth = daysBetween(startDate, currentDate ) / 30; 
						} else
						{
							if (daysBetween(startDate, currentDate ) % 30 == 0)
							{
								numberMonth = daysBetween(startDate, currentDate ) / 30;
							} else
							{
								numberMonth = (daysBetween(startDate, currentDate ) / 30) + 1;
							}
						}
						
						for (int i = 0; i < numberMonth; i++)
						{
							totalInterestCaculate = totalMoney * interestRate * 30;
							totalMoney = totalMoney + totalInterestCaculate;
						}
					}
				} else
				{
					totalMoney = money;
					int numberDay = 0;
					
					if (currentDate.compareTo(startDate) == 0)
						numberDay = 0;
					else if(currentDate.compareTo(expiredDate) < 0)
						numberDay = daysBetween(startDate, currentDate);
					else
						numberDay = daysBetween(startDate, expiredDate);
					
					for (int i = 0; i < numberDay; i++)
					{
						totalInterestCaculate = totalMoney * interestRate;
						totalMoney = totalMoney + totalInterestCaculate;
					}
				}
			}
		} else {
			totalMoney = values.getMoney();
		}
	}

	/**
	 * This method also assumes endDate >= startDate
	 **/
	private int daysBetween(Date startDate, Date endDate) {
		Calendar sDate = getDatePart(startDate);
		Calendar eDate = getDatePart(endDate);

		int daysBetween = 0;
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
