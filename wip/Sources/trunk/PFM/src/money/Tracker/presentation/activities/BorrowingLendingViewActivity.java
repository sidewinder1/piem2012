package money.Tracker.presentation.activities;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class BorrowingLendingViewActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_lend_view);
        
        TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		
		// Create tab and intent for schedule.
		Intent borrowIntent = new Intent(this, BorowingView.class);
		setupTab(borrowIntent, "Borrow", mTabHost);
		
		// Create tab and intent for Borrowing and Lending.
		Intent LendIntent = new Intent(this, LendingView.class);
		setupTab(LendIntent, "Lend", mTabHost);
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
}
