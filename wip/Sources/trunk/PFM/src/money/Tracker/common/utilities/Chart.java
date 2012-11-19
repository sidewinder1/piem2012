package money.Tracker.common.utilities;

import java.util.ArrayList;
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

	public Chart(Date sDate, Date eDate) {
		// TODO Auto-generated constructor stub
		this.startDate = sDate;
		this.endDate = eDate;
	}

	public String getName() {
		return "Sales horizontal bar chart";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The monthly sales for the last 2 years (horizontal bar chart)";
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
		String[] titles = new String[] { "Sales for 2008", "Sales for 2007",
        "Difference between 2008 and 2007 sales" };
    List<double[]> values = new ArrayList<double[]>();
    values.add(new double[] { 14230, 12300, 14240, 15244, 14900, 12200, 11030, 12000, 12500, 15500,
        14600, 15000 });
    values.add(new double[] { 10230, 10900, 11240, 12540, 13500, 14200, 12530, 11200, 10500, 12500,
        11600, 13500 });
    int length = values.get(0).length;
    double[] diff = new double[length];
    for (int i = 0; i < length; i++) {
      diff[i] = values.get(0)[i] - values.get(1)[i];
    }
    values.add(diff);
    int[] colors = new int[] { Color.BLUE, Color.CYAN, Color.GREEN };
    PointStyle[] styles = new PointStyle[] { PointStyle.POINT, PointStyle.POINT, PointStyle.POINT };
    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    setChartSettings(renderer, "Monthly sales in the last 2 years", "Month", "Units sold", 0.75,
        12.25, -5000, 19000, Color.GRAY, Color.LTGRAY);
    renderer.setXLabels(12);
    renderer.setYLabels(10);
    renderer.setChartTitleTextSize(20);
    renderer.setTextTypeface("sans_serif", Typeface.BOLD);
    renderer.setLabelsTextSize(14f);
    renderer.setAxisTitleTextSize(15);
    renderer.setLegendTextSize(15);
    length = renderer.getSeriesRendererCount();
    for (int i = 0; i < length; i++) {
      XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
      seriesRenderer.setFillBelowLine(i == length - 1);
      seriesRenderer.setFillBelowLineColor(colors[i]);
      seriesRenderer.setLineWidth(2.5f);
      seriesRenderer.setDisplayChartValues(true);
      seriesRenderer.setChartValuesTextSize(10f);
    }
    return ChartFactory.getLineChartIntent(context, buildBarDataset(titles, values), renderer);
	  }

	public Intent getPieIntent(Context context) {
		Log.d("Chart", "Check 1");
		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();

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
												color = categoryCursor
														.getString(categoryCursor
																.getColumnIndex("User_Color"));
											} while (categoryCursor
													.moveToNext());
										}
									}
									value = entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Total"));

									Log.d("Chart", "Check 4 - " + name);
									Log.d("Chart",
											"Check 4 - "
													+ String.valueOf(value));
									Log.d("Chart", "Check 4 - " + color);
									series.add(name, value);
									Log.d("Chart", "Check 5");
									SimpleSeriesRenderer r = new SimpleSeriesRenderer();
									Log.d("Chart", "Check 6");
									r.setColor(Color.parseColor(color));
									// Random random = new Random();
									// r.setColor(Color.argb(255,
									// random.nextInt(255), random.nextInt(255),
									// random.nextInt(255)));
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

		Log.d("Chart", "Check 9");
		Intent intent = ChartFactory.getPieChartIntent(context, series,
				renderer, "Pie");
		Log.d("Chart", "Check 10");
		return intent;
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
