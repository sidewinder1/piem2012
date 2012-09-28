package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.presnetation.model.Schedule;
import money.Tracker.repository.DataManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ScheduleViewActivity extends Activity {
	Intent scheduleEditIntent;
TextView displayText;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);
		displayText = (TextView)findViewById(R.id.no_data_edit);
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
	
	private void bindData()
	{
		ArrayList<Object> values = DataManager.getObjects("Schedule");
		if (values.size() == 0)
		{
			return;
		}
		
		displayText.setText("");
		for (Object value : values)
		{
			Schedule schedule = (Schedule)value;
			if (schedule != null)
			{
				displayText.setText(displayText.getText().toString() +
						"- From: " + schedule.start_date +
						", To: " + schedule.end_date + ", Total Budget: "+ schedule.budget + "\n");
			}
		}
	}
}
