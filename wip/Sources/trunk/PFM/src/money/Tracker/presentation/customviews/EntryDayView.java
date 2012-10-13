package money.Tracker.presentation.customviews;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.EntryDetailViewActivity;
import money.Tracker.presentation.activities.EntryEditActivity;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.Entry;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryDayView extends LinearLayout {
	private TextView name, cost;
	private LinearLayout chart;
	public int id;
	
	public EntryDayView(Context context, Entry entry) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_view_day_item, this, true);

		name = (TextView) findViewById(R.id.entry_view_day_item_name);
		cost = (TextView) findViewById(R.id.entry_view_day_item_cost);
		chart = (LinearLayout) findViewById(R.id.entry_view_day_stacked_bar_chart);

		if (entry != null) {
			id = entry.getId();
			// Set content to item title:
			setName(Converter.toString(entry.getDate(),
					"EEEE, dd/MM/yyyy"));

			// Set content to budget
			setCost(Converter.toString(entry.getTotal()));

			getChart().removeAllViews();

			// Prepare and display stacked bar chart:
			for (int i = 0; i < entry.getEntryDetails().size(); i++) {
				String keyCategory = String.valueOf(entry.getEntryDetails()
						.keySet().toArray()[i]);
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT, Float.parseFloat(entry
								.getTotal(Integer.parseInt(keyCategory)) + ""));
				Cursor categoryCursor = SqlHelper.instance.select("Category",
						"Id, User_Color",
						new StringBuilder("Id = ").append(keyCategory)
								.toString());

				if (categoryCursor != null && categoryCursor.moveToFirst()) {
					stackItem.setBackgroundColor(Color
							.parseColor(categoryCursor.getString(1)));
				}

				getChart().addView(stackItem, params);
			}
			
			setOnLongClickListener(new OnLongClickListener() {
				
				public boolean onLongClick(View v) {
					Intent edit = new Intent(getContext(), EntryEditActivity.class);
					edit.putExtra("entry_id", ((EntryDayView)v).id);
					getContext().startActivity(edit);
					return false;
				}
			});
			
			setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent detail = new Intent(getContext(), EntryDetailViewActivity.class);
					detail.putExtra("entry_id", ((EntryDayView)v).id);
					getContext().startActivity(detail);
				}
			});
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
}
