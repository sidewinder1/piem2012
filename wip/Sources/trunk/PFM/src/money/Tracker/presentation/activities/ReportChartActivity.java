package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.IChart;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.util.Log;
import android.view.Menu;

public class ReportChartActivity extends Activity {

	private Date startDate = null;
	private Date endDate = null;
	
	private static final int SERIES_NR = 2;

	private String[] mMenuText;

	private String[] mMenuSummary;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_chart);
        
        mMenuText = new String[] { "Line chart", "Scatter chart", "Time chart", "Bar chart" };
        mMenuSummary = new String[] { "Line chart with randomly generated values",
            "Scatter chart with randomly generated values",
            "Time chart with randomly generated values", "Bar chart with randomly generated values" };
        //setListAdapter(new SimpleAdapter(this, getListValues(), android.R.layout.simple_list_item_2,
            //new String[] { IChart.NAME, IChart.DESC }, new int[] { android.R.id.text1, android.R.id.text2 }));
    }

    private List<Map<String, String>> getListValues() {
        List<Map<String, String>> values = new ArrayList<Map<String, String>>();
        int length = mMenuText.length;
        for (int i = 0; i < length; i++) {
          Map<String, String> v = new HashMap<String, String>();
          v.put(IChart.NAME, mMenuText[i]);
          v.put(IChart.DESC, mMenuSummary[i]);
          values.add(v);
        }
        return values;
      }

      private XYMultipleSeriesDataset getDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
          XYSeries series = new XYSeries("Demo series " + (i + 1));
          for (int k = 0; k < nr; k++) {
            series.add(k, 20 + r.nextInt() % 100);
          }
          dataset.addSeries(series);
        }
        return dataset;
      }

      private XYMultipleSeriesDataset getDateDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        long value = new Date().getTime() - 3 * TimeChart.DAY;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
          TimeSeries series = new TimeSeries("Demo series " + (i + 1));
          for (int k = 0; k < nr; k++) {
            series.add(new Date(value + k * TimeChart.DAY / 4), 20 + r.nextInt() % 100);
          }
          dataset.addSeries(series);
        }
        return dataset;
      }

      private XYMultipleSeriesDataset getBarDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 10;
        Random r = new Random();
        for (int i = 0; i < SERIES_NR; i++) {
          CategorySeries series = new CategorySeries("Demo series " + (i + 1));
          for (int k = 0; k < nr; k++) {
            series.add(100 + r.nextInt() % 100);
          }
          dataset.addSeries(series.toXYSeries());
        }
        return dataset;
      }

      private XYMultipleSeriesRenderer getDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setPointSize(5f);
        renderer.setMargins(new int[] {20, 30, 15, 0});
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.BLUE);
        r.setPointStyle(PointStyle.SQUARE);
        r.setFillBelowLine(true);
        r.setFillBelowLineColor(Color.WHITE);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        r = new XYSeriesRenderer();
        r.setPointStyle(PointStyle.CIRCLE);
        r.setColor(Color.GREEN);
        r.setFillPoints(true);
        renderer.addSeriesRenderer(r);
        renderer.setAxesColor(Color.DKGRAY);
        renderer.setLabelsColor(Color.LTGRAY);
        return renderer;
      }

      public XYMultipleSeriesRenderer getBarDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(16);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[] {20, 30, 15, 0});
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(Color.BLUE);
        renderer.addSeriesRenderer(r);
        r = new SimpleSeriesRenderer();
        r.setColor(Color.GREEN);
        renderer.addSeriesRenderer(r);
        return renderer;
      }

      private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setChartTitle("Chart demo");
        renderer.setXTitle("x values");
        renderer.setYTitle("y values");
        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(10.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(210);
      }
      }
