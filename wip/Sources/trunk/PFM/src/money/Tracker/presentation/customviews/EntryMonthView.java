package money.Tracker.presentation.customviews;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.EntryRepository;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryMonthView extends LinearLayout {
	private TextView name;
	private TextView cost;
	private LinearLayout chart;
	private LinearLayout entryDayList;
	private TextView count_text;
	private boolean switcher = true;

	public EntryMonthView(Context context) {
		super(context);
	}

	public EntryMonthView(Context context, String keyMonth) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_view_month_item, this, true);

		name = (TextView) findViewById(R.id.entry_view_month_item_name);
		cost = (TextView) findViewById(R.id.entry_view_month_item_cost);
		count_text = (TextView) findViewById(R.id.entry_view_month_count);
		chart = (LinearLayout) findViewById(R.id.entry_view_month_stacked_bar_chart);
		entryDayList = (LinearLayout) findViewById(R.id.entry_view_day_item_list);

		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				entryDayList.setVisibility(switcher ? View.VISIBLE : View.GONE);
				count_text.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
						switcher ? R.drawable.opened : R.drawable.collapsed);
				switcher = !switcher;
			}
		});

		// Set content to item title:
		setName(keyMonth);

		ArrayList<Entry> entrySet = (ArrayList<Entry>) EntryRepository
				.getInstance().orderedEntries.get(keyMonth);
		SparseArray<Double> valueOnCategory = new SparseArray<Double>();
		if (entrySet != null) {
			double total = 0;
			// Set count.
			count_text.setText(new StringBuilder("(").append(entrySet.size()).append(")"));

			// Draw chart.
			entryDayList.removeAllViews();
			for (Entry entry : entrySet) {
				total += entry.getTotal();

				addToEntryDayList(new EntryDayView(getContext(), entry));

				for (EntryDetail entryDetail : entry.getEntryDetails()) {
					if (valueOnCategory
							.indexOfKey(entryDetail.getCategory_id()) < 0) {
						valueOnCategory.put(entryDetail.getCategory_id(), 0D);
					}

					double currentValue = valueOnCategory.get(entryDetail
							.getCategory_id());
					valueOnCategory.setValueAt(valueOnCategory
							.indexOfKey(entryDetail.getCategory_id()),
							currentValue + entryDetail.getMoney());
				}
			}

			// Set content to budget
			setCost(Converter.toString(total));

			getChart().removeAllViews();

			// Prepare and display stacked bar chart:
			for (int i = 0; i < valueOnCategory.size(); i++) {
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT,
						Float.parseFloat(valueOnCategory.valueAt(i) + ""));
				Cursor categoryCursor = SqlHelper.instance.select(
						"Category",
						"Id, User_Color",
						new StringBuilder("Id = ").append(
								valueOnCategory.keyAt(i)).toString());

				if (categoryCursor != null && categoryCursor.moveToFirst()) {
					stackItem.setBackgroundColor(Color
							.parseColor(categoryCursor.getString(1)));
				}

				getChart().addView(stackItem, params);
			}
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
