package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.ExcelHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import android.os.Bundle;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

/**
 * @author Kaminari.hp
 *
 */
public class HomeActivity extends TabActivity {
	private final String mTypeTabPathId = "type.tab.path.id";
	public static int sCurrentTab = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);

		// All of code blocks for initialize view should be placed here.
		TabHost mTabHost = getTabHost();
		// mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);

		// Create Expense & income tab.
		Intent managementIntent = new Intent(this, MainViewActivity.class);
		managementIntent.putExtra(mTypeTabPathId, 0);
		setupTab(managementIntent, "Expenses\n& Incomes", mTabHost, R.drawable.tab_bg_selector_entry);

		// Create tab and intent for schedule.
		Intent scheduleIntent = new Intent(this, MainViewActivity.class);
		scheduleIntent.putExtra(mTypeTabPathId, 1);
		setupTab(scheduleIntent, "Schedule", mTabHost, R.drawable.tab_bg_selector_schedule);

		// Create tab and intent for Borrowing and Lending.
		Intent borrowAndLendIntent = new Intent(this, BorrowLendMainViewActivity.class);
		setupTab(borrowAndLendIntent, "Borrowing\n& Lending", mTabHost, R.drawable.tab_bg_selector_borrow);

		// Create tab and intent for report
		Intent reportIntent = new Intent(this, ReportMainViewActivity.class);
		setupTab(reportIntent, "Báo cáo", mTabHost, R.drawable.tab_bg_selector_report);
		
		mTabHost.setCurrentTab(sCurrentTab);
		Alert.getInstance().stopNotify();
	}

	
	/**
	 * Remind user when he has a debt is expired.
	 * @param context
	 * Parent data context.
	 * @param intent
	 * Parent intent.
	 */
	public void onReceive(Context context, Intent intent) {
	    NotificationManager nm = (NotificationManager)
	    context.getSystemService(Context.NOTIFICATION_SERVICE);
	    Notification notification = new Notification();
	    notification.tickerText = "10 Minutes past";
	    nm.notify(0, notification);
	}
	
	/**
	 * Initialize tabs for activity tab.
	 * @param intent
	 * Type of intent of tab.
	 * @param tag
	 * The name of tabs that should be displayed on tab.
	 * @param mTabHost
	 * The tab host parent that contains tabs.
	 * @param resourceId
	 * Resource id of image on tab.
	 */
	public static void setupTab(final Intent intent, final String tag,
			TabHost mTabHost, final int resourceId) {
		View tabview = createTabView(mTabHost.getContext(), resourceId);
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview);
		setContent.setContent(intent);
		mTabHost.addTab(setContent);
	}

	/**
	 * Create a tab view with specified resource id image.
	 * @param context
	 * Parent data context.
	 * @param id
	 * Resource id image.
	 * @return
	 * The view that is displayed on tab.
	 */
	private static View createTabView(final Context context, final int id) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.main_tab_background, null);
		// view.setBackgroundResource(id);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.main_tab_background_icon);
		imageView.setImageResource(id);
		return view;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_activity, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent sync = new Intent(this, SyncSettingActivity.class);
			startActivity(sync);
			break;
		case R.id.menu_export:
			try {
				ExcelHelper.getInstance().exportData("data.xls");
				Alert.getInstance().show(this, getResources().getString(R.string.saved));
			} catch (Exception e) {
				Alert.getInstance().show(this, getResources().getString(R.string.error_load));
				Logger.Log(e.getMessage(), "HomeViewActivity");
			}
			break;
		case R.id.menu_import:
			Intent explorerIntent = new Intent(PfmApplication.getAppContext(), FileExplorerActivity.class);
			explorerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			explorerIntent.putExtra("FileExplorerActivity.allowedExtension", new String[] { ".xls" });
			explorerIntent.putExtra("FileExplorerActivity.file_icon", R.drawable.xls_file_icon);
			PfmApplication.getAppContext().startActivity(explorerIntent);
			break;
		}

		return true;
	}
}