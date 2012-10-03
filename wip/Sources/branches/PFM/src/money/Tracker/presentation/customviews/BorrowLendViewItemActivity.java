package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.R.layout;
import money.Tracker.presentation.activities.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BorrowLendViewItemActivity extends LinearLayout {
	public TextView personNameTextView, moneyTextView;
	public TextView startDateTextView, expiredDateTextView;
	Intent viewDetail;
	
	public BorrowLendViewItemActivity(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_borrow_lend_view_item, this, true);

		personNameTextView = (TextView) findViewById(R.id.borrow_lend_person_name);
		moneyTextView = (TextView) findViewById(R.id.borrow_lend_money);
		startDateTextView = (TextView) findViewById(R.id.borrow_lend_start_date);
		expiredDateTextView = (TextView) findViewById(R.id.borrow_lend_expired_date);
		
		this.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}