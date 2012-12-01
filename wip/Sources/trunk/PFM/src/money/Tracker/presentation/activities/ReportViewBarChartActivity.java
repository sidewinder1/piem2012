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

	private Date _startDate;
	private Date _endDate;
	private boolean checkMonth;
	private LinearLayout barChart;
	private LinearLayout barChartListDate;
	private List<Date[]> dateList;	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_bar_chart);

		Bundle extras = getIntent().getExtras();
		final boolean checkMonthly = extras.getBoolean("checkMonthly");
		checkMonth = checkMonthly;
		_startDate = Converter.toDate(extras.getString("start_date"));
		_endDate = Converter.toDate(extras.getString("end_date"));

		TextView buttonBarChartCompare = (TextView) findViewById(R.id.report_bar_chart_button_compare_title);
		LinearLayout barChartButton = (LinearLayout) findViewById(R.id.report_bar_chart_button_compare);
		barChart = (LinearLayout) findViewById(R.id.report_bar_chart);

		if (checkMonthly) {
			buttonBarChartCompare.setText("So sánh giữa các tháng");
		} else {
			buttonBarChartCompare.setText("So sánh giữa các tuần");
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Chart chart = new Chart();
		barChart.addView(chart.getBarIntent(this, checkMonthly, _startDate, _endDate), params);

		dateList = new ArrayList<Date[]>();
		dateList.add(new Date[] {_startDate, _endDate});

		barChartButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog( ReportViewBarChartActivity.this);

				dialog.setContentView(R.layout.report_view_chart_custom_dialog);

				if (checkMonthly)
					dialog.setTitle("Chọn tháng so sánh");
				else
					dialog.setTitle("Chọn tuần so sánh");

				barChartListDate = (LinearLayout) dialog.findViewById(R.id.report_custom_dialog_list_date_view);
				barChartListDate.removeAllViews();
				bindDataCustomItemView(false);

				Button okButton = (Button) dialog.findViewById(R.id.report_custom_dialog_ok_button);
				Button cancelButton = (Button) dialog.findViewById(R.id.report_custom_dialog_cancel_button);

				cancelButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						int size =  dateList.size();
						
						for (int i = 1; i < size; i++) {
							dateList.remove(1);
						}

						dialog.dismiss();
					}
				});

				okButton.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						bindCompareChart();
						int size =  dateList.size();
						for (int i = 1; i < size; i++) {
							dateList.remove(1);
							Log.d("Check remove dateList", (dateList.size() + " - " + i));
						}
						dialog.dismiss();
					}
				});

				final CheckBox checkAllCheckBox = (CheckBox) dialog.findViewById(R.id.report_custom_dialog_check_all_checkbox);
				final CheckBox uncheckAllCheckBox = (CheckBox) dialog.findViewById(R.id.report_custom_dialog_uncheck_all_checkbox);

				checkAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									int size =  dateList.size();
									for (int i = 1; i < size; i++) {
										dateList.remove(1);
										Log.d("Check remove dateList", (dateList.size() + " - " + i));
									}

									barChartListDate.removeAllViews();
									bindDataCustomItemView(true);

									uncheckAllCheckBox.setChecked(false);
								}
							}
						});

				uncheckAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								if (isChecked) {
									int size =  dateList.size();
									for (int i = 1; i < size; i++) {
										dateList.remove(1);
									}

									barChartListDate.removeAllViews();
									bindDataCustomItemView(false);

									checkAllCheckBox.setChecked(false);
								}
							}
						});

				dialog.show();
			}
		});
	}

	private void bindCompareChart() {
		if (dateList.size() > 1)
		{
			Chart chart = new Chart();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			barChart.removeAllViews();
			barChart.addView(chart.getBarCompareIntent(this, checkMonth, dateList), params);
		}
	}

	private void bindDataCustomItemView(boolean check) {
		if (checkMonth) {
			Cursor monthlyEntry = SqlHelper.instance.select("Entry", "DISTINCT strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry", "1=1 order by strftime('%Y', Date) DESC, strftime('%m', Date) DESC");
			if (monthlyEntry != null) {
				if (monthlyEntry.moveToFirst()) {
					do {
						String month = monthlyEntry.getString(monthlyEntry.getColumnIndex("monthEntry"));
						String year = monthlyEntry.getString(monthlyEntry.getColumnIndex("yearEntry"));
						Cursor entry = SqlHelper.instance.select("Entry", "*, strftime('%m', Date) as monthEntry, strftime('%Y', Date) as yearEntry","");
						Date startDate = null;
						Date endDate = null;

						if (entry != null) {
							if (entry.moveToFirst()) {
								do {
									Date entryDate = Converter.toDate(entry.getString(entry.getColumnIndex("Date")));
									String entryMonth = entry.getString(entry.getColumnIndex("monthEntry"));
									String entryYear = entry.getString(entry.getColumnIndex("yearEntry"));

									if (!entryDate.equals(_startDate) && !entryDate.equals(_endDate)) {

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
									}
								} while (entry.moveToNext());
							}
						}

						if (endDate == null)
							endDate = startDate;

						if (startDate != null) {
							LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
							barChartListDate.addView(new ReportCustomDialogViewItem(this, checkMonth, startDate, endDate, dateList, check), params);
						}

					} while (monthlyEntry.moveToNext());
				}
			}
		} else {

		}
	}
}
