package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportPieCategoryLegendItemView;
import android.os.Bundle;
import android.database.Cursor;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewPieChartActivity extends BaseActivity {

	private Date mStartDate;
	private Date mEndDate;
	private boolean mCheckMonthly;
	private long mTotalExpense = 0;
	private List<String> mEntryCategoryName;
	private List<Long> mEntryCategoryValue;
	private List<String> mEntryCategoryColor;
	private List<Long> mCategoryIDList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_pie_chart);

		Bundle extras = getIntent().getExtras();
		mCheckMonthly = extras.getBoolean("checkMonthly");
		mStartDate = Converter.toDate(extras.getString("start_date"));
		mEndDate = Converter.toDate(extras.getString("end_date"));
		
		mEntryCategoryName = new ArrayList<String>();
		mEntryCategoryValue = new ArrayList<Long>();
		mEntryCategoryColor = new ArrayList<String>();
		mCategoryIDList = new ArrayList<Long>();
		
		LinearLayout pieChart = (LinearLayout) findViewById(R.id.report_pie_chart);
		TextView totalMoneyTextView = (TextView) findViewById(R.id.report_pie_chart_total_money);
		LinearLayout pieChartLegend = (LinearLayout) findViewById(R.id.report_pie_chart_legend);
		TextView titlePieChart = (TextView) findViewById(R.id.report_pie_chart_title_text_view);
		
		if (mCheckMonthly)
			titlePieChart.setText(getResources().getString(R.string.report_in_month) + " " + Converter.toString(mStartDate, "MM"));
		else
		{
			//titlePieChart.setText(getResources().getString(R.string.report_in_week) + " " + new StringBuilder(Converter.toString(startDate, "dd/MM/yyyy")).append(" - ").append(Converter.toString(endDate, "dd/MM/yyyy")));
			titlePieChart.setText(new StringBuilder(Converter.toString(mStartDate, "dd/MM/yyyy")).append(" - ").append(Converter.toString(mEndDate, "dd/MM/yyyy")));
			//titlePieChart.setTextSize(15);
		}

		getData();
		totalMoneyTextView.setText(Converter.toString(mTotalExpense));
	
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Chart chart = new Chart();
		pieChart.addView(chart.getPieIntent(this.getApplicationContext(), mCheckMonthly, mStartDate, mEndDate), params);
		
		for(int i = 0; i < mEntryCategoryColor.size(); i++)
		{
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			Log.d("Check pie chart legend detail", mEntryCategoryColor.get(i));
			Log.d("Check pie chart legend detail", mEntryCategoryName.get(i));
			Log.d("Check pie chart legend detail", "" + mEntryCategoryValue.get(i));
			Log.d("Check pie chart legend detail", "" + mTotalExpense); 
			Log.d("Check pie chart legend detail", "" + mCategoryIDList.get(i));
			Log.d("Check pie chart legend detail", String.valueOf(mStartDate));
			Log.d("Check pie chart legend detail", String.valueOf(mEndDate));
			pieChartLegend.addView(new ReportPieCategoryLegendItemView(this.getApplicationContext(), mEntryCategoryColor.get(i), mEntryCategoryName.get(i), mEntryCategoryValue.get(i), mTotalExpense, mCategoryIDList.get(i), mStartDate, mEndDate), params1);
		}
	}
	
	private void getData() {
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					
					Log.d("Check pie chart legend", String.valueOf(entryDate) + " - " + String.valueOf(mStartDate) + " - " + String.valueOf(mEndDate));
					if (entryDate.compareTo(mStartDate) > 0 && entryDate.compareTo(mEndDate) < 0
							|| entryDate.compareTo(mStartDate) == 0
							|| entryDate.compareTo(mEndDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									String name = "";
									long value = 0;
									String color = "";

									long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
									Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
									if (categoryCursor != null) {
										if (categoryCursor.moveToFirst()) {
											do {
												name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												color = categoryCursor.getString(categoryCursor.getColumnIndex("User_Color"));
											} while (categoryCursor.moveToNext());
										}
									}

									value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));
									
									Log.d("Check pie chart legend", categoryID + " - " + name + " - " + value + " - " + color);
									if (!mEntryCategoryName.isEmpty()) {

										boolean check = false;

										for (int i = 0; i < mEntryCategoryName.size(); i++) {
											if (mEntryCategoryName.get(i).equals(name)) {
												mEntryCategoryValue.set(i, mEntryCategoryValue.get(i) + value);
												check = true;
											}
										}

										if (check == false) {
											mCategoryIDList.add(categoryID);
											mEntryCategoryName.add(name);
											mEntryCategoryValue.add(value);
											mEntryCategoryColor.add(color);
										}

									} else {
										mCategoryIDList.add(categoryID);
										mEntryCategoryName.add(name);
										mEntryCategoryValue.add(value);
										mEntryCategoryColor.add(color);
									}
									
									mTotalExpense += value;

								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
	}
}
