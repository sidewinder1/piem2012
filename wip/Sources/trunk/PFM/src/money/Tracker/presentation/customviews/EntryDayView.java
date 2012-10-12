package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryDayView extends LinearLayout {
	private TextView name, cost;
	private LinearLayout chart;

	public EntryDayView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_view_day_item, this, true);
		
		name = (TextView) findViewById(R.id.entry_view_day_item_name);
		cost = (TextView) findViewById(R.id.entry_view_day_item_cost);
		chart = (LinearLayout) findViewById(R.id.entry_view_day_stacked_bar_chart);
	}

	public String getName() {
		return String.valueOf(name.getText());
	}

	public void setName(String name) {
		this.name.setText(name);
	}

	public String getCost() {
		return String.valueOf(cost.getText());
	}

	public void setCost(String cost) {
		this.cost.setText(cost);
	}

	public LinearLayout getChart() {
		return chart;
	}

	public void setChart(LinearLayout chart) {
		this.chart = chart;
	}
}
