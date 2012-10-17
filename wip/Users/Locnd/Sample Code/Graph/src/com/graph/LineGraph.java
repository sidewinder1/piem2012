package com.graph;

import org.achartengine.ChartFactory;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;

public class LineGraph {
	
	private String [] _names;
	private int [] _values;
	
	public void setNames(String names) {
		_names = names.split(",");
	}
	
	public void setData(String data) {
		int i = 0;
		_values = new int [data.split(",").length];
		
		for (String value : data.split(","))
		{
			_values[i++] = Integer.parseInt(value);
		}
	}
	
	public Intent getIntent(Context context)
	{
		TimeSeries series = new TimeSeries(_names[0]);
		for (int i = 0; i < _values.length; i++)
		{
			series.add(i + 1, _values[i]);
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer(); 
		XYSeriesRenderer renderer = new XYSeriesRenderer(); 
		mRenderer.addSeriesRenderer(renderer);
		
		Intent intent = ChartFactory.getLineChartIntent(context, dataset, mRenderer, "Line Graph");
		return intent;
	}
}
