package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.adapters.ScheduleDetailViewAdapter;
import money.Tracker.presentation.model.DetailSchedule;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleDetailViewActivity extends Activity {
	ScheduleDetailViewAdapter detailAdapter;
	long schedule_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view_detail);

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("schedule_id")) {
			schedule_id = extras.getLong("schedule_id");
		}
		
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		Cursor schedule = SqlHelper.instance
				.select("Schedule", "id, budget, start_date, end_date, Type",
						"id = " + schedule_id);

		if (schedule != null && schedule.moveToFirst()) {
			TextView average = (TextView) findViewById(R.id.schedule_avg_budget_value);
			String format = "dd/MM/yyyy";
			TextView main_title = (TextView) findViewById(R.id.schedule_detail_main_title);
			main_title.setText(new StringBuilder(Converter.toString(
					Converter.toDate(schedule.getString(2)), format))
					.append("-")
					.append(Converter.toString(
							Converter.toDate(schedule.getString(3)), format))
					.toString());

			TextView budget_value = (TextView) findViewById(R.id.schedule_detail_budget_value);
			budget_value.setText(Converter.toString(schedule.getLong(1)));

			// bind data to list item
			ListView list = (ListView) findViewById(R.id.schedule_detail_item_list);
			ArrayList<DetailSchedule> data = new ArrayList<DetailSchedule>();
			Cursor detail_schedule = SqlHelper.instance.select(
					"ScheduleDetail", "category_id, budget, schedule_id, Id",
					"schedule_id = " + schedule_id);

			if (detail_schedule != null && detail_schedule.moveToFirst()) {
				do {
					data.add(new DetailSchedule(detail_schedule.getLong(3),
							detail_schedule.getLong(0), detail_schedule
									.getLong(1), detail_schedule.getLong(2)));
				} while (detail_schedule.moveToNext());

				detailAdapter = new ScheduleDetailViewAdapter(this,
						R.layout.schedule_edit_item, data);

				detailAdapter.notifyDataSetChanged();
				list.setAdapter(detailAdapter);
			}

			Date end_date = Converter.toDate(schedule.getString(3));
			average.setText(new StringBuilder(Converter.toString(schedule
					.getLong(1)
					/ DateTimeHelper.getDayOfMonth(end_date.getYear(),
							end_date.getMonth()))).append("/day"));
		}
	}

	public void editBtnClicked(View v) {
		Intent edit = new Intent(this, ScheduleEditActivity.class);
		edit.putExtra("schedule_id", schedule_id);
		startActivity(edit);
	}
}
