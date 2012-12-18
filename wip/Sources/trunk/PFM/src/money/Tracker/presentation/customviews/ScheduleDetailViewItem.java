package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ScheduleDetailViewItem extends LinearLayout {
	public TextView title, budget;

	public ScheduleDetailViewItem(Context context) {
		super(context);
		initializeComponent();
	}
	
	public ScheduleDetailViewItem(Context context, String name, String detailBudget) {
		super(context);
		initializeComponent();
		title.setText(name);
		budget.setText(detailBudget);
	}

	private void initializeComponent() {
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.schedule_detail_item, this, true);

		title = (TextView) findViewById(R.id.schedule_detail_item_budget_title);
		budget = (TextView) findViewById(R.id.schedule_detail_item_budget_value);
	}
}
