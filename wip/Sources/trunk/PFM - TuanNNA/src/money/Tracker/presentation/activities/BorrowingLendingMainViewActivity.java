package money.Tracker.presentation.activities;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class BorrowingLendingMainViewActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_lend_main_view);
        
        TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);
		
		// Create tab and intent for schedule.
		Intent borrowViewIntent = new Intent(this, BorrowLendViewActivity.class);
		borrowViewIntent.putExtra("Borrow", true);	
		setupTab(borrowViewIntent, "Borrow", mTabHost);
		
		Log.d("BLMainView", "Create first tab");
		
		// Create tab and intent for Borrowing and Lending.
		Intent lendViewIntent = new Intent(this, BorrowLendViewActivity.class);
		lendViewIntent.putExtra("Borrow", false);
		setupTab(lendViewIntent, "Lend", mTabHost);
		
		Log.d("1", "Create tab done");
		
		Button addBorrowLend = (Button) findViewById(R.id.addBorrowAndLend);
		
		addBorrowLend.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent borrowLendInsert = new Intent(BorrowingLendingMainViewActivity.this, BorrowLendInsertActivity.class);
				startActivity(borrowLendInsert);
			}
		});
    }
	
		// This method is used to setup a tab with Name tab and content of tab.
		private void setupTab(final Intent intent, final String tag, TabHost mTabHost) {
			View tabview = createTabView(mTabHost.getContext(), tag);
			Log.d("BLMainView", "Check 1");
			TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
			Log.d("BLMainView", "Check 2");
			setContent.setContent(intent);
			Log.d("BLMainView", "Check 3");
			mTabHost.addTab(setContent);
			Log.d("BLMainView", "Check 4");
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
