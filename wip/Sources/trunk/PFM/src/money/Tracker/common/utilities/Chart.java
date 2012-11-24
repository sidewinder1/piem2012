package money.Tracker.common.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.activities.R;

import org.achartengine.ChartFactory;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.TextView;

public class Chart extends AbstractChart {

	private Date startDate = null;
	private Date endDate = null;
	private boolean checkMonthly = true;
	private List<String> entryCategoryName;
	private List<Double> entryCategoryValue;
	private List<Integer> entryCategoryColor;

	public Chart(boolean checkMonthly, Date sDate, Date eDate) {
		// TODO Auto-generated constructor stub
		this.startDate = sDate;
		this.endDate = eDate;
		this.checkMonthly = checkMonthly;
	}
	
	private void getEntryData()
	{
		entryCategoryName = new ArrayList<String>();
		entryCategoryValue = new ArrayList<Double>();
		entryCategoryColor = new ArrayList<Integer>();
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null)
		{
			if (entryExpenseCursor.moveToFirst())
			{
				do{
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if(checkMonthly)
					{
						String entryDateMonth = Converter.toString(entryDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");
						String entryDateYear = Converter.toString(entryDate, "yyyy");
						String startDateYear = Converter.toString(startDate,"yyyy");
						
						if(entryDateMonth.equals(startDateMonth) && entryDateYear.equals(startDateYear))
						{
							Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id="	+ id + " group by Category_Id");							
							if (entryDetailCursor != null) {
								if (entryDetailCursor.moveToFirst()) {
									do {
										String name = "";
										double value = 0;
										int color = 0;

										int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
										Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id="+ categoryID);
										if (categoryCursor != null) {
											if (categoryCursor.moveToFirst()) {
												do {
													name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
													color = Color.parseColor(categoryCursor.getString(categoryCursor.getColumnIndex("User_Color")));
												} while (categoryCursor.moveToNext());
											}
										}
										
										value = entryDetailCursor.getDouble(entryDetailCursor.getColumnIndex("Total"));
										
										if (entryCategoryName.isEmpty())
										{
										
											boolean check = false;
											
										String[] caName = entryCategoryName.toArray(new String[entryCategoryName.size()]);
										Double[] caValue = entryCategoryValue.toArray(new Double[entryCategoryValue.size()]);
										
										for (int i=0; i<caName.length; i++)
										{
											if (caName[i].equals(name))
											{
												caValue[i] = caValue[i] + value;
												check = true;
											}
										}
										
										if (check == true)
										{
										entryCategoryName = Arrays.asList(caName);
										entryCategoryValue = Arrays.asList(caValue);
										}else
										{
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryCategoryColor.add(color);	
										}
										
										}
										else
										{
										entryCategoryName.add(name);
										entryCategoryValue.add(value);
										entryCategoryColor.add(color);
										}
									} while(entryDetailCursor.moveToNext());
						}
					}else{
						
					}
				}
			}
		}while(entryExpenseCursor.moveToNext());
			}
		}
	}

	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			Log.d("Check chart", "" + seriesLength + " " + titles[i]);
			for (int k = 0; k < seriesLength; k++) {
				Log.d("Check chart", "" + v[k]);
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		Log.v("abstract", "bbb");
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		Log.v("abstract", "555" + title + xMin + yMin);
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	@SuppressWarnings("deprecation")
	public Intent getBarIntent(Context context) {
		    String[] titles = new String[] { "a", "b", "c", "d", "e" };
		    List<double[]> values = new ArrayList<double[]>();
		    values.add(new double[] { 10, 5, 0, 0, 0, 0, 0, 0, 0, 0 });
		    values.add(new double[] { 0, 0, 20, 15, 0, 0, 0, 0, 0, 0 });
		    values.add(new double[] { 0, 0, 0, 0, 30, 25, 0, 0, 0, 0 });
		    values.add(new double[] { 0, 0, 0, 0, 0, 0, 40, 30, 0, 0 });
		    values.add(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 50, 60 });
		    int[] colors = new int[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.RED, Color.GRAY };
		    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		    setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.5,
		        12.5, 0, 70, Color.GRAY, Color.LTGRAY);
		    renderer.setXLabels(12);
		    renderer.setYLabels(10);
		    renderer.setDisplayChartValues(false);
		    renderer.setXLabelsAlign(Align.LEFT);
		    renderer.setYLabelsAlign(Align.LEFT);
		    // renderer.setPanEnabled(false);
		    // renderer.setZoomEnabled(false);
		    renderer.setZoomRate(1.1f);
		    renderer.setBarSpacing(0.5f);
		    return ChartFactory.getBarChartIntent(context, buildBarDataset(titles, values), renderer,
		        Type.STACKED);
	  }

	public Intent getPieIntent(Context context) {
		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();
		
		String [] entryCategoryNameArray = new String[entryCategoryName.size()];
		entryCategoryNameArray = entryCategoryName.toArray(entryCategoryNameArray);
		Double [] entryCategoryValueArray = new Double[entryCategoryValue.size()];
		entryCategoryValueArray = entryCategoryValue.toArray(entryCategoryValueArray);
		Integer [] entryCategoryColorArray = new Integer[entryCategoryColor.size()];
		entryCategoryColorArray = entryCategoryColor.toArray(entryCategoryColorArray);
				
		for (int i = 0; i < entryCategoryNameArray.length; i++)
		{
			series.add(entryCategoryNameArray[i], entryCategoryValueArray[i]);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();										
			r.setColor(entryCategoryColorArray[i]);										
			renderer.addSeriesRenderer(r);
		}
													
		
		return ChartFactory.getPieChartIntent(context, series,
				renderer, "Pie");
	}

	@Override
	public void draw(Canvas arg0, int arg1, int arg2, int arg3, int arg4,
			Paint arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawLegendShape(Canvas arg0, SimpleSeriesRenderer arg1,
			float arg2, float arg3, int arg4, Paint arg5) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getLegendShapeWidth(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

}
