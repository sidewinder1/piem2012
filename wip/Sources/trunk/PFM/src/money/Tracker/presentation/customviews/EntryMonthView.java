package money.Tracker.presentation.customviews;

import java.util.ArrayList;
import java.util.HashMap;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.Entry;
import money.Tracker.repository.EntryRepository;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class EntryMonthView extends LinearLayout {
	private TextView name;
	private TextView cost;
	private LinearLayout chart;
	private LinearLayout entryDayList;
	private boolean switcher = false;
	
	public EntryMonthView(Context context, String keyMonth) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_view_month_item, this, true);

		name = (TextView) findViewById(R.id.entry_view_month_item_name);
		cost = (TextView) findViewById(R.id.entry_view_month_item_cost);
		chart = (LinearLayout) findViewById(R.id.entry_view_month_stacked_bar_chart);
		entryDayList = (LinearLayout) findViewById(R.id.entry_view_day_item_list);
		
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				entryDayList.setVisibility(switcher ? View.VISIBLE : View.GONE);
				switcher = !switcher;
			}
		});
		
		// Set content to item title:
		setName(keyMonth);

		ArrayList<Entry> entrySet = (ArrayList<Entry>) EntryRepository.getInstance().orderedEntries.get(keyMonth);

		if (entrySet != null) {
			HashMap<Integer, Double> totalCategory = new HashMap<Integer, Double>();
			double total = 0;
			entryDayList.removeAllViews();
			for (Entry entry : entrySet) {
				//if (totalCategory.containsKey())
				addToEntryDayList(new EntryDayView(getContext(), entry));
			}
			
			// Set content to budget
			// entryMonthView.setCost(Converter.toString(entrySet.getTotal()));

			// entryMonthView.getChart().removeAllViews();

			// Prepare and display stacked bar chart:
			// for (int i = 0; i < entry.getEntryDetails().size(); i++) {
			// View stackItem = new View(getContext());
			// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
			// 0, LayoutParams.FILL_PARENT,
			// Float.parseFloat(entry.getEntryDetails().get(i).getMoney() +
			// ""));
			// Cursor categoryCursor = SqlHelper.instance.select("Category",
			// "Id, User_Color", "Id = "
			// + entry.getEntryDetails().get(i).getCategory_id());
			//
			// if (categoryCursor != null && categoryCursor.moveToFirst()) {
			// stackItem.setBackgroundColor(Color
			// .parseColor(categoryCursor.getString(1)));
			// }
			//
			// entryMonthView.getChart().addView(stackItem, params);
			// }
		}
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

	public LinearLayout getEntryDayList() {
		return entryDayList;
	}

	public void addToEntryDayList(LinearLayout entryDayView) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				 LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		this.entryDayList.addView(entryDayView, params);
	}
}
