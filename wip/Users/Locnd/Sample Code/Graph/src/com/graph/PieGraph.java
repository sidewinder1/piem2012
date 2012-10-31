package com.graph;

import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class PieGraph {
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
			_values[i++] = Integer.parseInt(value.trim());
		}
	}
	
	public Intent getIntent(Context context)
	{
		CategorySeries series = new CategorySeries("Pie Graph");
		DefaultRenderer renderer = new DefaultRenderer();
		
		int k = 0;
		Random random = new Random();
		
		for (int value : _values)
		{
			if (_names.length >= k + 1)
			{
				series.add(_names[k++], value);
			}
			else
			{
				series.add("Empty " + (++k - _names.length), value);
			}
			
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
			renderer.addSeriesRenderer(r);
		}
		
		Intent intent = ChartFactory.getPieChartIntent(context, series, renderer, "Pie");
		return intent;
	}
}
