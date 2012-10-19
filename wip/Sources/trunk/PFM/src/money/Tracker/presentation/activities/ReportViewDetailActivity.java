package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.customviews.ReportDetailCategory;
import money.Tracker.presentation.customviews.ReportDetailProduct;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.LinearGradient;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewDetailActivity extends Activity {
	private Date startDate;
	private Date endDate;
	private String _startDate;
	private String _endDate;
	private LinearLayout reportDetail;
	private boolean checkMonthly;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_detail);
		Log.d("report detail", "Check 1");
		bindData();
		Log.d("report detail", "Check 12");
	}

	// get schedule budget

	private void getSchedule() {		
		double budget = 0;
		String whereCondition = "";
		if(checkMonthly)
			whereCondition = "Type = 1";
		else
			whereCondition = "Type = 0";
		
		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", whereCondition);
		if(scheduleCursor != null)
		{
			if(scheduleCursor.moveToFirst())
			{
				do
				{
					if(checkMonthly)
					{
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						Log.d("Check get Month", scheduleMonth);
						String startDateMonth = Converter.toString(startDate, "MM");
						Log.d("Check get Month", startDateMonth);
						
						if(scheduleMonth.equals(startDateMonth))
							budget = scheduleCursor.getDouble(scheduleCursor.getColumnIndex("Budget"));
					} else
					{
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						Date scheduleEndDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("End_date")));						
						
						if((scheduleStartDate.compareTo(startDate) > 0 && scheduleEndDate.compareTo(endDate) < 0) ||
								(scheduleStartDate.compareTo(startDate) == 0 && scheduleEndDate.compareTo(endDate) == 0) ||
								(scheduleStartDate.compareTo(startDate) > 0 && scheduleEndDate.compareTo(endDate) == 0) ||
								(scheduleStartDate.compareTo(startDate) == 0 && scheduleEndDate.compareTo(endDate) < 0))
							budget = scheduleCursor.getDouble(scheduleCursor.getColumnIndex("Budget"));
					}
				}while(scheduleCursor.moveToNext());
			}
		}

		Log.d("report detail",
				"Check 5 - " + Converter.toString(startDate, "dd/MM/yyyy"));
		Log.d("report detail",
				"Check 5 - " + Converter.toString(endDate, "dd/MM/yyyy"));
		
		if (budget != 0)
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
			reportDetail.addView(new ReportDetailCategory(this, "Budget", budget), params);
		}
	}

	private void getIncome() {
		double totalIncome = 0;
		
		Cursor entryIncomeCursor = SqlHelper.instance.select("Entry", "*", "Type=0");
		if (entryIncomeCursor != null) {
			if (entryIncomeCursor.moveToFirst()) {
				do {
					int id = entryIncomeCursor.getInt(entryIncomeCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryIncomeCursor
							.getString(entryIncomeCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									totalIncome += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryIncomeCursor.moveToNext());
			}
			
			if (totalIncome != 0)
			{
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
				reportDetail.addView(new ReportDetailCategory(this, "Income", totalIncome), params);
			}

			if (entryIncomeCursor.moveToFirst()) {
				Log.d("report detail", "Check 57");
				do {
					Log.d("report detail", "Check 58");
					int id = entryIncomeCursor.getInt(entryIncomeCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryIncomeCursor
							.getString(entryIncomeCursor
									.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Log.d("report detail", "Check 59");
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail",
								"Category_Id, sum(Money) as Total", "Entry_Id="
										+ id + " group by Category_Id");
						Log.d("report detail", "Check 60");
						if (entryDetailCursor != null) {
							Log.d("report detail", "Check 61");
							if (entryDetailCursor.moveToFirst()) {
								Log.d("report detail", "Check 62");
								do {
									Log.d("report detail", "Check 63");
									int categoryID = entryDetailCursor
											.getInt(entryDetailCursor
													.getColumnIndex("Category_Id"));
									String name = "";
									Cursor categoryCursor = SqlHelper.instance
											.select("Category", "*", "Id="
													+ categoryID);
									Log.d("report detail", "Check 64");
									if (categoryCursor != null) {
										Log.d("report detail", "Check 65");
										if (categoryCursor.moveToFirst()) {
											Log.d("report detail", "Check 66");
											do {
												Log.d("report detail",
														"Check 67");
												name = categoryCursor
														.getString(categoryCursor
																.getColumnIndex("Name"));
											} while (categoryCursor
													.moveToNext());
										}
									}
									double value = entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Total"));
									Log.d("report detail",
											"Check 68");
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
									Log.d("report detail",
											"Check 69");
									reportDetail.addView(new ReportDetailProduct(this, name, value), params);
									Log.d("report detail",
											"Check 70");
									
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryIncomeCursor.moveToNext());
			}
		}
	}

	private void getExpense() {
		double totalExpense = 0;		

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*",
				"Type=1");
		Log.d("report detail", "Check 51");
		if (entryExpenseCursor != null) {
			Log.d("report detail", "Check 52");
			if (entryExpenseCursor.moveToFirst()) {
				Log.d("report detail", "Check 53");
				do {
					Log.d("report detail", "Check 54");
					int id = entryExpenseCursor.getInt(entryExpenseCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor
							.getString(entryExpenseCursor
									.getColumnIndex("Date")));
					Log.d("report detail",
							"Check 55 - "
									+ Converter.toString(entryDate,
											"dd/MM/yyyy"));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Log.d("report detail", "Check 56");
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									totalExpense += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
			
			if (totalExpense != 0)
			{
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
				reportDetail.addView(new ReportDetailCategory(this, "Expense", totalExpense), params);
			}

			if (entryExpenseCursor.moveToFirst()) {
				Log.d("report detail", "Check 57");
				do {
					Log.d("report detail", "Check 58");
					int id = entryExpenseCursor.getInt(entryExpenseCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor
							.getString(entryExpenseCursor
									.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Log.d("report detail", "Check 59");
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail",
								"Category_Id, sum(Money) as Total", "Entry_Id="
										+ id + " group by Category_Id");
						Log.d("report detail", "Check 60");
						if (entryDetailCursor != null) {
							Log.d("report detail", "Check 61");
							if (entryDetailCursor.moveToFirst()) {
								Log.d("report detail", "Check 62");
								do {
									Log.d("report detail", "Check 63");
									int categoryID = entryDetailCursor
											.getInt(entryDetailCursor
													.getColumnIndex("Category_Id"));
									String name = "";
									Cursor categoryCursor = SqlHelper.instance
											.select("Category", "*", "Id="
													+ categoryID);
									Log.d("report detail", "Check 64");
									if (categoryCursor != null) {
										Log.d("report detail", "Check 65");
										if (categoryCursor.moveToFirst()) {
											Log.d("report detail", "Check 66");
											do {
												Log.d("report detail",
														"Check 67");
												name = categoryCursor
														.getString(categoryCursor
																.getColumnIndex("Name"));
											} while (categoryCursor
													.moveToNext());
										}
									}
									double value = entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Total"));
									Log.d("report detail",
											"Check 68");
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
									Log.d("report detail",
											"Check 69");
									reportDetail.addView(new ReportDetailProduct(this, name, value), params);
									Log.d("report detail",
											"Check 70");
									
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
	}
	
	private void getBorrowing()
	{
				double totalBorrowing = 0;
				
				Cursor borrowingCursor = SqlHelper.instance.select("BorrowLend", "*",
						"Debt_type like 'Borrowing'");
				if (borrowingCursor != null) {
					if (borrowingCursor.moveToFirst()) {
						do {
							Date entryDate = Converter.toDate(borrowingCursor
									.getString(borrowingCursor
											.getColumnIndex("Start_date")), "dd/MM/yyyy");
							Date startDate1 = Converter.toDate(_startDate, "dd/MM/yyyy");
							Date endDate1 = Converter.toDate(_endDate, "dd/MM/yyyy");
							Log.d("Check date", startDate1.toString());
							Log.d("Check date", endDate1.toString());
							if (entryDate.compareTo(startDate) > 0
									&& entryDate.compareTo(endDate) < 0
									|| entryDate.compareTo(startDate) == 0
									|| entryDate.compareTo(endDate) == 0) {
								Log.d("report detail", "Check 56");
								totalBorrowing += borrowingCursor
										.getDouble(borrowingCursor
												.getColumnIndex("Money"));
							}
						} while (borrowingCursor.moveToNext());
					}

					if (totalBorrowing != 0)
					{
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					
						reportDetail.addView(new ReportDetailCategory(this, "Borrowing", totalBorrowing), params);
					}

					if (borrowingCursor.moveToFirst()) {
						Log.d("report detail", "Check 57");
						do {
							Log.d("report detail", "Check 58");
							Date entryDate = Converter.toDate(borrowingCursor
									.getString(borrowingCursor
											.getColumnIndex("Start_date")));
							if (entryDate.compareTo(startDate) > 0
									&& entryDate.compareTo(endDate) < 0
									|| entryDate.compareTo(startDate) == 0
									|| entryDate.compareTo(endDate) == 0) {
								String name = borrowingCursor.getString(borrowingCursor
										.getColumnIndex("Person_name"));
								double value = Double.parseDouble(borrowingCursor.getString(borrowingCursor
										.getColumnIndex("Money")));
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
										LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
								
								reportDetail.addView(new ReportDetailProduct(this, name, value), params);
							}
						} while (borrowingCursor.moveToNext());
					}
				}
	}
	
	private void getLending()
	{
		double totalLending = 0;
		
		Cursor lendingCursor = SqlHelper.instance.select("BorrowLend", "*",
				"Debt_type like 'Lending'");
		if (lendingCursor != null) {
			if (lendingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(lendingCursor
							.getString(lendingCursor
									.getColumnIndex("Start_date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Log.d("report detail", "Check 56");
						totalLending += lendingCursor.getDouble(lendingCursor
								.getColumnIndex("Money"));
					}
				} while (lendingCursor.moveToNext());
			}

			if (totalLending != 0)
			{
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			
				reportDetail.addView(new ReportDetailCategory(this, "Lending", totalLending), params);
			}

			if (lendingCursor.moveToFirst()) {
				Log.d("report detail", "Check 57");
				do {
					Log.d("report detail", "Check 58");
					Date entryDate = Converter.toDate(lendingCursor
							.getString(lendingCursor
									.getColumnIndex("Start_date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						String name = lendingCursor.getString(lendingCursor
								.getColumnIndex("Person_name"));
						double value = Double.parseDouble(lendingCursor.getString(lendingCursor
								.getColumnIndex("Money")));
						
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						
						reportDetail.addView(new ReportDetailProduct(this, name, value), params);
					}
				} while (lendingCursor.moveToNext());
			}
		}
		
	}

	private void bindData() {
		// TODO Auto-generated method stub
		Log.d("report detail", "Check 1");
		Bundle extras = getIntent().getExtras();
		int scheduleID = extras.getInt("schedule_id");
		checkMonthly = extras.getBoolean("checkMonthly");
		_startDate = extras.getString("start_date");
		startDate = Converter.toDate(_startDate);
		_endDate = extras.getString("end_date");
		endDate = Converter.toDate(_endDate);
		Log.d("report detail", "Check 2 - " + scheduleID);
		
		reportDetail = (LinearLayout) findViewById(R.id.report_detail_list_view);
		reportDetail.removeAllViews();
		
		getSchedule();
		getIncome();
		getExpense();
		getBorrowing();
		getLending();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_report_view_detail, menu);
		return true;
	}
}
