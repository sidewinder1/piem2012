package money.Tracker.presentation.customviews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ReportViewActivity;
import money.Tracker.presentation.activities.ReportViewBarChartActivity;
import money.Tracker.presentation.activities.ReportViewPieChartActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ReportViewItem extends LinearLayout {
	public TextView reportViewDate, reportViewSpentBudget;
	public LinearLayout reportStackedBarChart;
	private int checked = 0;
	private LinearLayout barChartListDate;
	private List<Date[]> dateList;
	final boolean checkMonth;
	final private Date _startDate;
	final private Date _endDate;

	public ReportViewItem(final Context context, Date startDate, Date endDate,
			boolean checkMonthly) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_item, this, true);

		_startDate = startDate;
		_endDate = endDate;

		dateList = new ArrayList<Date[]>();
		dateList.add(new Date[] { _startDate, _endDate });

		reportViewDate = (TextView) findViewById(R.id.report_view_date);
		reportViewSpentBudget = (TextView) findViewById(R.id.report_view_spent_buget);
		reportStackedBarChart = (LinearLayout) findViewById(R.id.report_stacked_bar_chart);

		if (checkMonthly)
			reportViewDate.setText(Converter.toString(startDate, "MM/ yyyy"));
		else
			reportViewDate.setText(new StringBuilder(Converter.toString(
					startDate, "dd/MM/yyyy")).append(" - ").append(
					Converter.toString(endDate, "dd/MM/yyyy")));

		// get spent
		long spent = 0;

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*",
				"Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor
							.getString(entryExpenseCursor
									.getColumnIndex("Date")));

					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail", "*", "Entry_Id=" + id);
						Log.d("Check spent", "Check 6");
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									spent += entryDetailCursor
											.getLong(entryDetailCursor
													.getColumnIndex("Money"));
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

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*",
				whereCondition);
		if (scheduleCursor != null) {
			if (scheduleCursor.moveToFirst()) {
				do {
					if (checkMonthly) {
						Date scheduleStartDate = Converter
								.toDate(scheduleCursor.getString(scheduleCursor
										.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(
								scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate,
								"MM");

						if (scheduleMonth.equals(startDateMonth))
							budget = scheduleCursor.getLong(scheduleCursor
									.getColumnIndex("Budget"));
					} else {
						Date scheduleStartDate = Converter
								.toDate(scheduleCursor.getString(scheduleCursor
										.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(
								scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate,
								"MM");

						Calendar calScheduleStart = Calendar.getInstance();
						calScheduleStart.setTime(scheduleStartDate);
						int scheduleWeek = calScheduleStart
								.get(Calendar.WEEK_OF_MONTH);
						Calendar calStartDate = Calendar.getInstance();
						calStartDate.setTime(scheduleStartDate);
						int startDateWeek = calStartDate
								.get(Calendar.WEEK_OF_MONTH);

						if (scheduleMonth.equals(startDateMonth)
								&& scheduleWeek == startDateWeek)
							budget = scheduleCursor.getLong(scheduleCursor
									.getColumnIndex("Budget"));
					}
				} while (scheduleCursor.moveToNext());
			}
		}

		reportViewSpentBudget.setText("Thực tế/Kế hoạch: "
				+ Converter.toString(spent) + "/" + Converter.toString(budget));

		if (budget - spent > 0) {
			View stackItem = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.FILL_PARENT, Float.parseFloat(spent + ""));
			stackItem.setBackgroundColor(Color.YELLOW); // Color.parseColor("#FFD700")
			reportStackedBarChart.addView(stackItem, params);

			View stackItem1 = new View(getContext());
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					0, LayoutParams.FILL_PARENT,
					Float.parseFloat((budget - spent) + ""));
			stackItem1.setBackgroundColor(Color.GREEN); // Color.parseColor("#7CFC00")
			reportStackedBarChart.addView(stackItem1, params1);
		} else {
			View stackItem = new View(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
					LayoutParams.FILL_PARENT, Float.parseFloat(spent + ""));
			stackItem.setBackgroundColor(Color.RED);// Color.parseColor("(#8B0000")
			reportStackedBarChart.addView(stackItem, params);
		}

		checkMonth = checkMonthly;
		final String sDate = Converter.toString(startDate);
		final String eDate = Converter.toString(endDate);

		Log.d("Check report view", "Check on click 0");
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log.d("Check report view", "Check on click 1");
//				final Dialog dialog = new Dialog(v.getContext());
				TextView textView = new TextView(PfmApplication.getAppContext());
				textView.setText("test");
				//dialog.setContentView(textView);
				// dialog.setContentView(R.layout.report_view_chart_custom_dialog);
				// TODO: chuyen het tat ca hardcode string den strings.xml.
//				dialog.setTitle("Thống kê so sánh");

//				dialog.show();
				new AlertDialog.Builder(PfmApplication.getAppContext()).setTitle("asdas").setView(textView).
				setPositiveButton(
						getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface arg0,
									int arg1) {
								
							}
						})
				.setNegativeButton(
						getResources().getString(
								R.string.cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0,
									int arg1) {
								
							}
						}).show();
				
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
				 * if (checkMonth) compareDialog.setTitle("Chọn tháng so sánh");
				 * else compareDialog.setTitle("Chọn tuần so sánh");
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
				Log.d("Check report view", "Check on click 8");
				/*
				 * Intent reportDetail = new Intent(getContext(),
				 * ReportMainViewDetailActivity.class);
				 * reportDetail.putExtra("checkMonthly", checkMonth);
				 * reportDetail.putExtra("start_date", sDate);
				 * reportDetail.putExtra("end_date", eDate);
				 * getContext().startActivity(reportDetail);
				 */

			}
		});
	}

	private void bindDataCustomItemView(boolean check) {
		if (checkMonth) {
			Cursor monthlyEntry = SqlHelper.instance
					.select("Entry",
							"DISTINCT strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry",
							"1=1 order by strftime('%Y', Date) DESC, strftime('%m', Date) DESC");
			if (monthlyEntry != null) {
				if (monthlyEntry.moveToFirst()) {
					do {
						String month = monthlyEntry.getString(monthlyEntry
								.getColumnIndex("monthEntry"));
						String year = monthlyEntry.getString(monthlyEntry
								.getColumnIndex("yearEntry"));
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
									String entryMonth = entry.getString(entry
											.getColumnIndex("monthEntry"));
									String entryYear = entry.getString(entry
											.getColumnIndex("yearEntry"));

									if (!entryDate.equals(_startDate)
											&& !entryDate.equals(_endDate)) {

										if (entryMonth.equals(month)
												&& entryYear.equals(year)) {
											if (startDate == null) {
												startDate = entryDate;
											} else if (endDate == null) {
												if (startDate
														.compareTo(entryDate) < 0) {
													endDate = entryDate;
												} else {
													endDate = startDate;
													startDate = entryDate;
												}
											} else {
												if (startDate
														.compareTo(entryDate) < 0) {
													endDate = entryDate;
												} else {
													endDate = startDate;
													startDate = entryDate;
												}
											}
										}
									}
								} while (entry.moveToNext());
							}
						}

						if (endDate == null)
							endDate = startDate;

						if (startDate != null) {
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT);
							barChartListDate
									.addView(
											new ReportCustomDialogViewItem(this
													.getContext(), checkMonth,
													startDate, endDate,
													dateList, check), params);
						}

					} while (monthlyEntry.moveToNext());
				}
			}
		} else {
			Cursor weekEntry = SqlHelper.instance
					.select("Entry",
							"DISTINCT strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry",
							"1=1 order by strftime('%Y', Date) DESC, strftime('%W', Date) DESC");
			if (weekEntry != null) {
				if (weekEntry.moveToFirst()) {
					do {
						String week = weekEntry.getString(weekEntry
								.getColumnIndex("weekEntry"));
						String year = weekEntry.getString(weekEntry
								.getColumnIndex("yearEntry"));

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
									String entryWeek = entry.getString(entry
											.getColumnIndex("weekEntry"));
									String entryYear = entry.getString(entry
											.getColumnIndex("yearEntry"));

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

						if (endDate != null) {
							endDate = startDate;
						}

						if (startDate != null) {
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT);
							barChartListDate
									.addView(
											new ReportCustomDialogViewItem(this
													.getContext(), checkMonth,
													startDate, endDate,
													dateList, check), params);
						}
					} while (weekEntry.moveToNext());
				}
			}
		}
	}
}
