package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.EntryMonthView;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class EntryViewAdapter extends ArrayAdapter<IModelBase> {
	private ArrayList<IModelBase> entries;

	public EntryViewAdapter(Context context, int resource,
			ArrayList<IModelBase> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		entries = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EntryMonthView entryMonthView = (EntryMonthView) convertView;

		if (entryMonthView == null) {
			entryMonthView = new EntryMonthView(getContext());
		}

		final Entry entry = (Entry) entries.get(position);

		if (entry != null) {
			// Set content to item title:
			entryMonthView.setName(Converter.toString(entry.getDate(), "MMMM, yyyy"));

			// Set content to budget
			entryMonthView.setCost(Converter.toString(entry.getTotal()));

			entryMonthView.getChart().removeAllViews();

			// Prepare and display stacked bar chart:
//			for (int i = 0; i < entry.getEntryDetails().size(); i++) {
//				View stackItem = new View(getContext());
//				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//						0, LayoutParams.FILL_PARENT,
//						Float.parseFloat(entry.getEntryDetails().get(i).getMoney() + ""));
//				Cursor categoryCursor = SqlHelper.instance.select("Category",
//						"Id, User_Color", "Id = "
//								+ entry.getEntryDetails().get(i).getCategory_id());
//
//				if (categoryCursor != null && categoryCursor.moveToFirst()) {
//					stackItem.setBackgroundColor(Color
//							.parseColor(categoryCursor.getString(1)));
//				}
//
//				entryMonthView.getChart().addView(stackItem, params);
//			}
		}

		return entryMonthView;
	}
}
