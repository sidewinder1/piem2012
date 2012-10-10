package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class EntryMonthView extends LinearLayout {
	private EditText name, cost;
	private LinearLayout chart;
	private ListView entryDayList;

	public EntryMonthView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_category_item, this, true);

		name = (EditText) findViewById(R.id.entry_view_month_item_name);
		cost = (EditText) findViewById(R.id.entry_view_month_item_cost);
		chart = (LinearLayout) findViewById(R.id.entry_view_month_stacked_bar_chart);
		entryDayList = (ListView) findViewById(R.id.entry_view_day_item_list);
	}
}
