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
		Log.d("report detail", "Check 1");
		Bundle extras = getIntent().getExtras();
		int scheduleID = extras.getInt("schedule_id");
		Log.d("report detail", "Check 2 - " + scheduleID);

		TextView budgetTextView = (TextView) findViewById(R.id.report_detail_total_budget);

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*",
				"Id=" + scheduleID);
		if (scheduleCursor != null) {
			Log.d("report detail", "Check 3");
			if (scheduleCursor.moveToFirst()) {
				Log.d("report detail", "Check 4");
				do {
					Log.d("report detail", "Check 5");
					startDate = Converter.toDate(scheduleCursor
							.getString(scheduleCursor
									.getColumnIndex("Start_date")));
					endDate = Converter.toDate(scheduleCursor
							.getString(scheduleCursor
									.getColumnIndex("End_date")));

					budgetTextView
							.setText(Converter.toString(scheduleCursor
									.getDouble(scheduleCursor
											.getColumnIndex("Budget"))));
				} while (scheduleCursor.moveToNext());
			}
		}

		Log.d("report detail",
				"Check 5 - " + Converter.toString(startDate, "dd/MM/yyyy"));
		Log.d("report detail",
				"Check 5 - " + Converter.toString(endDate, "dd/MM/yyyy"));
	}

	private void getIncome() {
		double total = 0;
		TextView totalIncomeTextView = (TextView) findViewById(R.id.report_detail_total_income);
		LinearLayout incomeDetail = (LinearLayout) findViewById(R.id.report_detail_income_list_view);
		incomeDetail.removeAllViews();
		
		Cursor entryCursor = SqlHelper.instance.select("Entry", "*", "Type=0");
		if (entryCursor != null) {
			if (entryCursor.moveToFirst()) {
				do {
					int id = entryCursor.getInt(entryCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryCursor
							.getString(entryCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									total += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryCursor.moveToNext());
			}

			totalIncomeTextView.setText(Converter.toString(total));

			if (entryCursor.moveToFirst()) {
				do {
					int id = entryCursor.getInt(entryCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryCursor
							.getString(entryCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									String name = entryDetailCursor
											.getString(entryDetailCursor
													.getColumnIndex("Name"));
									double value = entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
									
									incomeDetail.addView(new ReportDetailProduct(this, name, value), params);
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryCursor.moveToNext());
			}
		}
	}

	private void getExpense() {
		double totalExpense = 0;
		TextView totalExpenseTextView = (TextView) findViewById(R.id.report_detail_total_expense);
		LinearLayout expenseDetail = (LinearLayout) findViewById(R.id.report_detail_expense_list_view);
		expenseDetail.removeAllViews();

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
			totalExpenseTextView.setText(Converter.toString(totalExpense));

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
									expenseDetail.addView(new ReportDetailCategory(this, name, value, id, categoryID), params);
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
				TextView totalBorrowingTextView = (TextView) findViewById(R.id.report_detail_total_borrowing);
				LinearLayout borrowingDetail = (LinearLayout) findViewById(R.id.report_detail_borrowing_list_view);
				borrowingDetail.removeAllViews();
				
				Cursor borrowingCursor = SqlHelper.instance.select("BorrowLend", "*",
						"Debt_type like 'Borrowing'");
				if (borrowingCursor != null) {
					if (borrowingCursor.moveToFirst()) {
						do {
							Date entryDate = Converter.toDate(borrowingCursor
									.getString(borrowingCursor
											.getColumnIndex("Start_date")));
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

					totalBorrowingTextView.setText(Converter.toString(totalBorrowing));

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
								
								borrowingDetail.addView(new ReportDetailProduct(this, name, value), params);
							}
						} while (borrowingCursor.moveToNext());
					}
				}
	}
	
	private void getLending()
	{
		double totalLending = 0;
		TextView totalLendingTextView = (TextView) findViewById(R.id.report_detail_total_lending);
		LinearLayout lendingDetail = (LinearLayout) findViewById(R.id.report_detai_lending_list_view);
		lendingDetail.removeAllViews();
		
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

			totalLendingTextView.setText(Converter.toString(totalLending));

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
						
						lendingDetail.addView(new ReportDetailProduct(this, name, value), params);
					}
				} while (lendingCursor.moveToNext());
			}
		}
		
	}

	private void bindData() {
		// TODO Auto-generated method stub
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
