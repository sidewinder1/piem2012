package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EntryMonthView extends LinearLayout {
	private TextView name;
	private TextView cost;
	private LinearLayout chart;
	private ListView entryDayList;

	public EntryMonthView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_view_month_item, this, true);

		name = (TextView) findViewById(R.id.entry_view_month_item_name);
		cost = (TextView) findViewById(R.id.entry_view_month_item_cost);
		chart = (LinearLayout) findViewById(R.id.entry_view_month_stacked_bar_chart);
		entryDayList = (ListView) findViewById(R.id.entry_view_day_item_list);
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

	public ListView getEntryDayList() {
		return entryDayList;
	}

	public void setEntryDayList(ListView entryDayList) {
		this.entryDayList = entryDayList;
	}
}
