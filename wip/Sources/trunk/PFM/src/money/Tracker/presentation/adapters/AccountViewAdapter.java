package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.presentation.customviews.EmailAccountCustomView;

import android.accounts.Account;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class AccountViewAdapter extends ArrayAdapter<Account> {
	ArrayList<Account> mAccounts;

	public AccountViewAdapter(Context context, int textViewResourceId,
			ArrayList<Account> objects) {
		super(context, textViewResourceId, objects);
		mAccounts = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EmailAccountCustomView view = (EmailAccountCustomView) convertView;

		if (view == null) {
			view = new EmailAccountCustomView(getContext());
		}
		
		view.setEmailAccount(mAccounts.get(position).name);

		return view;
	}
}
