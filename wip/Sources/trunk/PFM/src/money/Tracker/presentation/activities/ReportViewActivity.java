package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.customviews.ReportCustomDialogViewItem;
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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ReportViewActivity extends Activity {

	private boolean checkMonthly;
	private TextView displayNoReportDataText;
	private LinearLayout reportListView;
	private Context myContext = this;
	private int checked = 0;
	private List<Date[]> dateList;
	private LinearLayout barChartListDate;
	private boolean hasData = false;

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

						if (endDate == null) {
							endDate = startDate;
						}

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT);
						ReportViewItem monthReportViewItem = new ReportViewItem(
								this.getApplicationContext(), startDate,
								endDate, checkMonthly);
						reportListView.addView(monthReportViewItem, params);

						final Date sDate = startDate;
						final Date eDate = endDate;
						monthReportViewItem
								.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										// TODO Auto-generated method stub
										onItemClick(checkMonthly, sDate, eDate);
									}
								});

					} while (monthlyEntry.moveToNext());
				}
			}

		} else {
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

						if (endDate == null) {
							endDate = startDate;
						}

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT);
						ReportViewItem weekReportViewItem = new ReportViewItem(
								this, startDate, endDate, checkMonthly);
						reportListView.addView(weekReportViewItem, params);

						final Date sDate = startDate;
						final Date eDate = endDate;
						weekReportViewItem
								.setOnClickListener(new OnClickListener() {

									public void onClick(View v) {
										// TODO Auto-generated method stub
										onItemClick(checkMonthly, sDate, eDate);
									}
								});

					} while (weekEntry.moveToNext());
				}
			}
		}
	}

	private void onItemClick(final boolean checkMonth, final Date sDate,
			final Date eDate) {
		Log.d("Check report view", "Check on click 1");
		final Dialog dialog = new Dialog(getParent(), R.style.CustomDialogTheme);
		dialog.setContentView(R.layout.report_view_chart_custom_dialog);

		dialog.show();

		final RadioButton reportInMonthWeekCheckBox = (RadioButton) dialog
				.findViewById(R.id.report_in_month_week_checkbox);
		final RadioButton reportBetweenMonthWeekCheckBox = (RadioButton) dialog
				.findViewById(R.id.report_betwwen_month_week_checkbox);

		if (checkMonthly) {
			reportBetweenMonthWeekCheckBox
					.setText(R.string.report_between_month);
			reportInMonthWeekCheckBox.setText(R.string.report_in_month);
		} else {
			reportBetweenMonthWeekCheckBox
					.setText(R.string.report_between_week);
			reportInMonthWeekCheckBox.setText(R.string.report_in_week);
		}

		/*
		 * RadioGroup radioGroup = (RadioGroup)
		 * findViewById(R.id.report_radio_group);
		 * 
		 * radioGroup.setOnCheckedChangeListener(new
		 * RadioGroup.OnCheckedChangeListener() {
		 * 
		 * public void onCheckedChanged(RadioGroup group, int checkedId) { //
		 * TODO Auto-generated method stub switch(checkedId) { case
		 * R.id.report_in_month_week_checkbox: checked = 1; break; case
		 * R.id.report_betwwen_month_week_checkbox: checked = 2; break; } } });
		 */

		reportInMonthWeekCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							checked = 1;
						}
					}
				});

		reportBetweenMonthWeekCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						if (isChecked) {
							checked = 2;
						}
					}
				});

		Button okButton = (Button) dialog
				.findViewById(R.id.report_custom_dialog_ok_button);
		Button cancelButton = (Button) dialog
				.findViewById(R.id.report_custom_dialog_cancel_button);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checked = 0;
				dialog.dismiss();
			}
		});

		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (checked) {
				case 1:
					Intent pieChart = new Intent(getParent(),
							ReportViewPieChartActivity.class);
					pieChart.putExtra("checkMonthly", checkMonth);
					pieChart.putExtra("start_date", Converter.toString(sDate));
					pieChart.putExtra("end_date", Converter.toString(eDate));
					startActivity(pieChart);

					checked = 0;
					dialog.dismiss();

					break;

				case 2:
					dateList = new ArrayList<Date[]>();
					dateList.add(new Date[] { sDate, eDate });

					final Dialog compareDialog = new Dialog(getParent(),
							R.style.CustomDialogTheme);
					compareDialog
							.setContentView(R.layout.report_view_chart_compare_custom_dialog);
					TextView title = (TextView) compareDialog
							.findViewById(R.id.compare_report_dialog_title);
					if (checkMonth)
						title.setText(getResources().getString(
								R.string.report_bar_chart_month_title));
					else
						title.setText(getResources().getString(
								R.string.report_bar_chart_week_title));

					barChartListDate = (LinearLayout) compareDialog
							.findViewById(R.id.report_compare_custom_dialog_list_date_view);
					barChartListDate.removeAllViews();
					bindDataCustomItemView(false, sDate, eDate);
					Button okButton = (Button) compareDialog
							.findViewById(R.id.report_compare_custom_dialog_ok_button);
					Button cancelButton = (Button) compareDialog
							.findViewById(R.id.report_compare_custom_dialog_cancel_button);

					cancelButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							// TODO Auto-generated method stub
							int size = dateList.size();
							for (int i = 1; i < size; i++) {
								dateList.remove(1);
							}

							compareDialog.dismiss();
						}
					});
					okButton.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent barChartIntent = new Intent(
									ReportViewActivity.this,
									ReportViewBarChartActivity.class);

							int size = dateList.size();
							barChartIntent.putExtra("Size_List", size);
							barChartIntent.putExtra("checkMonthly", checkMonth);
							for (int i = 0; i < size; i++) {
								Date[] compareDate = dateList.get(i);
								barChartIntent.putExtra("start_date_" + i,
										Converter.toString(compareDate[0]));
								barChartIntent.putExtra("end_date_" + i,
										Converter.toString(compareDate[1]));
								Log.d("Check list extra",
										i
												+ " - "
												+ Converter
														.toString(compareDate[0])
												+ " - "
												+ Converter
														.toString(compareDate[1]));
							}
							for (int i = 1; i < size; i++) {
								dateList.remove(1);
							}

							startActivity(barChartIntent);
							compareDialog.dismiss();
						}
					});

					final CheckBox checkAllCheckBox = (CheckBox) compareDialog
							.findViewById(R.id.compare_report_dialog_select_all);
					checkAllCheckBox
							.setOnCheckedChangeListener(new OnCheckedChangeListener() {
								public void onCheckedChanged(
										CompoundButton buttonView,
										boolean isChecked) {
									int size = dateList.size();

									for (int i = 1; i < size; i++) {
										dateList.remove(1);
									}

									barChartListDate.removeAllViews();
									bindDataCustomItemView(isChecked, sDate,
											eDate);
								}
							});

					if (hasData)
						compareDialog.show();
					else {
						Alert alert = new Alert();
						alert.show(
								ReportViewActivity.this,
								getResources().getString(
										R.string.report_no_data_compare_date));
					}

					checked = 0;
					dialog.dismiss();

					break;

				default:
					checked = 0;
					dialog.dismiss();
					break;
				}
			}
		});

	}

	private void bindDataCustomItemView(boolean check, Date _startDate,
			Date _endDate) {
		if (checkMonthly) {
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
							barChartListDate.addView(
									new ReportCustomDialogViewItem(this,
											checkMonthly, startDate, endDate,
											dateList, check), params);
							hasData = true;
						}

					} while (monthlyEntry.moveToNext());
				}
			}
		} else {
			Cursor weekEntry = SqlHelper.instance
					.select("Entry",
							"DISTINCT strftime('%W', Date) as weekEntry, strftime('%Y', Date) as yearEntry",
							"1=1 order by strftime('%Y', Date) DESC, strftime('%W', Date) DESC");
			if (weekEntry != null && weekEntry.moveToFirst()) {
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
								Date entryDate = Converter
										.toDate(entry.getString(entry
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
						// TODO: Locnd hotfix for case: endDate = null
						if (endDate == null) {
							endDate = DateTimeHelper
									.getLastDayOfWeek(startDate);
						}

						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT);
						barChartListDate.addView(
								new ReportCustomDialogViewItem(this,
										checkMonthly, startDate, endDate,
										dateList, check), params);
						hasData = true;
					}
				} while (weekEntry.moveToNext());
			}
		}
	}
}
