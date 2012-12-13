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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class ReportViewActivity extends Activity {

	private boolean checkMonthly;
	private TextView displayNoReportDataText;
	private LinearLayout reportListView;
	private Context myContext = this;

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
						ReportViewItem monthReportViewItem = new ReportViewItem(this.getApplicationContext(), startDate, endDate, checkMonthly);
						reportListView.addView(monthReportViewItem, params);
						
						monthReportViewItem.setOnClickListener(new OnClickListener() {
							
							public void onClick(View v) {
								// TODO Auto-generated method stub
								//onItemClick();
								final Dialog dialog = new Dialog(getParent());
								//TextView textView = new TextView(myContext);
								//textView.setText("test");
								//dialog.setContentView(textView);
								dialog.setContentView(R.layout.report_view_chart_custom_dialog);
								// TODO: chuyen het tat ca hardcode string den strings.xml.
								dialog.setTitle(getResources().getString(R.string.report_select_chart));

								dialog.show();
							}
						});
						
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
						ReportViewItem weekReportViewItem = new ReportViewItem(this, startDate, endDate, checkMonthly);
						reportListView.addView(weekReportViewItem, params);
					} while (weekEntry.moveToNext());
				}
			}
		}
	}
	
	private void onItemClick()
	{
		Log.d("Check report view", "Check on click 1");
		final Dialog dialog = new Dialog(getParent());
		//TextView textView = new TextView(myContext);
		//textView.setText("test");
		//dialog.setContentView(textView);
		dialog.setContentView(R.layout.report_view_chart_custom_dialog);
		// TODO: chuyen het tat ca hardcode string den strings.xml.
		dialog.setTitle(getResources().getString(R.string.dummy_data));

		dialog.show();
		
		/*
		 * final CheckBox reportInMonthWeekCheckBox = (CheckBox)
		 * dialog.findViewById(R.id.report_in_month_week_checkbox);
		 * TextView reportInMonthWeekTextView = (TextView)
		 * dialog.findViewById(R.id.report_in_month_week_text_view);
		 * 
		 * final CheckBox reportBetweenMonthWeekCheckBox = (CheckBox)
		 * dialog.findViewById(R.id.report_betwwen_month_week_checkbox);
		 * TextView reportBetweenMonthWeekTextView = (TextView)
		 * dialog.findViewById(R.id.report_betwwen_month_week_textview);
		 * 
		 * Log.d("Check report view", "Check on click 2");
		 * 
		 * if (checkMonth) {
		 * reportBetweenMonthWeekTextView.setText(R.string
		 * .report_between_month);
		 * reportInMonthWeekTextView.setText(R.string.report_in_month);
		 * } else { reportBetweenMonthWeekTextView.setText(R.string.
		 * report_between_week);
		 * reportInMonthWeekTextView.setText(R.string.report_in_week); }
		 * 
		 * Log.d("Check report view", "Check on click 3");
		 * 
		 * reportInMonthWeekCheckBox.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() { public void
		 * onCheckedChanged(CompoundButton buttonView, boolean
		 * isChecked) { // TODO Auto-generated method stub if
		 * (isChecked) { checked = 1;
		 * reportBetweenMonthWeekCheckBox.setChecked(false); } else {
		 * checked = 0; } } });
		 * 
		 * reportBetweenMonthWeekCheckBox.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() { public void
		 * onCheckedChanged(CompoundButton buttonView, boolean
		 * isChecked) { // TODO Auto-generated method stub if
		 * (isChecked) { checked = 2;
		 * reportInMonthWeekCheckBox.setChecked(false); } else { checked
		 * = 0; } } });
		 */

		/*
		 * Log.d("Check report view", "Check on click 4"); Button
		 * okButton = (Button)
		 * dialog.findViewById(R.id.report_custom_dialog_ok_button);
		 * Button cancelButton = (Button)
		 * dialog.findViewById(R.id.report_custom_dialog_cancel_button);
		 * 
		 * Log.d("Check report view", "Check on click 5");
		 * cancelButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View v) { // TODO Auto-generated method
		 * stub checked = 0; dialog.dismiss(); } });
		 * 
		 * final Context context = ReportViewItem.this.getContext();
		 * Log.d("Check report view", "Check on click 6");
		 * okButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View v) { // TODO Auto-generated method
		 * stub switch (checked) { case 1: Intent pieChart = new
		 * Intent(context, ReportViewPieChartActivity.class);
		 * pieChart.putExtra("checkMonthly", checkMonth);
		 * pieChart.putExtra("start_date", sDate);
		 * pieChart.putExtra("end_date", eDate);
		 * 
		 * checked = 0; dialog.dismiss();
		 * 
		 * break;
		 * 
		 * case 2: /* final Dialog compareDialog = new
		 * Dialog(ReportViewItem.this.getContext());
		 * 
		 * compareDialog.setContentView(R.layout.
		 * report_view_chart_compare_custom_dialog);
		 * 
		 * if (checkMonth) compareDialog.setTitle("nh");
		 * else compareDialog.setTitle("Cnh");
		 * 
		 * barChartListDate = (LinearLayout)
		 * compareDialog.findViewById(R
		 * .id.report_compare_custom_dialog_list_date_view);
		 * barChartListDate.removeAllViews();
		 * bindDataCustomItemView(false);
		 * 
		 * Button okButton = (Button) compareDialog.findViewById(R.id.
		 * report_compare_custom_dialog_ok_button); Button cancelButton
		 * = (Button) compareDialog.findViewById(R.id.
		 * report_compare_custom_dialog_cancel_button);
		 * 
		 * cancelButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View v) { // TODO Auto-generated method
		 * stub int size = dateList.size();
		 * 
		 * for (int i = 1; i < size; i++) { dateList.remove(1); }
		 * 
		 * compareDialog.dismiss(); } });
		 * 
		 * okButton.setOnClickListener(new View.OnClickListener() {
		 * 
		 * public void onClick(View v) { // TODO Auto-generated method
		 * stub int size = dateList.size(); for (int i = 1; i < size;
		 * i++) { dateList.remove(1); Log.d("Check remove dateList",
		 * (dateList.size() + " - " + i)); } compareDialog.dismiss(); }
		 * });
		 * 
		 * Log.d("Check report view", "Check on click 7"); final
		 * CheckBox checkAllCheckBox = (CheckBox)
		 * compareDialog.findViewById
		 * (R.id.report_compare_custom_dialog_check_all_checkbox); final
		 * CheckBox uncheckAllCheckBox = (CheckBox)
		 * compareDialog.findViewById
		 * (R.id.report_compare_custom_dialog_uncheck_all_checkbox);
		 * 
		 * checkAllCheckBox.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() { public void
		 * onCheckedChanged(CompoundButton buttonView, boolean
		 * isChecked) { // TODO Auto-generated method stub if
		 * (isChecked) { int size = dateList.size(); for (int i = 1; i <
		 * size; i++) { dateList.remove(1);
		 * Log.d("Check remove dateList", (dateList.size() + " - " +
		 * i)); }
		 * 
		 * barChartListDate.removeAllViews();
		 * bindDataCustomItemView(true);
		 * 
		 * uncheckAllCheckBox.setChecked(false); } } });
		 * 
		 * uncheckAllCheckBox.setOnCheckedChangeListener(new
		 * OnCheckedChangeListener() { public void
		 * onCheckedChanged(CompoundButton buttonView, boolean
		 * isChecked) { // TODO Auto-generated method stub if
		 * (isChecked) { int size = dateList.size(); for (int i = 1; i <
		 * size; i++) { dateList.remove(1); }
		 * 
		 * barChartListDate.removeAllViews();
		 * bindDataCustomItemView(false);
		 * 
		 * checkAllCheckBox.setChecked(false); } } });
		 * 
		 * compareDialog.show();
		 * 
		 * checked = 0; dialog.dismiss();
		 * 
		 * break;
		 * 
		 * default: checked = 0; dialog.dismiss();
		 * 
		 * break; } } });
		 */
	}
}
