package money.Tracker.presentation.customviews;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ReportViewActivity;
import money.Tracker.presentation.activities.ReportViewBarChartActivity;
import money.Tracker.presentation.activities.ReportViewPieChartActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

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
