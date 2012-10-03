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

public class ScheduleMainViewActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_main_view);

		TabHost mTabHost = getTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider);

		// Create tab and intent for schedule.
		Intent monthlyIntent = new Intent(this, ScheduleViewActivity.class);
		monthlyIntent.putExtra("Monthly", true);
		setupTab(monthlyIntent, "Monthly", mTabHost);

		// Create tab and intent for Borrowing and Lending.
		Intent weeklyIntent = new Intent(this, ScheduleViewActivity.class);
		weeklyIntent.putExtra("Monthly", false);
		ViewHelper.setupTab(weeklyIntent, "Weekly", mTabHost);

		// Bind clicked event to add new button.
		Button addSchedule = (Button) findViewById(R.id.addSchedule);

		addSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent scheduleEditIntent = new Intent(
						ScheduleMainViewActivity.this,
						ScheduleEditActivity.class);
				scheduleEditIntent.putExtra("schedule_id", -1);
				startActivityForResult(scheduleEditIntent, 100);
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
