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
import android.view.View.OnClickListener;
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
		final boolean checkMonthly = extras.getBoolean("checkMonthly");
		final String startDate = extras.getString("start_date");
		final String endDate = extras.getString("end_date");

		TextView reportViewDetailTitle = (TextView) findViewById(R.id.report_main_view_detail_title);
		if (checkMonthly) {
			reportViewDetailTitle.setText(DateFormat.format("MMMM yyyy",Converter.toDate(startDate)));
		} else {
			reportViewDetailTitle.setText(new StringBuilder(DateFormat.format("dd/MM/yyyy", Converter.toDate(startDate))).append("-").append(DateFormat.format("dd/MM/yyyy",Converter.toDate(endDate))).toString());
		}

		// Create tab and intent for view detail information
		Intent reportViewDetailIntent = new Intent(this, ReportViewDetailActivity.class);
		reportViewDetailIntent.putExtra("checkMonthly", checkMonthly);
		reportViewDetailIntent.putExtra("start_date", startDate);
		reportViewDetailIntent.putExtra("end_date", endDate);		
		setupTab(reportViewDetailIntent, "Chi tiết", mTabHost);

		// Create tab and intent for chart
		Intent reportViewChartIntent = new Intent(this, ReportViewChartActivity.class);
		reportViewChartIntent.putExtra("checkMonthly", checkMonthly);
		reportViewChartIntent.putExtra("start_date", startDate);
		reportViewChartIntent.putExtra("end_date", endDate);		
		
		// Create chart tab.
		// setupTab(reportViewChartIntent, "Biểu đồ", mTabHost);
		TextView chartTitle = (TextView)findViewById(R.id.report_chart_title);
		chartTitle.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent reportMainViewChart = new Intent(getBaseContext(), ReportMainViewChartActivity.class);
				reportMainViewChart.putExtra("checkMonthly", checkMonthly);
				reportMainViewChart.putExtra("start_date", startDate);
				reportMainViewChart.putExtra("end_date", endDate);
				startActivity(reportMainViewChart);
			}
		});
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
		getMenuInflater()
				.inflate(R.menu.activity_report_main_view_detail, menu);
		return true;
	}
}
