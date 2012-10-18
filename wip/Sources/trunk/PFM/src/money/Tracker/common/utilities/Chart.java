package money.Tracker.common.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.activities.R;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

public class Chart {
	
	private int scheduleID;

	public Chart(int _scheduleID) {
		// TODO Auto-generated constructor stub
		this.scheduleID = _scheduleID;
	}
	
	public Intent getIntent(Context context)
	{
		Log.d("Chart", "Check 1");
		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();
		
		Date startDate = null;
		Date endDate = null;
		
		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*",
				"Id=" + scheduleID);
		Log.d("Chart", "Check 2");
		if (scheduleCursor != null) {
			Log.d("report detail", "Check 3");
			if (scheduleCursor.moveToFirst()) {
				Log.d("report detail", "Check 4");
				do {
					Log.d("report detail", "Check 5");
					startDate = Converter.toDate(scheduleCursor
							.getString(scheduleCursor
									.getColumnIndex("Start_date")));
					endDate = Converter.toDate(scheduleCursor
							.getString(scheduleCursor
									.getColumnIndex("End_date")));
				} while (scheduleCursor.moveToNext());
			}
		}
		
		Log.d("Chart", "Check 3");
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*",
				"Type=1");		
		if (entryExpenseCursor != null) {
						if (entryExpenseCursor.moveToFirst()) {
				do {					
					int id = entryExpenseCursor.getInt(entryExpenseCursor
							.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor
							.getString(entryExpenseCursor
									.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select(
								"EntryDetail",
								"Category_Id, sum(Money) as Total", "Entry_Id="
										+ id + " group by Category_Id");						
						if (entryDetailCursor != null) {							
							if (entryDetailCursor.moveToFirst()) {
								do {	
									String name = "";
									double value = 0;
									String color = "";
									
									int categoryID = entryDetailCursor
											.getInt(entryDetailCursor
													.getColumnIndex("Category_Id"));
									Cursor categoryCursor = SqlHelper.instance
											.select("Category", "*", "Id="
													+ categoryID);
									if (categoryCursor != null) {
										if (categoryCursor.moveToFirst()) {											
											do {												
												name = categoryCursor
														.getString(categoryCursor
																.getColumnIndex("Name"));
												color = categoryCursor.getString(categoryCursor.getColumnIndex("User_Color"));
											} while (categoryCursor
													.moveToNext());
										}
									}									
									value = entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Total"));
									
									Log.d("Chart", "Check 4 - " + name);
									Log.d("Chart", "Check 4 - " + String.valueOf(value));
									Log.d("Chart", "Check 4 - " + color);
									series.add(name, value);
									Log.d("Chart", "Check 5");
									SimpleSeriesRenderer r = new SimpleSeriesRenderer();
									Log.d("Chart", "Check 6");
									r.setColor(Color.parseColor(color));
									//Random random = new Random();
									//r.setColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
									Log.d("Chart", "Check 7");
									renderer.addSeriesRenderer(r);
									Log.d("Chart", "Check 8");
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
		
		//series.add("a", 10000);
		//SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		//Random random = new Random();
		//r.setColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		//renderer.addSeriesRenderer(r);
		
		Log.d("Chart", "Check 9");
		Intent intent = ChartFactory.getPieChartIntent(context, series, renderer, "Pie");
		Log.d("Chart", "Check 10");
		return intent;
	}

}
