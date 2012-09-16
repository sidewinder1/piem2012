package money.Tracker.presentation;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class HomeActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		
		// Create tab and intent for schedule.
		Intent scheduleIntent = new Intent(this, ScheduleViewActivity.class);
		setupTab(scheduleIntent, "Schedule", mTabHost);
		
		// Create tab and intent for Borrowing and Lending.
		Intent borrowAndLendIntent = new Intent(this, BorrowingLendingViewActivity.class);
		setupTab(borrowAndLendIntent, "Borrowing\n& Lending", mTabHost);
		
		// Temporary tabs.
		setupTab(scheduleIntent, "Expenses\n& Incomes", mTabHost);
		setupTab(borrowAndLendIntent, "Report", mTabHost);
	}

	/// This method is used to setup a tab with Name tab and content of tab.
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
		getMenuInflater().inflate(R.menu.home_activity, menu);
		return true;
	}
}