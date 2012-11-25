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
        
        Log.d("Check chart", "Check 0");
        TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		Log.d("Check chart", "Check 1");
		Bundle extras = getIntent().getExtras();
		Log.d("Check chart", "Check 2");
		boolean checkMonthly = extras.getBoolean("checkMonthly");
		Log.d("Check chart", "Check 3 " + checkMonthly);
		String startDate = extras.getString("start_date");
		Log.d("Check chart", "Check 4 " + startDate);
		String endDate = extras.getString("end_date");
		Log.d("Check chart", "Check 5 " + endDate);

		TextView reportViewDetailTitle = (TextView) findViewById(R.id.report_main_view_chart_title);
		if (checkMonthly) {
			reportViewDetailTitle.setText(DateFormat.format("MMMM yyyy",
					Converter.toDate(startDate)));
		} else {
			reportViewDetailTitle.setText(new StringBuilder(DateFormat.format(
					"dd/MM", Converter.toDate(startDate)))
					.append("-")
					.append(DateFormat.format("dd/MM/yyyy",
							Converter.toDate(endDate))).toString());
		}
		
		Log.d("Check chart", "Check 1");
		
		Date sDate = Converter.toDate(startDate);
		Date eDate = Converter.toDate(endDate);
		Chart chart = new Chart(checkMonthly, sDate, eDate);		
		Log.d("Chart", "Check 2 - " + startDate + " - " + sDate.toString());
		Log.d("Chart", "Check 2 - " + endDate + " - " + eDate.toString());
		// Create tab and intent for view detail information
		Intent pieChartViewIntent = chart.getPieIntent(this); 
		setupTab(pieChartViewIntent, "Biểu đồ hình tròn", mTabHost);

		// Create tab and intent for chart
		Intent barChartViewIntent = chart.getBarIntent(this);
		Log.d("Chart", "Check 11");
		setupTab(barChartViewIntent, "Biểu đồ hình cột", mTabHost);
		Log.d("Chart", "Check 12");
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
