package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportBarChartViewDetailItemView;
import android.os.Bundle;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewBarChartActivity extends BaseActivity {

	private Date mStartDate;
	private Date mEndDate;
	private boolean mCheckMonthly;
	private LinearLayout mBarChart;
	private List<Date[]> mDateList;
	private List<Double> mEntryCategoryValue;
	private List<Double> mScheduleCategoryValue;
	private List<String> mDateListString;
	private long mMaxValue;
	private String mMaxDate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_bar_chart);

		Bundle extras = getIntent().getExtras();
		mCheckMonthly = extras.getBoolean("checkMonthly");
		// startDate = Converter.toDate(extras.getString("start_date"));
		// endDate = Converter.toDate(extras.getString("end_date"));
		mDateList = new ArrayList<Date[]>();
		int size = extras.getInt("Size_List");
		for (int i = 0; i < size; i++)
		{
			Date sDate = Converter.toDate(extras.getString("start_date_" + i ));
			Date eDate = Converter.toDate(extras.getString("end_date_" + i ));
			mDateList.add(new Date[] {sDate, eDate});
		}
		
		TextView barChartTitle = (TextView) findViewById(R.id.report_bar_chart_title_text_view);
		if (mCheckMonthly)
		{
			barChartTitle.setText(getResources().getString(R.string.report_bar_chart_month_title));
		} else
		{
			barChartTitle.setText(getResources().getString(R.string.report_bar_chart_week_title));
		}
		mBarChart = (LinearLayout) findViewById(R.id.report_bar_chart);
		
		mEntryCategoryValue = new ArrayList<Double>();
		mScheduleCategoryValue = new ArrayList<Double>();
		mDateListString = new ArrayList<String>();
		mMaxValue = 0;
		mMaxDate = "";
		
		
		if (mDateList.size() > 1)
		{
			Chart chart = new Chart();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mBarChart.removeAllViews();
			mBarChart.addView(chart.getBarCompareIntent(this, mCheckMonthly, mDateList), params);
		}
		
		for(int i = 0; i < mDateList.size(); i++)
		{
			Date [] compareDate = mDateList.get(i);
			this.mStartDate = compareDate[0];
			this.mEndDate = compareDate[1];
			
			getData();
		}
		
		LinearLayout reportExpenseLegendColor = (LinearLayout) findViewById(R.id.report_bar_chart_expense_legend_color);
		LinearLayout reportScheduleLegendColor = (LinearLayout) findViewById(R.id.report_bar_chart_schedule_legend_color);
		
		reportExpenseLegendColor.setBackgroundColor(Color.YELLOW);
		reportScheduleLegendColor.setBackgroundColor(Color.GREEN);
		
		LinearLayout barViewDetail = (LinearLayout) findViewById(R.id.report_bar_chart_detail);
		barViewDetail.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this, getResources().getString(R.string.report_bar_chart_max_value), ""), params);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this, Converter.toString(mMaxValue), mMaxDate), params);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this, "", ""), params);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this.getApplicationContext(), getResources().getString(R.string.report_bar_chart_expense_schedule), ""), params);
		
		for (int i = 0; i < mDateList.size(); i++)
		{
			barViewDetail.addView(new ReportBarChartViewDetailItemView(this.getApplicationContext(), Converter.toString(mScheduleCategoryValue.get(i) - mEntryCategoryValue.get(i)), mDateListString.get(i)), params);
		}
	}
	
	private void getData()
	{
		// get spent
		long spent = 0;

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(mStartDate) > 0 && entryDate.compareTo(mEndDate) < 0
							|| entryDate.compareTo(mStartDate) == 0
							|| entryDate.compareTo(mEndDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									spent += entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}

		// get budget
		long budget = 0;
		String whereCondition = "";
		if (mCheckMonthly)
			whereCondition = "Type = 1";
		else
			whereCondition = "Type = 0";

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*",whereCondition);
		if (scheduleCursor != null) {
			if (scheduleCursor.moveToFirst()) {
				do {
					if (mCheckMonthly) {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(mStartDate,"MM");

						if (scheduleMonth.equals(startDateMonth))
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					} else {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "yyyy");
						String startDateMonth = Converter.toString(mStartDate,"yyyy");

						Calendar calScheduleStart = Calendar.getInstance();
						calScheduleStart.setTime(scheduleStartDate);
						int scheduleWeek = calScheduleStart.get(Calendar.WEEK_OF_YEAR);
						Calendar calStartDate = Calendar.getInstance();
						calStartDate.setTime(scheduleStartDate);
						int startDateWeek = calStartDate.get(Calendar.WEEK_OF_YEAR);

						if (scheduleMonth.equals(startDateMonth) && scheduleWeek == startDateWeek)
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					}
				} while (scheduleCursor.moveToNext());
			}
		}
		
		mEntryCategoryValue.add((double) spent);
		mScheduleCategoryValue.add((double) budget);
		String dateString = "";
			if (mCheckMonthly)
				dateString = Converter.toString(mStartDate, "MM/ yyyy");
			else
				dateString = String.valueOf(new StringBuilder(Converter.toString(mStartDate, "dd")).append(" - ").append(Converter.toString(mEndDate, "dd/MM/yyyy")));
		mDateListString.add(dateString);
		
		if (spent > mMaxValue)
		{
			mMaxValue = spent;
			mMaxDate = dateString;
		}
	}
}
