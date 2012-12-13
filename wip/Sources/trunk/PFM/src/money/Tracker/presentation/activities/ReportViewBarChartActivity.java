package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportCustomDialogViewItem;
import money.Tracker.presentation.customviews.ReportViewItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewBarChartActivity extends Activity {

	private Date startDate;
	private Date endDate;
	private boolean checkMonthly;
	private LinearLayout barChart;
	private List<Date[]> dateList;
	private List<Double> entryCategoryValue;
	private List<Double> scheduleCategoryValue;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_bar_chart);

		Bundle extras = getIntent().getExtras();
		checkMonthly = extras.getBoolean("checkMonthly");
		startDate = Converter.toDate(extras.getString("start_date"));
		endDate = Converter.toDate(extras.getString("end_date"));
		barChart = (LinearLayout) findViewById(R.id.report_bar_chart);
		
		entryCategoryValue = new ArrayList<Double>();
		scheduleCategoryValue = new ArrayList<Double>();
		
		if (dateList.size() > 1)
		{
			Chart chart = new Chart();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
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
		
		for (int i = 0; i < dateList.size(); i++)
		{
			
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
						Date scheduleEndDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("End_date")));

						if ((scheduleStartDate.compareTo(startDate) > 0 && scheduleEndDate.compareTo(endDate) < 0)
								|| (scheduleStartDate.compareTo(startDate) == 0 && scheduleEndDate.compareTo(endDate) == 0)
								|| (scheduleStartDate.compareTo(startDate) > 0 && scheduleEndDate.compareTo(endDate) == 0)
								|| (scheduleStartDate.compareTo(startDate) == 0 && scheduleEndDate.compareTo(endDate) < 0))
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					}
				} while (scheduleCursor.moveToNext());
			}
		}
		
		entryCategoryValue.add((double) spent);
		scheduleCategoryValue.add((double) budget);
	}
}
