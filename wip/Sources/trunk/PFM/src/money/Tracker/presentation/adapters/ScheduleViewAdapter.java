package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import java.util.Random;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.Schedule;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleViewAdapter extends ArrayAdapter<Object> {
	private ArrayList<Object> schedules;

	public ScheduleViewAdapter(Context context, int resource,
			ArrayList<Object> objects) {
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
			item_title.setText(DateFormat.format("MMMM yyyy", schedule.end_date));
			
			// Set content to budget
			final TextView budget = ((ScheduleViewItem) scheduleItemView).total_budget;
			budget.setText(String.valueOf(schedule.budget));
			scheduleItemView.stacked_bar_chart.removeAllViews();
			Random random = new Random();
			// Prepare and display stacked bar chart:
			for (int i=0; i <schedule.details.size(); i++)
			{
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					    0, LayoutParams.FILL_PARENT, 
					    Float.parseFloat(schedule.details.get(i).getBudget() + ""));
				stackItem.setBackgroundColor(Color.argb(200, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
				scheduleItemView.stacked_bar_chart.addView(stackItem, params);
			}
		}

		return scheduleItemView;
	}
}
