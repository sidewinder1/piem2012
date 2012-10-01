package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.DataManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleViewActivity extends Activity {
	TextView displayText;
	LinearLayout chart_legend;
	ListView list;
	private ScheduleViewAdapter scheduleAdapter;

	boolean isMonthly;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);

		Bundle extras = getIntent().getExtras();
		isMonthly = extras.getBoolean("Monthly");

		displayText = (TextView) findViewById(R.id.no_data_edit);

		chart_legend = (LinearLayout) findViewById(R.id.chart_legend);
		list = (ListView) findViewById(R.id.schedule_view_list);
		list.setOnItemClickListener(onListClick);
		
		bindData();
	}
	
	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Schedule schedule = (Schedule)listView.getAdapter().getItem(position);
			if (schedule != null)
			{
				Intent scheduleDetail =new Intent(ScheduleViewActivity.this ,ScheduleDetailViewActivity.class);
				scheduleDetail.putExtra("schedule_id", schedule.id);
				startActivity(scheduleDetail);
			}
		}
	};

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		String whereCondition;
		if (isMonthly) {
			whereCondition = "Time_Id = 1";
		} else {
			whereCondition = "Time_Id = 0";
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
		
		list.setAdapter(scheduleAdapter);
	}
}
