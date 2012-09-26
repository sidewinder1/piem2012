package money.Tracker.presentation.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ScheduleViewActivity extends Activity {
	 Intent scheduleEditIntent;
	 
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_view);
        Button addSchedule = (Button)findViewById(R.id.addSchedule);
        scheduleEditIntent = new Intent(this, ScheduleEditActivity.class);
        addSchedule.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(scheduleEditIntent);
			}
		});
    }
}
