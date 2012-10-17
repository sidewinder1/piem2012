package money.Tracker.presentation.activities;

import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ReportMainViewDetailActivity extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_main_view_detail);
        
        TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		
		Bundle extras = getIntent().getExtras();
		int scheduleID = extras.getInt("schedule_id");
		
		TextView reportViewDetailTitle = (TextView) findViewById(R.id.report_main_view_detail_title);
		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", "Id=" + scheduleID);
		Log.d("Main View Detail", "Check 1 - " + scheduleID);
		if (scheduleCursor != null)
		{
			if(scheduleCursor.moveToFirst())
			{
				do
				{
					int scheduleType = scheduleCursor.getInt(scheduleCursor.getColumnIndex("Type"));
					Date startDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date"))); 
					Date endDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("End_date")));		
					if (scheduleType == 1) {
						reportViewDetailTitle.setText(DateFormat.format("MMMM yyyy", endDate));
					} else {
						reportViewDetailTitle.setText(new StringBuilder(DateFormat.format("dd/MM", startDate)).append("-").append(DateFormat.format("dd/MM/yyyy", endDate)).toString());
					}
				}while(scheduleCursor.moveToNext());
			}
		}
		Log.d("Main View Detail", "Check 2");
		// Create tab and intent for view detail information
		Intent reportViewDetailIntent = new Intent(this, ReportViewDetailActivity.class);
		Log.d("Main View Detail", "Check 3");
		reportViewDetailIntent.putExtra("schedule_id", scheduleID);
		Log.d("Main View Detail", "Check 4");
		setupTab(reportViewDetailIntent, "Chi tiết", mTabHost);
		
		// Create tab and intent for chart
		//Chart pie = new Chart(scheduleID);
		//Log.d("Chart", String.valueOf(scheduleID));
		//Intent pieIntent = pie.getIntent(this);
		Log.d("Chart", "Check 11");
		//setupTab(pieIntent, "Biểu đồ", mTabHost);
		//startActivity(pieIntent);
		Log.d("Chart", "Check 12");
    }
    
    	// This method is used to setup a tab with Name tab and content of tab.
 		private void setupTab(final Intent intent, final String tag, TabHost mTabHost) {
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
        getMenuInflater().inflate(R.menu.activity_report_main_view_detail, menu);
        return true;
    }
}
