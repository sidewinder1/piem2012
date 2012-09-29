package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.repository.DataManager;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleViewActivity extends Activity {
	Intent scheduleEditIntent;
	TextView displayText;
	
	private ScheduleViewAdapter scheduleAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);
		displayText = (TextView) findViewById(R.id.no_data_edit);
		Button addSchedule = (Button) findViewById(R.id.addSchedule);
		scheduleEditIntent = new Intent(this, ScheduleEditActivity.class);
		addSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(scheduleEditIntent, 100);
			}
		});

		bindData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		ArrayList<Object> values = DataManager.getObjects("Schedule");
		if (values.size() == 0) {
			displayText.setVisibility(View.VISIBLE);
			return;
		}

		displayText.setVisibility(View.GONE);
		scheduleAdapter = new ScheduleViewAdapter(this,
				R.layout.schedule_edit_item, values);
		
		scheduleAdapter.notifyDataSetChanged();
		final ListView list = (ListView) findViewById(R.id.schedule_view_list);
		list.setAdapter(scheduleAdapter);
	}
}
