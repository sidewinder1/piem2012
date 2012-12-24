package money.Tracker.common.utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import money.Tracker.common.sql.SqlHelper;
import org.achartengine.ChartFactory;
import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.view.View;

@SuppressWarnings("serial")
public class Chart extends AbstractChart {
	private Date startDate = null;
	private Date endDate = null;
	private boolean checkMonthly = true;
	private List<String> entryCategoryName;
	private List<Double> entryCategoryValue;
	private List<Integer> entryCategoryColor;
	private List<Double> scheduleCategoryValue;
	private List<Date []> dateList;

	public Chart() {
	}

	private void getEntryData() {
		entryCategoryName = new ArrayList<String>();
		entryCategoryValue = new ArrayList<Double>();
		entryCategoryColor = new ArrayList<Integer>();
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									String name = "";
									double value = 0;
									int color = 0;

									long categoryID = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Category_Id"));
									Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id=" + categoryID);
									if (categoryCursor != null) {
										if (categoryCursor.moveToFirst()) {
											do {
												name = categoryCursor.getString(categoryCursor.getColumnIndex("Name"));
												color = Color.parseColor(categoryCursor.getString(categoryCursor.getColumnIndex("User_Color")));
											} while (categoryCursor.moveToNext());
										}
									}

									value = entryDetailCursor.getDouble(entryDetailCursor.getColumnIndex("Total"));

									if (!entryCategoryName.isEmpty()) {

										boolean check = false;

										for (int i = 0; i < entryCategoryName.size(); i++) {
											if (entryCategoryName.get(i).equals(name)) {
												check = true;
											}
										}

										if (check == false) {
											entryCategoryName.add(name);
											entryCategoryValue.add(value);
											entryCategoryColor.add(color);
										}

									} else {
										entryCategoryName.add(name);
										entryCategoryValue.add(value);
										entryCategoryColor.add(color);
									}
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
	}

	private void getData() {
		// get spent
		long spent = 0;

		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									spent += entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Money"));
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

		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", whereCondition);
		if (scheduleCursor != null) {
			if (scheduleCursor.moveToFirst()) {
				do {
					if (checkMonthly) {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate, "MM");

						if (scheduleMonth.equals(startDateMonth))
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					} else {
						Date scheduleStartDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
						String scheduleMonth = Converter.toString(scheduleStartDate, "MM");
						String startDateMonth = Converter.toString(startDate,"MM");

						Calendar calScheduleStart = Calendar.getInstance();
						calScheduleStart.setTime(scheduleStartDate);
						int scheduleWeek = calScheduleStart.get(Calendar.WEEK_OF_MONTH);
						Calendar calStartDate = Calendar.getInstance();
						calStartDate.setTime(scheduleStartDate);
						int startDateWeek = calStartDate.get(Calendar.WEEK_OF_MONTH);

						if (scheduleMonth.equals(startDateMonth) && scheduleWeek == startDateWeek)
							budget = scheduleCursor.getLong(scheduleCursor.getColumnIndex("Budget"));
					}
				} while (scheduleCursor.moveToNext());
			}
		}

