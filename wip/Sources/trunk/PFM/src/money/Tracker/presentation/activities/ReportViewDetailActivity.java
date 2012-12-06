package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.customviews.ReportDetailCategory;
import money.Tracker.presentation.customviews.ReportDetailProduct;
import money.Tracker.presentation.customviews.ReportMainDetailCategory;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
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
		bindData();
	}

	// get schedule budget

	private void getSchedule() {
		long budget = 0;
		String whereCondition = "";
		if (checkMonthly)
			whereCondition = "Type = 1";
		else
			whereCondition = "Type = 0";

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*",whereCondition);
		if (scheduleCursor != null) {
			if (scheduleCursor.moveToFirst()) {
				do {
					if (checkMonthly) {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String scheduleYear = Converter.toString(scheduleStartDate, "YYYY");
						Log.d("Check get Month", scheduleMonth);
						String startDateMonth = Converter.toString(startDate,"MM");
						String startDateYear = Converter.toString(startDate,"YYYY");
						Log.d("Check get Month", startDateMonth);

						if (scheduleMonth.equals(startDateMonth) && scheduleYear.equals(startDateYear))
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					} else {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calScheduleStart = Calendar.getInstance();
				        calScheduleStart.setTime(scheduleStartDate);
				        int scheduleWeek = calScheduleStart.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(scheduleStartDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (scheduleMonth.equals(startDateMonth) && scheduleWeek == startDateWeek){
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
						}
					}
				} while (scheduleCursor.moveToNext());
			}
		}

		if (budget != 0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			reportDetail.addView(new ReportMainDetailCategory(this, "Ngân Sách", budget, checkMonthly, startDate, endDate), params);
		}
	}

	private void getIncome() {
		long totalIncome = 0;

		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*","Type=0");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate,"MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate,"yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalIncome += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalIncome += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		if (totalIncome != 0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			reportDetail.addView(new ReportMainDetailCategory(this, "Thu Nhập", totalIncome, checkMonthly, startDate, endDate), params);
		}
		
	}

	private void getExpense() {
		long totalExpense = 0;

		List<String> entryCategoryName = new ArrayList<String>();
		List<Long> entryCategoryValue = new ArrayList<Long>();

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*","Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate,"MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate,"yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id+ " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;										

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
											}

										} else {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		if (totalExpense != 0) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			reportDetail.addView(new ReportMainDetailCategory(this, "Chi Phí", totalExpense, checkMonthly, startDate, endDate), params);
		}
	}

	private void getBorrowing() {		
		long totalBorrowing = 0;

		Cursor borrowingCursor = SqlHelper.instance.select("BorrowLend", "*",
				"Debt_type like 'Borrowing'");
		if (borrowingCursor != null) {
			if (borrowingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(borrowingCursor.getString(borrowingCursor.getColumnIndex("Start_date")),"dd/MM/yyyy");
					if (checkMonthly)
					{
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate, "YYYY");
						String startDateYear = Converter.toString(startDate,"YYYY");
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							totalBorrowing += borrowingCursor.getLong(borrowingCursor.getColumnIndex("Money"));
						}
					} else
					{
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek) {
						Log.d("report detail", "Check 56");
						totalBorrowing += borrowingCursor
								.getLong(borrowingCursor
										.getColumnIndex("Money"));
					}
					}
				} while (borrowingCursor.moveToNext());
			}

			if (totalBorrowing != 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				reportDetail.addView(new ReportMainDetailCategory(this, "Vay", totalBorrowing, checkMonthly, startDate, endDate), params);
			}
		}
	}

	private void getLending() {
		long totalLending = 0;

		Cursor lendingCursor = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like 'Lending'");
		if (lendingCursor != null) {
			if (lendingCursor.moveToFirst()) {
				do {
					Date entryDate = Converter.toDate(lendingCursor
							.getString(lendingCursor
									.getColumnIndex("Start_date")));
					if (checkMonthly)
					{
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate,
								"MM");
						String entryDateYear = Converter.toString(entryDate, "YYYY");
						String startDateYear = Converter.toString(startDate,
								"YYYY");
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							totalLending += lendingCursor.getLong(lendingCursor
									.getColumnIndex("Money"));
						}
					}else
					{
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek){
						Log.d("report detail", "Check 56");
						totalLending += lendingCursor.getLong(lendingCursor.getColumnIndex("Money"));
					}
					}
				} while (lendingCursor.moveToNext());
			}

			if (totalLending != 0) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

				reportDetail.addView(new ReportMainDetailCategory(this, "Cho Vay", totalLending, checkMonthly, startDate, endDate), params);
			}
		}

	}

	private void bindData() {
		// TODO Auto-generated method stub
		Log.d("report detail", "Check 1");
		Bundle extras = getIntent().getExtras();
		long scheduleID = extras.getLong("schedule_id");
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
