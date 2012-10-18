package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.customviews.ReportDetailProduct;
import money.Tracker.presentation.customviews.ReportViewItem;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.BorrowLendRepository;
import money.Tracker.repository.EntryRepository;
import money.Tracker.repository.ScheduleRepository;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReportViewActivity extends Activity {

	private boolean checkMonthly;
	private TextView displayNoReportDataText;
	private LinearLayout reportListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view);

		bindData();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	};

	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view,
				int position, long id) {
			int data_id = -1;
			Schedule schedule = (Schedule) listView.getAdapter().getItem(
					position);
			data_id = schedule.id;

			Intent reportDetail = new Intent(ReportViewActivity.this,
					ReportMainViewDetailActivity.class);
			reportDetail.putExtra("schedule_id", data_id);
			Log.d("Check click report", "Check finish");
			startActivity(reportDetail);
		}
	};

	private void bindData() {
		Bundle extras = getIntent().getExtras();
		checkMonthly = extras.getBoolean("Monthly");
		displayNoReportDataText = (TextView) findViewById(R.id.no_report_data);
		reportListView = (LinearLayout) findViewById(R.id.report_view_list_view);

		if (checkMonthly) {
			Cursor monthlyEntry = SqlHelper.instance
					.select("Entry",
							"DISTINCT strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry",
							"1=1 order by strftime('%Y', Date) DESC, strftime('%m', Date) DESC");
			if (monthlyEntry != null) {
				displayNoReportDataText.setVisibility(View.GONE);
				if (monthlyEntry.moveToFirst()) {
					do {
						String month = monthlyEntry.getString(monthlyEntry
								.getColumnIndex("monthEntry"));
						String year = monthlyEntry.getString(monthlyEntry
								.getColumnIndex("yearEntry"));
						Log.d("Check report view", monthlyEntry
								.getString(monthlyEntry
										.getColumnIndex("monthEntry")) + " - " + String.valueOf(month));
						Log.d("Check report view", monthlyEntry
								.getString(monthlyEntry
										.getColumnIndex("yearEntry")) + " - " + String.valueOf(year));

						Cursor entry = SqlHelper.instance
								.select("Entry",
										"*, strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry",
										"");
						Date startDate = null;
						Date endDate = null;

						if (entry != null) {
							if (entry.moveToFirst()) {
								do {
									Date entryDate = Converter.toDate(entry
											.getString(entry
													.getColumnIndex("Date")));
									String entryMonth = entry
											.getString(entry
													.getColumnIndex("monthEntry"));
									String entryYear = entry
											.getString(entry
													.getColumnIndex("yearEntry"));
									
									Log.d("Check report view", "entryMonth - " + entryMonth);
									Log.d("Check report view", "entryYear - " + entryYear);
									if (entryMonth.equals(month)
											&& entryYear.equals(year)) {
										if (startDate == null) {
											startDate = entryDate;
										} else if (endDate == null) {
											if (startDate.compareTo(entryDate) < 0) {
												endDate = entryDate;
											} else {
												endDate = startDate;
												startDate = entryDate;
											}
										} else {
											if (startDate.compareTo(entryDate) < 0) {
												endDate = entryDate;
											} else {
												endDate = startDate;
												startDate = entryDate;
											}
										}
									}
								} while (entry.moveToNext());
							}
						}
						
						Log.d("Check report view", "Start date - " + Converter.toString(startDate, "dd/MM/yyyy"));
						if(endDate != null)
							Log.d("Check report view", "End date - " + Converter.toString(endDate, "dd/MM/yyyy"));
						else
						{
							Log.d("Check report view", "End date - null");
							endDate = startDate;
						}
						
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						
						reportListView.addView(new ReportViewItem(this, startDate, endDate, checkMonthly), params);
					} while (monthlyEntry.moveToNext());
				}
			}

		} else
		{
			Cursor weekEntry = SqlHelper.instance
					.select("Entry",
							"DISTINCT strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry",
							"1=1 order by strftime('%Y', Date) DESC, strftime('%W', Date) DESC");
			if (weekEntry != null) {
				displayNoReportDataText.setVisibility(View.GONE);
				if (weekEntry.moveToFirst()) {
					do {
						String week = weekEntry.getString(weekEntry
								.getColumnIndex("weekEntry"));
						String year = weekEntry.getString(weekEntry
								.getColumnIndex("yearEntry"));
						Log.d("Check report view", weekEntry
								.getString(weekEntry
										.getColumnIndex("weekEntry")) + " - " + String.valueOf(week));
						Log.d("Check report view", weekEntry
								.getString(weekEntry
										.getColumnIndex("yearEntry")) + " - " + String.valueOf(year));

						Cursor entry = SqlHelper.instance
								.select("Entry",
										"*, strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry",
										"");
						Date startDate = null;
						Date endDate = null;

						if (entry != null) {
							if (entry.moveToFirst()) {
								do {
									Date entryDate = Converter.toDate(entry
											.getString(entry
													.getColumnIndex("Date")));
									String entryWeek = entry
											.getString(entry
													.getColumnIndex("weekEntry"));
									String entryYear = entry
											.getString(entry
													.getColumnIndex("yearEntry"));
									
									Log.d("Check report view", "entryMonth - " + entryWeek);
									Log.d("Check report view", "entryYear - " + entryYear);
									if (entryWeek.equals(week)
											&& entryYear.equals(year)) {
										if (startDate == null) {
											startDate = entryDate;
										} else if (endDate == null) {
											if (startDate.compareTo(entryDate) < 0) {
												endDate = entryDate;
											} else {
												endDate = startDate;
												startDate = entryDate;
											}
										} else {
											if (startDate.compareTo(entryDate) < 0) {
												endDate = entryDate;
											} else {
												endDate = startDate;
												startDate = entryDate;
											}
										}
									}
								} while (entry.moveToNext());
							}
						}
						
						Log.d("Check report view", "Start date - " + Converter.toString(startDate, "dd/MM/yyyy"));
						if(endDate != null)
							Log.d("Check report view", "End date - " + Converter.toString(endDate, "dd/MM/yyyy"));
						else
						{
							Log.d("Check report view", "End date - null");
							endDate = startDate;
						}
						
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
						
						reportListView.addView(new ReportViewItem(this, startDate, endDate, checkMonthly), params);
					} while (weekEntry.moveToNext());
				}
			}
		}

		// sort();
	}

	/*
	 * private void bindChartLegend() { // Bind chart legend: Cursor category =
	 * SqlHelper.instance .query(new StringBuilder(
	 * "SELECT DISTINCT Category.Name, Category.User_Color ")
	 * .append("FROM Category ") .append("INNER JOIN ScheduleDetail ")
	 * .append("ON Category.Id=ScheduleDetail.Category_Id") .toString());
	 * 
	 * if (category != null && category.moveToFirst()) { int index = 1;
	 * chart_legend.removeAllViews(); LinearLayout itemView = new
	 * LinearLayout(this); do { CategoryLegendItemView item = new
	 * CategoryLegendItemView(this); item.setName(category.getString(0));
	 * item.setColor(category.getString(1)); LinearLayout.LayoutParams params =
	 * new LinearLayout.LayoutParams( 0, LayoutParams.FILL_PARENT, 1);
	 * itemView.addView(item, params);
	 * 
	 * if ((index % 2 == 0) || index == category.getCount()) {
	 * chart_legend.addView(itemView); itemView = new LinearLayout(this); }
	 * 
	 * index++; } while (category.moveToNext()); } }
	 */

	/*
	 * private void sort() { int i, j; int length = values.size(); IModelBase t;
	 * for (i = 0; i < length; i++) { for (j = 1; j < (length - i); j++) { if
	 * ((values.get(j - 1)).compareTo(values.get(j)) < 0) { t = values.get(j -
	 * 1); values.set(j - 1, values.get(j)); values.set(j, t); } } }
	 * 
	 * }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_report_view, menu);
		return true;
	}
}
