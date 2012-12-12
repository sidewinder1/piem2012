package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.Logger;
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
import android.app.Dialog;
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
		try {
			bindData();
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "ReportViewActivity");
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	};

	private void bindData() {
		Bundle extras = getIntent().getExtras();
		checkMonthly = extras.getBoolean("Monthly");
		displayNoReportDataText = (TextView) findViewById(R.id.no_report_data);
		reportListView = (LinearLayout) findViewById(R.id.report_view_list_view);
		reportListView.removeAllViews();

		if (checkMonthly) {
			Cursor monthlyEntry = SqlHelper.instance.select("Entry", "DISTINCT strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry", "1=1 order by strftime('%Y', Date) DESC, strftime('%m', Date) DESC");
			if (monthlyEntry != null) {
				displayNoReportDataText.setVisibility(View.GONE);
				if (monthlyEntry.moveToFirst()) {
					do {
						String month = monthlyEntry.getString(monthlyEntry.getColumnIndex("monthEntry"));
						String year = monthlyEntry.getString(monthlyEntry.getColumnIndex("yearEntry"));

						Cursor entry = SqlHelper.instance.select("Entry", "*, strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry", "");
						Date startDate = null;
						Date endDate = null;

						if (entry != null) {
							if (entry.moveToFirst()) {
								do {
									Date entryDate = Converter.toDate(entry.getString(entry.getColumnIndex("Date")));
									String entryMonth = entry.getString(entry.getColumnIndex("monthEntry"));
									String entryYear = entry.getString(entry.getColumnIndex("yearEntry"));

									if (entryMonth.equals(month) && entryYear.equals(year)) {
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

						if (endDate == null) {
							endDate = startDate;
						}

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

						reportListView.addView(new ReportViewItem(this, startDate, endDate, checkMonthly), params);
					} while (monthlyEntry.moveToNext());
				}
			}

		} else {
			Cursor weekEntry = SqlHelper.instance.select("Entry", "DISTINCT strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry", "1=1 order by strftime('%Y', Date) DESC, strftime('%W', Date) DESC");
			if (weekEntry != null) {
				displayNoReportDataText.setVisibility(View.GONE);
				if (weekEntry.moveToFirst()) {
					do {
						String week = weekEntry.getString(weekEntry.getColumnIndex("weekEntry"));
						String year = weekEntry.getString(weekEntry.getColumnIndex("yearEntry"));

						Cursor entry = SqlHelper.instance.select("Entry", "*, strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry", "");
						Date startDate = null;
						Date endDate = null;

						if (entry != null) {
							if (entry.moveToFirst()) {
								do {
									Date entryDate = Converter.toDate(entry.getString(entry.getColumnIndex("Date")));
									String entryWeek = entry.getString(entry.getColumnIndex("weekEntry"));
									String entryYear = entry.getString(entry.getColumnIndex("yearEntry"));

									if (entryWeek.equals(week) && entryYear.equals(year)) {
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

						if (endDate == null) {
							endDate = startDate;
						}

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);

						reportListView.addView(new ReportViewItem(this, startDate, endDate, checkMonthly), params);
					} while (weekEntry.moveToNext());
				}
			}
		}
	}

}
