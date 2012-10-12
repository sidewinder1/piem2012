package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.customviews.EntryMonthView;
import money.Tracker.presentation.model.Entry;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EntryMonthViewAdapter extends ArrayAdapter<String> {
	public HashMap<String, ArrayList<Entry>> entries;

	private EntryDayViewAdapter dayViewAdapter;
	public EntryMonthViewAdapter(Context context, int resource,
			ArrayList<String> objects, HashMap<String, ArrayList<Entry>> entries) {
		super(context, resource, objects);
		this.entries = entries;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryMonthView entryMonthView = (EntryMonthView) convertView;

		if (entryMonthView == null) {
			entryMonthView = new EntryMonthView(getContext());
		}

		String keyMonth = String.valueOf(entries.keySet().toArray()[position]);

		// Set content to item title:
		entryMonthView.setName(keyMonth);

		ArrayList<Entry> entrySet = (ArrayList<Entry>) entries
				.get(keyMonth);

		if (entrySet != null) {
			dayViewAdapter = new EntryDayViewAdapter(getContext(),
					R.layout.entry_view_month_item, entrySet);

			dayViewAdapter.notifyDataSetChanged();
			entryMonthView.getEntryDayList().setAdapter(dayViewAdapter);
			double total = 0;

			// Set content to budget
//			entryMonthView.setCost(Converter.toString(entrySet.getTotal()));

//			entryMonthView.getChart().removeAllViews();

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

		return entryMonthView;
	}
}
