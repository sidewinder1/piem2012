package money.Tracker.common.utilities;

import java.util.ArrayList;
import java.util.List;

//import org.achartengine.*;
//#import org.achartengine.chart.AbstractChart;
//import org.achartengine.renderer.SimpleSeriesRenderer;
//import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

//public class ChartDrawer  extends AbstractChart {

//	@Override
//	public void draw(Canvas arg0, int arg1, int arg2, int arg3, int arg4,
//			Paint arg5) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public Intent getIntent(Context context) {
//
//	    String[] titles = new String[] { "Good", "Defect", "Repair" };
//	    List<double[]> values = new ArrayList<double[]>();
//
//	    values.add(new double[] { 14230, 12300, 1424, 15240, 15900, 19200,
//	    22030, 21200, 19500, 15500, 12060, 14000 });
//	    values.add(new double[] { 14230, 12300, 14240, 15244, 15900, 19200,
//	            22030, 21200, 19500, 15500, 12600, 14000 });
//	    values.add(new double[] { 5230, 7300, 9240, 10540, 7900, 9200, 12030,
//	            11200, 9500, 10500, 11600, 13500 });
//
//	    int[] colors = new int[] { Color.GREEN, Color.YELLOW, Color.RED };
//
//	    XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
//
//	    setChartSettings(renderer, "Machine Efficiency Rates",
//	            "Machine", "NET produce", 0.5, 12.5, 0, 24000, Color.GRAY,
//	            Color.LTGRAY);
//
//	    renderer.getSeriesRendererAt(0).setDisplayChartValues(true);
//	    renderer.getSeriesRendererAt(1).setDisplayChartValues(true);
//	    renderer.getSeriesRendererAt(2).setDisplayChartValues(true);
//	    renderer.setXLabels(12);
//	    renderer.setYLabels(5);
//	    renderer.setXLabelsAlign(Align.LEFT);
//	    renderer.setYLabelsAlign(Align.LEFT);
//	    renderer.setPanEnabled(true, false);
//	    renderer.setZoomEnabled(true);
//	    renderer.setZoomRate(1.1f);
//	    renderer.setBarSpacing(0.5f);
//	    return ChartFactory.getBarChartIntent(context,
//	            buildBarDataset(titles, values), renderer, Type.STACKED);
//
//	}
//	
//	@Override
//	public void drawLegendShape(Canvas arg0, SimpleSeriesRenderer arg1,
//			float arg2, float arg3, int arg4, Paint arg5) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public int getLegendShapeWidth(int arg0) {
//		// TODO Auto-generated method stub
//		return 0;
//	}

//}
