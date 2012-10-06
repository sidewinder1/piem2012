package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.customviews.BorrowLendViewItemActivity;
import money.Tracker.presentation.model.BorrowLend;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

public class BorrowLendAdapter extends ArrayAdapter<Object> {
	private Context mContext;
	private ArrayList<Object> arrayListBorrowLend;
	
	public BorrowLendAdapter(Context context, int resource, ArrayList<Object> object) {
		super(context, resource, object);
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.arrayListBorrowLend = object;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return arrayListBorrowLend.size();
	}

	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return arrayListBorrowLend.get(pos);
	}

	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// get selected entry
		BorrowLend entry = (BorrowLend) arrayListBorrowLend.get(pos);
		BorrowLendViewItemActivity borrowLendViewItem = (BorrowLendViewItemActivity) convertView;
		//borrowLendViewItem.setClickable(true);
		//borrowLendViewItem.setFocusable(true);
		
		if(borrowLendViewItem == null)
		{
			borrowLendViewItem = new BorrowLendViewItemActivity(mContext);
		}
	    
		borrowLendViewItem.personNameTextView.setText(entry.getPersonName());
		borrowLendViewItem.moneyTextView.setText(String.valueOf(entry.getMoney()));
		Log.d("Check number", String.valueOf(entry.getMoney()));
		borrowLendViewItem.startDateTextView.setText(Converter.toString(entry.getStartDate(), "MMM dd, yyyy"));
		if (entry.getExpiredDate() != null)
			borrowLendViewItem.expiredDateTextView.setText(Converter.toString(entry.getExpiredDate(), "MMM dd, yyyy"));
		else
			borrowLendViewItem.expiredDateTextView.setText("");
		
		return borrowLendViewItem;
	}
}
