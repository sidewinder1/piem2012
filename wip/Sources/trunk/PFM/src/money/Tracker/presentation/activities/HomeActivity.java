package money.Tracker.presentation.activities;

import money.Tracker.common.sql.SqlHelper;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class HomeActivity extends TabActivity {
	private final String typeTabPathId = "type.tab.path.id";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		
		// Create db connector
		SqlHelper.instance = new SqlHelper(this);
		SqlHelper.instance.initializeTable();
	
		// All of code blocks for initialize view should be placed here.
		TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);		
		
		// Create tab and intent for schedule.
		Intent scheduleIntent = new Intent(this, MainViewActivity.class);
		scheduleIntent.putExtra(typeTabPathId, 1);
		setupTab(scheduleIntent, "Schedule", mTabHost);
		
		// Temporary tabs.
				Intent managementIntent = new Intent(this, MainViewActivity.class);
				managementIntent.putExtra(typeTabPathId, 0);
				setupTab(managementIntent, "Expenses\n& Incomes", mTabHost);
		
		// Create tab and intent for Borrowing and Lending.
		Intent borrowAndLendIntent = new Intent(this, BorrowLendMainViewActivity.class);
		setupTab(borrowAndLendIntent, "Borrowing\n& Lending", mTabHost);
		
		
		// Create tab and intent for report
		Intent reportIntent = new Intent(this, ReportMainViewActivity.class);
		setupTab(reportIntent, "Báo cáo", mTabHost);	
		
		
		// insert data
		SqlHelper sqlhelp = new SqlHelper(this);
		sqlhelp.initializeTable();
		Log.d("Insert", "Check 1");
		try
		{
		sqlhelp.insert("Entry", new String[] {"Date", "Type"}, new String[] {"13/10/2012", "1"});
		} catch (Exception e)
		{
			Log.d("Insert", "Check 1 - false");
		}
		Log.d("Insert", "Check 2");
		try
		{
		sqlhelp.insert("EntryDetail", new String[] {"Category_id", "Name", "Money", "Entry_id"}, new String[] {"1", "An trua", "200000", "1"});
		} catch (Exception e)
		{
			Log.d("Insert", "Check 2 - false");
		}
		
		Log.d("Insert", "Check 3");
		try {
		sqlhelp.insert("EntryDetail", new String[] {"Category_id", "Name", "Money", "Entry_id"}, new String[] {"1", "An toi", "200000", "1"});
		}catch (Exception e)
		{
			Log.d("Insert", "Check 3 - false");
		}
		
		Log.d("Insert", "Check 4");
		try
		{
		sqlhelp.insert("EntryDetail", new String[] {"Category_id", "Name", "Money", "Entry_id"}, new String[] {"1", "An khuya", "100000", "1"});
		} catch (Exception e)
		{
			Log.d("Insert", "Check 4 - false");
		}
		Log.d("Insert", "Check 5");
	}
	
	public static void setupTab(final Intent intent, final String tag, TabHost mTabHost) {
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
		getMenuInflater().inflate(R.menu.home_activity, menu);
		return true;
	}
}