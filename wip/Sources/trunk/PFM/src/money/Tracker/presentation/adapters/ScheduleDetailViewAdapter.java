package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.presentation.customviews.ScheduleDetailViewItem;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ScheduleDetailViewAdapter extends ArrayAdapter<DetailSchedule> {
	public ArrayList<DetailSchedule> array;

	public ScheduleDetailViewAdapter(Context context, int textViewResourceId,
			ArrayList<DetailSchedule> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		array = objects;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ScheduleDetailViewItem scheduleItemView = (ScheduleDetailViewItem) convertView;

		if (scheduleItemView == null) {
			scheduleItemView = new ScheduleDetailViewItem(getContext());
		}

		final DetailSchedule scheduleDetail = (DetailSchedule) array
				.get(position);

		if (scheduleDetail != null) {
			scheduleItemView.budget.setText(String.valueOf(scheduleDetail
					.getBudget()));
			scheduleItemView.title.setText(CategoryRepository.getInstance()
					.getName((scheduleDetail.getCategory())));
			scheduleItemView.setBackgroundColor(Color
					.parseColor(CategoryRepository.getInstance().getColor(
							scheduleDetail.getCategory())));
		}

		return scheduleItemView;
	}
}