		entryCategoryValue.add((double) spent);
		scheduleCategoryValue.add((double) budget);
	}

	protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);

		renderer.setInScroll(false);
		renderer.setPanEnabled(false, false);
		renderer.setInScroll(false);

		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
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
	
	private void sort()
	{
		int i, j;
		int length = dateList.size();
		Date[] t = new Date[2];
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				Date j1StartedDate;
				Date jStartedDate;
				
				Date[] compareDate = dateList.get(j);
				jStartedDate = compareDate[0];
				
				compareDate = dateList.get(j - 1);
				j1StartedDate = compareDate[0];
				
				if (j1StartedDate.compareTo(jStartedDate) < 0) {
					t = dateList.get(j - 1);
					dateList.set(j - 1 , dateList.get(j));
					dateList.set(j, t);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public View getBarCompareIntent(Context context, boolean checkMonthly,List<Date[]> dateList) {
		entryCategoryValue = new ArrayList<Double>();
		scheduleCategoryValue = new ArrayList<Double>();
		this.checkMonthly = checkMonthly;
		this.dateList = dateList;
		
		sort();
		
		for (int i = 0; i < dateList.size(); i++) {
			Date[] compareDate = dateList.get(i);
			this.startDate = compareDate[0];
			this.endDate = compareDate[1];

			getData();
		}

		double xMax = 0;
		double yMax = 0;

		double[] entryCategoryValueArray = new double[entryCategoryValue.size()];
		double[] scheduleCategoryValueArray = new double[entryCategoryValue.size()];

		for (int i = 0; i < entryCategoryValue.size(); i++) {
			entryCategoryValueArray[i] = entryCategoryValue.get(i) / 10000;
			scheduleCategoryValueArray[i] = scheduleCategoryValue.get(i) / 10000;
			yMax = Math.max(Math.max(entryCategoryValueArray[i], scheduleCategoryValueArray[i]), yMax);
		}
		
		if(yMax > 1000 || yMax == 1000)
		{
			yMax = yMax + yMax / 10;
		}

		List<double[]> values = new ArrayList<double[]>();
		values.add(entryCategoryValueArray);
		values.add(scheduleCategoryValueArray);

		String[] titles = new String[] { "Kế Hoạch", "Thực Tế" };
		int[] colors = new int[] { Color.YELLOW, Color.GREEN }; // Color.parseColor("#FFD700"),
																// Color.parseColor("#7CFC00")

		xMax = entryCategoryValueArray.length + 1;
		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
		setChartSettings(renderer, "", "", "\r\n\r\nx10000 VND", -0.5, xMax, 0, yMax, Color.TRANSPARENT, Color.TRANSPARENT);
		renderer.setXLabels(0);
		renderer.setYLabels(0);
		for (int i = 0; i < dateList.size(); i++) {
			Date[] compareDate = dateList.get(i);
			this.startDate = compareDate[0];
			this.endDate = compareDate[1];

			if (checkMonthly)
				renderer.addXTextLabel(i + 1,Converter.toString(startDate, "MM/yyyy"));
			else
				renderer.addXTextLabel(i + 1,Converter.toString(startDate, "dd") + " - " + Converter.toString(endDate, "dd\r\n\r\n") + Converter.toString(endDate, "MM/yyyy\r\n\r\n"));
		}
		
		renderer.getSeriesRendererAt(0).setChartValuesTextAlign(Align.LEFT);

		// Display value on X axis.
		int unit = (int) yMax / 5;
		for (int i = 0; i < 6; i++) {
			renderer.addYTextLabel(unit * i, new StringBuilder("\r\n").append(String.valueOf(unit * i)).toString());
		}
		// renderer.setXLabels(12);
		// renderer.setYLabels(10);
		// renderer.clearTextLabels();
		renderer.setApplyBackgroundColor(true);
		renderer.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer.setDisplayChartValues(true);
		renderer.setOrientation(Orientation.VERTICAL);
		renderer.setXLabelsAlign(!checkMonthly ? Align.CENTER : Align.RIGHT);
		renderer.setYLabelsAlign(Align.CENTER);
		renderer.setShowAxes(true);

		// renderer.setChartTitleTextSize(25);
		renderer.setChartValuesTextSize(20);
		renderer.setLabelsTextSize(18);
		renderer.setLegendTextSize(20);
		//renderer.setFitLegend(true);
		//renderer.setAntialiasing(true);
		renderer.setZoomEnabled(true, false);
		// renderer.setPanEnabled(false);
		// renderer.setZoomEnabled(false);
		// renderer.setZoomRate(1.1f);
		renderer.setMargins(new int[] { 60, -10, 100, 0 });
		// renderer.setInitialRange(new double[]{0, 100, 0, yMax}, 1);
		renderer.setBarSpacing(0.6f);
		// renderer.setShowLabels(false);
		renderer.setXLabelsAngle(checkMonthly ? 0 : -90);
		renderer.setPanEnabled(false, false);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setXLabelsColor(Color.BLACK); // Color.argb(0x00, 0x01, 0x01,
		renderer.setAxisTitleTextSize(13);										// 0x01)
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxesColor(Color.BLACK);
		renderer.setShowLegend(false);
		// renderer.setYLabelsColor(Color.BLACK);
		// renderer.clearXTextLabels();
		// renderer.clearYTextLabels();
		// return ChartFactory.getBarChartIntent(context,
		// buildBarDataset(titles, values), renderer, Type.STACKED);
		return ChartFactory.getBarChartView(context,buildBarDataset(titles, values), renderer, Type.DEFAULT);
	}

	public View getPieIntent(Context context, boolean checkMonthly, Date sDate,
			Date eDate) {
		this.startDate = sDate;
		this.endDate = eDate;
		this.checkMonthly = checkMonthly;

		getEntryData();

		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();

		String[] entryCategoryNameArray = new String[entryCategoryName.size()];
		entryCategoryNameArray = entryCategoryName
				.toArray(entryCategoryNameArray);
		Double[] entryCategoryValueArray = new Double[entryCategoryValue.size()];
		entryCategoryValueArray = entryCategoryValue
				.toArray(entryCategoryValueArray);
		Integer[] entryCategoryColorArray = new Integer[entryCategoryColor
				.size()];
		entryCategoryColorArray = entryCategoryColor
				.toArray(entryCategoryColorArray);

		for (int i = 0; i < entryCategoryNameArray.length; i++) {
			series.add(entryCategoryNameArray[i], entryCategoryValueArray[i]);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(entryCategoryColorArray[i]);
			renderer.addSeriesRenderer(r);
		}

		renderer.setPanEnabled(false);
		renderer.setShowLegend(false);
		renderer.setShowLabels(false);
		renderer.setScale((float) 1.1);
		// /renderer.setZoomRate(0);

		// return ChartFactory.getPieChartView(context, series, renderer);
		return ChartFactory.getPieChartView(context, series, renderer);
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
