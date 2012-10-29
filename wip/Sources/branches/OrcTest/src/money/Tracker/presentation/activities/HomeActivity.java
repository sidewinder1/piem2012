package money.Tracker.presentation.activities;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
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

		// Create Expense & income tab.
		Intent managementIntent = new Intent(this, MainViewActivity.class);
		managementIntent.putExtra(typeTabPathId, 0);
		setupTab(managementIntent, "Expenses\n& Incomes", mTabHost,
				R.drawable.management_icon);

		// Create tab and intent for schedule.
		Intent scheduleIntent = new Intent(this, MainViewActivity.class);
		scheduleIntent.putExtra(typeTabPathId, 1);
		setupTab(scheduleIntent, "Schedule", mTabHost, R.drawable.lich_icon);

		// Create tab and intent for Borrowing and Lending.
		Intent borrowAndLendIntent = new Intent(this,
				BorrowLendMainViewActivity.class);
		setupTab(borrowAndLendIntent, "Borrowing\n& Lending", mTabHost,
				R.drawable.small_delete_button);

		// Create tab and intent for report
		Intent reportIntent = new Intent(this, OrcActivity.class);
		setupTab(reportIntent, "Báo cáo", mTabHost, R.drawable.report_icon);
		
		
	}

	public static void setupTab(final Intent intent, final String tag,
			TabHost mTabHost, final int resourceId) {
		View tabview = createTabView(mTabHost.getContext(), resourceId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);
		mTabHost.addTab(setContent);
	}

	// Create tab view.
	private static View createTabView(final Context context, final int id) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.main_tab_background, null);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.main_tab_background_icon);
		imageView.setImageResource(id);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_activity, menu);
		return true;
	}
}