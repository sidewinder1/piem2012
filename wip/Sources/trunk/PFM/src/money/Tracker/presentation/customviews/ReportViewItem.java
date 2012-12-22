package money.Tracker.presentation.customviews;

import java.util.Calendar;
import java.util.Date;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportViewItem extends LinearLayout {
	public TextView reportViewDate, reportViewSpentBudget;
	public LinearLayout reportStackedBarChart;

	public ReportViewItem(Context context, Date startDate, Date endDate, boolean checkMonthly) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_item, this, true);

		reportViewDate = (TextView) findViewById(R.id.report_view_date);
		reportViewSpentBudget = (TextView) findViewById(R.id.report_view_spent_buget);
		reportStackedBarChart = (LinearLayout) findViewById(R.id.report_stacked_bar_chart);

		if (checkMonthly)
			reportViewDate.setText(Converter.toString(startDate, "MM/ yyyy"));
		else
			reportViewDate.setText(new StringBuilder(Converter.toString(startDate, "dd/MM/yyyy")).append(" - ").append(Converter.toString(endDate, "dd/MM/yyyy")));

		// get spent
		long spent = 0;

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					
					Log.d("Check spent", String.valueOf(entryDate) + " - " + String.valueOf(startDate) + " - " + String.valueOf(endDate));
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						Log.d("Check spent", "Check 6");
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

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", whereCondition);
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
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");

						Calendar calScheduleStart = Calendar.getInstance();
						calScheduleStart.setTime(scheduleStartDate);
						int scheduleWeek = calScheduleStart.get(Calendar.WEEK_OF_MONTH);
						Calendar calStartDate = Calendar.getInstance();
						calStartDate.setTime(scheduleStartDate);
						int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (scheduleMonth.equals(startDateMonth) && scheduleWeek == startDateWeek)
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					}
				} while (scheduleCursor.moveToNext());
			}
		}

		reportViewSpentBudget.setText(getResources().getString(R.string.report_expense_schedule_stacked_bar_chart) + Converter.toString(spent) + "/" + Converter.toString(budget));

		if (budget - spent > 0) {
			View stackItem = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, Float.parseFloat(spent + ""));
			stackItem.setBackgroundColor(Color.YELLOW); // Color.parseColor("#FFD700")
			reportStackedBarChart.addView(stackItem, params);

			View stackItem1 = new View(getContext());
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, Float.parseFloat((budget - spent) + ""));
			stackItem1.setBackgroundColor(Color.GREEN); // Color.parseColor("#7CFC00")
			reportStackedBarChart.addView(stackItem1, params1);
		} else {
			View stackItem = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, Float.parseFloat(spent + ""));
			stackItem.setBackgroundColor(Color.RED);// Color.parseColor("(#8B0000")
			reportStackedBarChart.addView(stackItem, params);
		}
	}
}
