package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BorrowLendViewDetailViewItem extends LinearLayout {
	public BorrowLendViewDetailViewItem(Context context, String name, String value) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_borrow_lend_view_detail_view_item, this, true);
		
		TextView nameTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_item_name);
		TextView valueTextView = (TextView) findViewById(R.id.borrow_lend_detail_view_item_value);
		
		nameTextView.setText(name);
		valueTextView.setText(value);

	}
}
