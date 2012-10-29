package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleViewAdapter extends ArrayAdapter<IModelBase> {
	private ArrayList<IModelBase> schedules;

	public ScheduleViewAdapter(Context context, int resource,
			ArrayList<IModelBase> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		schedules = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScheduleViewItem scheduleItemView = (ScheduleViewItem) convertView;

		if (scheduleItemView == null) {
			scheduleItemView = new ScheduleViewItem(getContext());
		}

		final Schedule schedule = (Schedule) schedules.get(position);

		if (schedule != null) {
			// Set content to item title:
			final TextView item_title = ((ScheduleViewItem) scheduleItemView).schedule_item_title;
			if (schedule.type == 1) {
				item_title.setText(DateFormat.format("MMMM yyyy",
						schedule.end_date));
			} else {
				item_title.setText(new StringBuilder(DateFormat.format("dd/MM",
						schedule.start_date))
						.append("-")
						.append(DateFormat.format("dd/MM/yyyy",
								schedule.end_date)).toString());
			}

			// Set content to budget
			final TextView budget = ((ScheduleViewItem) scheduleItemView).total_budget;
			budget.setText(Converter.toString(schedule.budget));

			scheduleItemView.stacked_bar_chart.removeAllViews();

			// Prepare and display stacked bar chart:
			for (int i = 0; i < schedule.details.size(); i++) {
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT,
						Float.parseFloat(schedule.details.get(i).getBudget()
								+ ""));
				Cursor categoryCursor = SqlHelper.instance.select("Category",
						"Id, User_Color", "Id = "
								+ schedule.details.get(i).getCategory());

				if (categoryCursor != null && categoryCursor.moveToFirst()) {
					stackItem.setBackgroundColor(Color
							.parseColor(categoryCursor.getString(1)));
				}

				scheduleItemView.stacked_bar_chart.addView(stackItem, params);
			}
		}

		return scheduleItemView;
	}
}
