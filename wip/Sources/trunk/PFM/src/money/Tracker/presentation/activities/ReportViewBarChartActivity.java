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
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewBarChartActivity extends BaseActivity {

	private Date startDate;
	private Date endDate;
	private boolean checkMonthly;
	private LinearLayout barChart;
	private List<Date[]> dateList;
	private List<Double> entryCategoryValue;
	private List<Double> scheduleCategoryValue;
	private List<String> dateListString;
	private long maxValue;
	private String maxDate;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_bar_chart);

		Bundle extras = getIntent().getExtras();
		checkMonthly = extras.getBoolean("checkMonthly");
		// startDate = Converter.toDate(extras.getString("start_date"));
		// endDate = Converter.toDate(extras.getString("end_date"));
		dateList = new ArrayList<Date[]>();
		int size = extras.getInt("Size_List");
		for (int i = 0; i < size; i++)
		{
			Date sDate = Converter.toDate(extras.getString("start_date_" + i ));
			Date eDate = Converter.toDate(extras.getString("end_date_" + i ));
			dateList.add(new Date[] {sDate, eDate});
		}
		
		TextView barChartTitle = (TextView) findViewById(R.id.report_bar_chart_title_text_view);
		if (checkMonthly)
		{
			barChartTitle.setText(getResources().getString(R.string.report_bar_chart_month_title));
		} else
		{
			barChartTitle.setText(getResources().getString(R.string.report_bar_chart_week_title));
		}
		barChart = (LinearLayout) findViewById(R.id.report_bar_chart);
		
		entryCategoryValue = new ArrayList<Double>();
		scheduleCategoryValue = new ArrayList<Double>();
		dateListString = new ArrayList<String>();
		maxValue = 0;
		maxDate = "";
		
		
		if (dateList.size() > 1)
		{
			Chart chart = new Chart();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			barChart.removeAllViews();
			barChart.addView(chart.getBarCompareIntent(this, checkMonthly, dateList), params);
		}
		
		for(int i = 0; i < dateList.size(); i++)
		{
			Date [] compareDate = dateList.get(i);
			this.startDate = compareDate[0];
			this.endDate = compareDate[1];
			
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
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this, Converter.toString(maxValue), maxDate), params);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this, "", ""), params);
		barViewDetail.addView(new ReportBarChartViewDetailItemView(this.getApplicationContext(), getResources().getString(R.string.report_bar_chart_expense_schedule), ""), params);
		
		for (int i = 0; i < dateList.size(); i++)
		{
			barViewDetail.addView(new ReportBarChartViewDetailItemView(this.getApplicationContext(), Converter.toString(scheduleCategoryValue.get(i) - entryCategoryValue.get(i)), dateListString.get(i)), params);
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
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
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
						String startDateMonth = Converter.toString(startDate,"MM");

						if (scheduleMonth.equals(startDateMonth))
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					} else {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "yyyy");
						String startDateMonth = Converter.toString(startDate,"yyyy");

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
		
		entryCategoryValue.add((double) spent);
		scheduleCategoryValue.add((double) budget);
		String dateString = "";
			if (checkMonthly)
				dateString = Converter.toString(startDate, "MM/ yyyy");
			else
				dateString = String.valueOf(new StringBuilder(Converter.toString(startDate, "dd")).append(" - ").append(Converter.toString(endDate, "dd/MM/yyyy")));
		dateListString.add(dateString);
		
		if (spent > maxValue)
		{
			maxValue = spent;
			maxDate = dateString;
		}
	}
}
