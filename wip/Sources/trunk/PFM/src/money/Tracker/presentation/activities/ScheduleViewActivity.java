package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.repository.DataManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleViewActivity extends Activity {
	TextView displayText;
	LinearLayout chart_legend;

	private ScheduleViewAdapter scheduleAdapter;

	boolean isMonthly;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);

		Bundle extras = getIntent().getExtras();
		isMonthly = extras.getBoolean("Monthly");

		displayText = (TextView) findViewById(R.id.no_data_edit);

		chart_legend = (LinearLayout) findViewById(R.id.chart_legend);

		bindData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		String whereCondition;
		if (isMonthly) {
			whereCondition = "For_Month = 1";
		} else {
			whereCondition = "For_Month = 0";
		}
		
		ArrayList<Object> values = DataManager.getObjects("Schedule",
				whereCondition);
		if (values.size() == 0) {
			chart_legend.setVisibility(View.GONE);
			displayText.setVisibility(View.VISIBLE);
			return;
		}

		displayText.setVisibility(View.GONE);
		chart_legend.setVisibility(View.VISIBLE);
		scheduleAdapter = new ScheduleViewAdapter(this,
				R.layout.schedule_edit_item, values);

		scheduleAdapter.notifyDataSetChanged();
		final ListView list = (ListView) findViewById(R.id.schedule_view_list);
		list.setAdapter(scheduleAdapter);
	}
}
