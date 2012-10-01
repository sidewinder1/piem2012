package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.adapters.ScheduleDetailViewAdapter;
import money.Tracker.presentation.model.DetailSchedule;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleDetailViewActivity extends Activity {
	ScheduleDetailViewAdapter detailAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view_detail);

		Bundle extras = getIntent().getExtras();
		int schedule_id = extras.getInt("schedule_id");

		Cursor schedule = SqlHelper.instance.select("Schedule",
				"id, budget, start_date, end_date, time_id", "id = "
						+ schedule_id);

		if (schedule != null && schedule.moveToFirst()) {
			String format = schedule.getInt(4) == 1 ? "dd/MM/yyyy"
					: "MMMM dd, yyyy";
			TextView main_title = (TextView) findViewById(R.id.schedule_detail_main_title);
			main_title.setText(new StringBuilder(Converter.toString(
					Converter.toDate(schedule.getString(2)), format))
					.append("-")
					.append(Converter.toString(
							Converter.toDate(schedule.getString(2)), format))
					.toString());

			TextView budget_value = (TextView) findViewById(R.id.schedule_detail_budget_value);
			budget_value.setText(String.valueOf(schedule.getDouble(1)));

			// bind data to list item
			ListView list = (ListView) findViewById(R.id.schedule_detail_item_list);
			ArrayList<DetailSchedule> data = new ArrayList<DetailSchedule>();
			Cursor detail_schedule = SqlHelper.instance.select(
					"ScheduleDetail", "category_id, budget, schedule_id",
					"schedule_id = " + schedule_id);
			if (detail_schedule != null && detail_schedule.moveToFirst()) {
				do {
					data.add(new DetailSchedule(detail_schedule.getInt(0),
							detail_schedule.getDouble(1), detail_schedule
									.getInt(2)));
				} while (detail_schedule.moveToNext());

				detailAdapter = new ScheduleDetailViewAdapter(this,
						R.layout.schedule_edit_item, data);

				detailAdapter.notifyDataSetChanged();
				list.setAdapter(detailAdapter);
			}

		}

	}

	public void editBtnClicked(View v) {
		finish();
	}
}
