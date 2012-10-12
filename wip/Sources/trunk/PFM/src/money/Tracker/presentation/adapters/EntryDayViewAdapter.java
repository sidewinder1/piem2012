package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.EntryDayView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class EntryDayViewAdapter extends ArrayAdapter<Entry> {
	private ArrayList<Entry> entries;

	public EntryDayViewAdapter(Context context, int resource,
			ArrayList<Entry> objects) {
		super(context, resource, objects);
		entries = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryDayView entryDayView = (EntryDayView) convertView;

		if (entryDayView == null) {
			entryDayView = new EntryDayView(getContext());
		}

		final Entry entry = (Entry) entries.get(position);

		if (entry != null) {
			// Set content to item title:
			entryDayView.setName(Converter.toString(entry.getDate(), "EEEE, dd/MM/yyyy"));

			// Set content to budget
			entryDayView.setCost(Converter.toString(entry.getTotal()));

			entryDayView.getChart().removeAllViews();

			// Prepare and display stacked bar chart:
			for (int i = 0; i < entry.getEntryDetails().size(); i++) {
				String keyCategory = String.valueOf(entry.getEntryDetails().keySet().toArray()[i]);
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT,
						Float.parseFloat(entry.getTotal(Integer.parseInt(keyCategory)) + ""));
				Cursor categoryCursor = SqlHelper.instance.select("Category",
						"Id, User_Color", new StringBuilder("Id = ").append(
								keyCategory).toString());

				if (categoryCursor != null && categoryCursor.moveToFirst()) {
					stackItem.setBackgroundColor(Color
							.parseColor(categoryCursor.getString(1)));
				}

				entryDayView.getChart().addView(stackItem, params);
			}
		}

		return entryDayView;
	}
}