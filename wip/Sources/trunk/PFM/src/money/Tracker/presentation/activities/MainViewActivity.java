package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.ViewHelper;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MainViewActivity extends TabActivity {
	private int tab_type;
	private final String path = "type.tab.path.id";
	private final String sub_path = "type.tab.path.id.subtab";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_view);

		TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);

		// Get bundle from HomeActivity
		Bundle extras = getIntent().getExtras();
		tab_type = extras.getInt(path);

		// If tab_type equals 0 then it is Management tab, else it is Schedule
		// tab.
		String firstTabTitle = (tab_type == 0 ? getResources().getString(
				R.string.entry_expense_title) : getResources().getString(
				R.string.schedule_monthly));
		String secondTabTitle = (tab_type == 0 ? getResources().getString(
				R.string.entry_income_title) : getResources().getString(
				R.string.schedule_weekly));

		// Set title of screen.
		TextView title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getString(
				(tab_type == 0) ? R.string.entry_management_title
						: R.string.schedule_title));

		// Create tab and intent for schedule.
		Intent monthlyIntent = new Intent(this, TabViewActivity.class);
		monthlyIntent.putExtra(sub_path, "1" + tab_type);
		setupTab(monthlyIntent, firstTabTitle, mTabHost);

		// Create tab and intent for Borrowing and Lending.
		Intent weeklyIntent = new Intent(this, TabViewActivity.class);
		weeklyIntent.putExtra(sub_path, "0" + tab_type);
		ViewHelper.setupTab(weeklyIntent, secondTabTitle, mTabHost);

		// Bind clicked event to add new button.
		Button addSchedule = (Button) findViewById(R.id.addSchedule);

		addSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (tab_type == 0) {
					Intent entryEditIntent = new Intent(MainViewActivity.this,
							EntryEditActivity.class);
					entryEditIntent.putExtra("entry_id2", -1);
					startActivityForResult(entryEditIntent, 100);
				} else {
					Intent scheduleEditIntent = new Intent(
							MainViewActivity.this, ScheduleEditActivity.class);
					scheduleEditIntent.putExtra("schedule_id", -1);
					startActivityForResult(scheduleEditIntent, 100);
				}
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

}
