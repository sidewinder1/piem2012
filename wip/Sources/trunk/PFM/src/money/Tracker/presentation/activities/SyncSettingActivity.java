package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import android.accounts.Account;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;

public class SyncSettingActivity extends Activity {
	private LinearLayout mAccountList;
	private Spinner mScheduleWarn, mScheduleRing, mScheduleRemain, mBorrowWarn,
			mBorrowRing, mBorrowRemain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_setting);
		mAccountList = (LinearLayout) findViewById(R.id.sync_view_account_list);
		mScheduleWarn = (Spinner) findViewById(R.id.warning_schedule_warn_before);
		mScheduleRing = (Spinner) findViewById(R.id.warning_schedule_ring);
		mScheduleRemain = (Spinner) findViewById(R.id.warning_schedule_remain);
		mBorrowWarn = (Spinner) findViewById(R.id.warning_borrow_warn_before);
		mBorrowRing = (Spinner) findViewById(R.id.warning_borrow_ring);
		mBorrowRemain = (Spinner) findViewById(R.id.warning_borrow_remain);

		ArrayAdapter<String> ringAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.warning_ring_array));
		ringAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRing.setAdapter(ringAdapter);
		mBorrowRing.setAdapter(ringAdapter);

		ArrayAdapter<String> remainAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.warning_remain_array));
		remainAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRemain.setAdapter(remainAdapter);
		mBorrowRemain.setAdapter(remainAdapter);
	}

	@Override
	protected void onResume() {
		try {
			bindAccount();
			super.onResume();
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "SyncSetting.onResume()");
		}
	}

	protected void addNewAccount() {

	}

	private void bindAccount() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAccountList.removeAllViews();
		for (Account account : AccountProvider.getInstance().getAccounts()) {
			EmailAccountCustomView email = new EmailAccountCustomView(this);
			email.setEmailAccount(account.name);
			email.setStatus(getResources().getString(
					AccountProvider.getInstance().currentAccount != null &&
			 account.name.equals(AccountProvider.getInstance().currentAccount.name)
			 ? R.string.sync_view_account_active
					 : R.string.sync_view_account_deactive));
			mAccountList.addView(email, params);
		}
	}
}
