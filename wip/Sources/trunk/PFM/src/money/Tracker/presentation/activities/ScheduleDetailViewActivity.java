package money.Tracker.presentation.activities;

import java.util.Date;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ScheduleDetailViewItem;
import money.Tracker.repository.CategoryRepository;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleDetailViewActivity extends BaseActivity {
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
			LinearLayout list = (LinearLayout) findViewById(R.id.schedule_detail_item_list);
			Cursor detail_schedule = SqlHelper.instance.select(
					"ScheduleDetail", "category_id, budget, schedule_id, Id",
					"schedule_id = " + schedule_id);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			if (detail_schedule != null && detail_schedule.moveToFirst()) {
				list.removeAllViews();

				do {
					list.addView(
							new ScheduleDetailViewItem(getBaseContext(),
							CategoryRepository.getInstance().getName(
									detail_schedule.getLong(0)),
									detail_schedule.getString(1)), params);
				} while (detail_schedule.moveToNext());
			}

			Date end_date = Converter.toDate(schedule.getString(3));
			Date start_date = Converter.toDate(schedule.getString(2));
			average.setText(new StringBuilder(
					Converter.toString(schedule.getLong(1)
							/ (((end_date.getTime() - start_date.getTime()) / 86400000) + 1)))
					.toString());
		}
	}

	public void editBtnClicked(View v) {
		Intent edit = new Intent(this, ScheduleEditActivity.class);
		edit.putExtra("schedule_id", schedule_id);
		startActivity(edit);
	}
}
