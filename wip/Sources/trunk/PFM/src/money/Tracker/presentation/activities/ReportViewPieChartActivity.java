package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportCustomDialogViewItem;
import money.Tracker.presentation.customviews.ReportPieCategoryLegendItemView;
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

public class ReportViewPieChartActivity extends Activity {

	private Date startDate;
	private Date endDate;
	private boolean checkMonthly;
	private long totalExpense = 0;
	private List<String> entryCategoryName;
	private List<Long> entryCategoryValue;
	private List<String> entryCategoryColor;
	private List<Long> entryIDList;
	private List<Long> categoryIDList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_pie_chart);

		Bundle extras = getIntent().getExtras();
		checkMonthly = extras.getBoolean("checkMonthly");
		startDate = Converter.toDate(extras.getString("start_date"));
		endDate = Converter.toDate(extras.getString("end_date"));
		
		entryCategoryName = new ArrayList<String>();
		entryCategoryValue = new ArrayList<Long>();
		entryCategoryColor = new ArrayList<String>();
		entryIDList = new ArrayList<Long>();
		categoryIDList = new ArrayList<Long>();
		
		LinearLayout pieChart = (LinearLayout) findViewById(R.id.report_pie_chart);
		TextView totalMoneyTextView = (TextView) findViewById(R.id.report_pie_chart_total_money);
		LinearLayout pieChartLegend = (LinearLayout) findViewById(R.id.report_pie_chart_legend);
		TextView titlePieChart = (TextView) findViewById(R.id.report_pie_chart_title_text_view);
		
		if (checkMonthly)
			titlePieChart.setText(getResources().getString(R.string.report_in_month) + " " + Converter.toString(startDate, "MM"));
		else
			titlePieChart.setText(getResources().getString(R.string.report_in_week) + " " + new StringBuilder(Converter.toString(startDate, "dd/MM/yyyy")).append(" - ").append(Converter.toString(endDate, "dd/MM/yyyy")));

		getData();
		totalMoneyTextView.setText(Converter.toString(totalExpense));
	
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Chart chart = new Chart();
		pieChart.addView(chart.getPieIntent(this.getApplicationContext(), checkMonthly, startDate, endDate), params);
		
		for(int i = 0; i < entryCategoryColor.size(); i++)
		{
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			pieChartLegend.addView(new ReportPieCategoryLegendItemView(this.getApplicationContext(), entryCategoryColor.get(i), entryCategoryName.get(i), entryCategoryValue.get(i), totalExpense, entryIDList.get(i), categoryIDList.get(i)), params1);
		}
	}
	
	private void getData() {
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*","Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (checkMonthly) {
						String entryDateMonth = Converter.toString(entryDate,"MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate,"yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");

						if (entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear)) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail","Category_Id, sum(Money) as Total","Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;
										String color = "";

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													color = categoryCursor.getString(categoryCursor.getColumnIndex("User_Color"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryIDList.add(id);
												categoryIDList.add(categoryID);
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryCategoryColor.add(color);
											}

										} else {
											entryIDList.add(id);
											categoryIDList.add(categoryID);
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryCategoryColor.add(color);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					} else {
						String entryMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");
						
				        Calendar calEntry = Calendar.getInstance();
				        calEntry.setTime(entryDate);
				        int entryWeek = calEntry.get(Calendar.WEEK_OF_MONTH);
				        Calendar calStartDate = Calendar.getInstance();
				        calStartDate.setTime(startDate);
				        int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (entryMonth.equals(startDateMonth) && entryWeek == startDateWeek) {
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										long value = 0;										
										String color = "";

										long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													color = categoryCursor.getString(categoryCursor.getColumnIndex("User_Color"));
												} while (categoryCursor.moveToNext());
											}
										}

										value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));

										if (!entryCategoryName.isEmpty()) {

											boolean check = false;

											for (int i = 0; i < entryCategoryName.size(); i++) {
												if (entryCategoryName.get(i).equals(name)) {
													entryCategoryValue.set(i, entryCategoryValue.get(i) + value);
													check = true;
												}
											}

											if (check == false) {
												entryIDList.add(id);
												categoryIDList.add(categoryID);
												entryCategoryName.add(name);
												entryCategoryValue.add(value);
												entryCategoryColor.add(color);
											}

										} else {
											entryIDList.add(id);
											categoryIDList.add(categoryID);
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryCategoryColor.add(color);
										}
										
										totalExpense += value;
										
									} while (entryDetailCursor.moveToNext());
								}
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
	}

	public void navigateToView() {
			return;
		}

	public void navigateToview() {
			return;
		}

}
