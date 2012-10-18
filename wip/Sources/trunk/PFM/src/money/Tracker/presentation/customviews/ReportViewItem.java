package money.Tracker.presentation.customviews;

import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ReportMainViewDetailActivity;
import money.Tracker.presentation.activities.ReportViewActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportViewItem extends LinearLayout{
	public TextView reportViewDate, reportViewSpentBudget;
	public LinearLayout reportStackedBarChart;
	
	public ReportViewItem(Context context, Date startDate, Date endDate, boolean checkMonthly) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_item, this, true);
		
		reportViewDate = (TextView) findViewById(R.id.report_view_date);
		reportViewSpentBudget = (TextView) findViewById(R.id.report_view_spent_buget);
		reportStackedBarChart = (LinearLayout) findViewById(R.id.report_stacked_bar_chart);
		
		if(checkMonthly)
			reportViewDate.setText(Converter.toString(startDate, "MMMM, yyyy"));
		else
			reportViewDate.setText(new StringBuilder(Converter.toString(startDate, "dd/MM/yyyy")).append(" - ").append(Converter.toString(endDate, "dd/MM/yyyy")));
		
		// get spent 
		double spent = 0;
		
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			Log.d("Check spent", "Check 1");
			if (entryExpenseCursor.moveToFirst()) {
				Log.d("Check spent", "Check 2");
				do {
					Log.d("Check spent", "Check 3");
					int id = entryExpenseCursor.getInt(entryExpenseCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor
							.getString(entryExpenseCursor.getColumnIndex("Date")));
					Log.d("Check spent", "Check 4");
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Log.d("Check spent", "Check 5 - " + id);
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						Log.d("Check spent", "Check 6");
						if (entryDetailCursor != null) {
							Log.d("Check spent", "Check 7");
							if (entryDetailCursor.moveToFirst()) {
								Log.d("Check spent", "Check 8");
								do {
									Log.d("Check spent", "Check 9");
									spent += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
									Log.d("Check spent", String.valueOf(spent));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		// get budget
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
		
		reportViewSpentBudget.setText("Spent/Budget:"
				+ Converter.toString(spent) + "/"
				+ Converter.toString(budget));
		
		View stackItem = new View(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
				LayoutParams.FILL_PARENT, Float.parseFloat(budget
						+ ""));
		Log.d("Report Adapter", "Check 18");
		stackItem.setBackgroundColor(Color.parseColor("#99FF0000"));
		reportStackedBarChart.addView(stackItem, params);
		Log.d("Report Adapter", "Check 19");

		View stackItem1 = new View(getContext());
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				0, LayoutParams.FILL_PARENT, Float.parseFloat(spent + ""));
		Log.d("Report Adapter", "Check 20");
		stackItem1.setBackgroundColor(Color.parseColor("#9900FFFF"));
		Log.d("Report Adapter", "Check 21");
		reportStackedBarChart.addView(stackItem1, params1);
		Log.d("Report Adapter", "Check 22");
		
		final boolean checkMonth = checkMonthly;
		final String sDate = Converter.toString(startDate);
		final String eDate = Converter.toString(endDate);
		
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent reportDetail = new Intent(getContext(),
						ReportMainViewDetailActivity.class);
				reportDetail.putExtra("checkMonthly", checkMonth);
				reportDetail.putExtra("start_date", sDate);
				reportDetail.putExtra("end_date", eDate);
				Log.d("Check click report", "Check finish");
				getContext().startActivity(reportDetail);
			}
		});
	}
	
}
