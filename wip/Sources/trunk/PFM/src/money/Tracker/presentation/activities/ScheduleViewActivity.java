package money.Tracker.presentation.activities;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.repository.ScheduleRepository;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScheduleViewActivity extends Activity {
	Intent scheduleEditIntent;
	Button addSchedule;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);
		// new ScheduleRepository();
		
		addSchedule = (Button) findViewById(R.id.addSchedule);
		scheduleEditIntent = new Intent(this, ScheduleEditActivity.class);
		addSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivityForResult(scheduleEditIntent, 100);
			}
		});
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	private void bindData() {
		Cursor data = SqlHelper.instance.select("Schedule",
				"Budget,Start_date,End_date", null);

		if (data != null) {
			if (data.moveToFirst()) {
				do {
					double budget = data
							.getFloat(data.getColumnIndex("Budget"));
					String a = budget + "";
				} while (data.moveToNext());
			}
		}
	}
}
