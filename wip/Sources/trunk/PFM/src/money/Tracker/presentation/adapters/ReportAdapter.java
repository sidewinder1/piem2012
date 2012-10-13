package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportViewItem;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.EntryRepository;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportAdapter extends ArrayAdapter<IModelBase> {
	private ArrayList<IModelBase> schedules;

	public ReportAdapter(Context context, int resource,
			ArrayList<IModelBase> scheduleObject) {
		super(context, resource, scheduleObject);
		// TODO Auto-generated constructor stub

		schedules = scheduleObject;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("Report Adapter", "Check 1");
		ReportViewItem reportItemView = (ReportViewItem) convertView;
		Log.d("Report Adapter", "Check 2");
		if (reportItemView == null) {
			reportItemView = new ReportViewItem(getContext());
		}
		Log.d("Report Adapter", "Check 3");
		final Schedule schedule = (Schedule) schedules.get(position);
		Log.d("Report Adapter", "Check 4");
		if (schedule != null) {
			// Set content to item title:
			final TextView reportDate = ((ReportViewItem) reportItemView).reportViewDate;
			Log.d("Report Adapter", "Check 5");
			if (schedule.type == 1) {
				Log.d("Report Adapter", "Check 6");
				reportDate.setText(DateFormat.format("MMMM yyyy",
						schedule.end_date));
			} else {
				Log.d("Report Adapter", "Check 7");
				reportDate.setText(new StringBuilder(DateFormat.format("dd/MM",
						schedule.start_date))
						.append("-")
						.append(DateFormat.format("dd/MM/yyyy",
								schedule.end_date)).toString());
			}
			Log.d("Report Adapter", "Check 8");
			// get expense
			Date startDate = schedule.start_date;
			Date endDate = schedule.end_date;
			Log.d("Report Adapter", "Check 9");
			EntryRepository enre = new EntryRepository();
			double money = 0;
			Log.d("Report Adapter", "Check 10");
			ArrayList<IModelBase> entryValues = enre.getData("");
			for (int i = 0; i < entryValues.size(); i++) {
				Entry entry = (Entry) entryValues.get(i);
				int id = entry.getId();
				if (entry.getDate().compareTo(startDate) > 0
						&& entry.getDate().compareTo(endDate) < 0
						|| entry.getDate().compareTo(endDate) == 0
						|| entry.getDate().compareTo(endDate) == 0) {
					Log.d("Report Adapter", "Check 11");
					Cursor entryDetailCursor = SqlHelper.instance.select(
							"EntryDetail", "*", "Entry_Id=" + id);
					Log.d("Report Adapter", "Check 12");
					if (entryDetailCursor != null) {
						if (entryDetailCursor.moveToFirst()) {
							do {
								Log.d("Report Adapter", "Check 13");
								money += entryDetailCursor
										.getDouble(entryDetailCursor
												.getColumnIndex("Money"));
							} while (entryDetailCursor.moveToNext());
						}
					}
				}
			}
			Log.d("Report Adapter", "Check 14");
			// Set content to budget
			final TextView reportSpentBudget = ((ReportViewItem) reportItemView).reportViewSpentBudget;
			Log.d("Report Adapter", "Check 15");
			reportSpentBudget.setText("Spent/Budget:"
					+ Converter.toString(schedule.budget) + "/"
					+ Converter.toString(money));
			Log.d("Report Adapter", "Check 16 - " + Converter.toString(money));
			reportItemView.reportStackedBarChart.removeAllViews();
			Log.d("Report Adapter", "Check 17");
			// Prepare and display stacked bar chart:
			View stackItem = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.FILL_PARENT, Float.parseFloat(schedule.budget
							+ ""));
			Log.d("Report Adapter", "Check 18");
			stackItem.setBackgroundColor(Color.parseColor("#99FF0000"));
			reportItemView.reportStackedBarChart.addView(stackItem, params);
			Log.d("Report Adapter", "Check 19");

			View stackItem1 = new View(getContext());
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					0, LayoutParams.FILL_PARENT, Float.parseFloat(money + ""));
			Log.d("Report Adapter", "Check 20");
			stackItem1.setBackgroundColor(Color.parseColor("#9900FFFF"));
			Log.d("Report Adapter", "Check 21");
			reportItemView.reportStackedBarChart.addView(stackItem1, params1);
			Log.d("Report Adapter", "Check 22");
		}
		Log.d("Report Adapter", "Check finish");
		return reportItemView;
	}
}
