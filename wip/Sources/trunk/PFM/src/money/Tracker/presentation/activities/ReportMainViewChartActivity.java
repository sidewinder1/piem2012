package money.Tracker.presentation.activities;

import java.util.Date;

import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ReportMainViewChartActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main_view_chart);
        
        TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		Bundle extras = getIntent().getExtras();
		boolean checkMonthly = extras.getBoolean("checkMonthly");
		String startDate = extras.getString("start_date");
		String endDate = extras.getString("end_date");		

		TextView reportViewDetailTitle = (TextView) findViewById(R.id.report_main_view_chart_title);
		if (checkMonthly) {
			reportViewDetailTitle.setText(DateFormat.format("MMMM yyyy", Converter.toDate(startDate)));
		} else {
			reportViewDetailTitle.setText(new StringBuilder(DateFormat.format("dd/MM", Converter.toDate(startDate))).append("-").append(DateFormat.format("dd/MM/yyyy",Converter.toDate(endDate))).toString());
		}
		
		Log.d("Check chart", "Check 1");
		
		Date sDate = Converter.toDate(startDate);
		Date eDate = Converter.toDate(endDate);
		Chart chart = new Chart();		
		// Create tab and intent for view detail information
		// Intent pieChartViewIntent = chart.getPieIntent(this, checkMonthly, sDate, eDate);
		Intent pieChartViewIntent = new Intent(this, ReportViewPieChartActivity.class);
		pieChartViewIntent.putExtra("checkMonthly", checkMonthly);
		pieChartViewIntent.putExtra("start_date", startDate);
		pieChartViewIntent.putExtra("end_date", endDate);
		setupTab(pieChartViewIntent, "Biểu đồ hình tròn", mTabHost);

		// Create tab and intent for chart
		//Intent barChartViewIntent = chart.getBarIntent(this);
		Intent barChartViewIntent = new Intent(this, ReportViewBarChartActivity.class);
		barChartViewIntent.putExtra("checkMonthly", checkMonthly);
		barChartViewIntent.putExtra("start_date", startDate);
		barChartViewIntent.putExtra("end_date", endDate);
		setupTab(barChartViewIntent, "Biểu đồ hình cột", mTabHost);
    }
    
 // This method is used to setup a tab with Name tab and content of tab.
 	private void setupTab(final Intent intent, final String tag,
 			TabHost mTabHost) {
 		View tabview = createTabView(mTabHost.getContext(), tag);
 		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
 		setContent.setContent(intent);
 		mTabHost.addTab(setContent);
 	}

 	// Create tab view.
 	private static View createTabView(final Context context, final String text) {
 		View view = LayoutInflater.from(context)
 				.inflate(R.layout.tabs_bg, null);
 		TextView tv = (TextView) view.findViewById(R.id.tabsText);
 		tv.setText(text);
 		return view;
 	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_report_main_view_chart, menu);
        return true;
    }
}
