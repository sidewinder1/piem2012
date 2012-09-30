package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.R.layout;
import money.Tracker.presentation.activities.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BorrowLendViewItem extends LinearLayout {
	public TextView personName, money;
	public TextView startDate, expiredDate;
	
	public BorrowLendViewItem(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_borrow_lend_view_item, this, true);

		personName = (TextView) findViewById(R.id.borrow_lend_person_name);
		money = (TextView) findViewById(R.id.borrow_lend_money);
		startDate = (TextView) findViewById(R.id.borrow_lend_start_date);
		expiredDate = (TextView) findViewById(R.id.borrow_lend_expired_date);
	}
	
}
