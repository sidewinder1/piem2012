package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ScheduleDetailViewActivity;
import money.Tracker.presentation.activities.ScheduleMainViewActivity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleViewItem extends LinearLayout {
	public TextView schedule_item_title, total_budget;
	public LinearLayout stacked_bar_chart;
	Intent viewDetail;
	
	public ScheduleViewItem(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.schedule_view_item, this, true);

		
		schedule_item_title = (TextView)findViewById(R.id.schedule_item_name);
		total_budget = (TextView)findViewById(R.id.schedule_total_budget);
		stacked_bar_chart = (LinearLayout)findViewById(R.id.stacked_bar_chart);
	}
}
