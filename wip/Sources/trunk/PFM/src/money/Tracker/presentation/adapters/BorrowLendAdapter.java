package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.customviews.BorrowLendViewItemActivity;
import money.Tracker.presentation.model.BorrowLend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BorrowLendAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<BorrowLend> arrayListBorrowLend;
	
	public BorrowLendAdapter(Context context, ArrayList<BorrowLend> object) {
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
		BorrowLend entry = arrayListBorrowLend.get(pos);
		BorrowLendViewItemActivity borrowLendViewItem = (BorrowLendViewItemActivity) convertView;
		if(borrowLendViewItem == null)
		{
			borrowLendViewItem = new BorrowLendViewItemActivity(mContext);
		}
		
		borrowLendViewItem.personNameTextView.setText(entry.getPersonName());
		borrowLendViewItem.moneyTextView.setText(String.valueOf(entry.getMoney()));
		borrowLendViewItem.startDateTextView.setText(String.valueOf(entry.getStartDate()));
		borrowLendViewItem.expiredDateTextView.setText(String.valueOf(entry.getExpiredDate()));
		
		return borrowLendViewItem;
	}

}
