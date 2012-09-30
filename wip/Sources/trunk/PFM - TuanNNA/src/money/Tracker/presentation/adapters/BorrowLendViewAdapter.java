package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import java.util.Random;

import money.Tracker.presentation.customviews.BorrowLendViewItem;
import money.Tracker.presentation.customviews.ScheduleViewItem;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.presentation.model.Schedule;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BorrowLendViewAdapter extends ArrayAdapter<Object> {
	private ArrayList<Object> borrowLends;

	public BorrowLendViewAdapter(Context context, int resource,
			ArrayList<Object> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		borrowLends = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BorrowLendViewItem blViewItem = (BorrowLendViewItem) convertView;

		if (blViewItem == null) {
			blViewItem = new BorrowLendViewItem(getContext());
		}

		final BorrowLend borrowLend = (BorrowLend) borrowLends.get(position);

		if (borrowLend != null) {
			// Set content to person name:
			final TextView personNameTV = ((BorrowLendViewItem) blViewItem).personName;
			personNameTV.setText(borrowLend.getPersonName());
			
			// Set content to money
			final TextView moneyTV = ((BorrowLendViewItem) blViewItem).money;
			moneyTV.setText(String.valueOf(borrowLend.getMoney()));
			
			// Set content to start date
			final TextView startDateTV = ((BorrowLendViewItem) blViewItem).startDate;
			startDateTV.setText(String.valueOf(borrowLend.getStartDate()));
			
			// Set content to expired date
			final TextView expiredDateTV = ((BorrowLendViewItem) blViewItem).expiredDate;
			expiredDateTV.setText(String.valueOf(borrowLend.getExpiredDate()));
		}

		return blViewItem;
	}
}
