package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SyncSettingActivity extends Activity {
	private LinearLayout mAccountList;
	private Spinner mScheduleWarn, mScheduleRing, mScheduleRemain, mBorrowWarn,
			mBorrowRing, mBorrowRemain;

	private String[] mScheduleWarnArr, mScheduleRingArr,
			mScheduleRemainArr, mBorrowWarnArr, mBorrowRingArr,
			mBorrowRemainArr;

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
		mScheduleWarn.setOnItemSelectedListener(itemSelected);
		mScheduleRing.setOnItemSelectedListener(itemSelected);
		mScheduleRemain.setOnItemSelectedListener(itemSelected);
		mBorrowWarn.setOnItemSelectedListener(itemSelected);
		mBorrowRing.setOnItemSelectedListener(itemSelected);
		mBorrowRemain.setOnItemSelectedListener(itemSelected);

		mScheduleWarnArr = getResources()
				.getStringArray(R.array.warning_schedule_warn_array);
		mScheduleRingArr = getResources()
				.getStringArray(R.array.warning_ring_array);
		mScheduleRemainArr = getResources()
				.getStringArray(R.array.warning_remain_array);
		mBorrowWarnArr = getResources()
				.getStringArray(R.array.warning_borrow_warn_array);
		mBorrowRingArr = getResources()
				.getStringArray(R.array.warning_ring_array);
		mBorrowRemainArr = getResources()
				.getStringArray(R.array.warning_remain_array);
		
		ArrayAdapter<String> ringAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mScheduleRingArr);
		ringAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRing.setAdapter(ringAdapter);
		mBorrowRing.setAdapter(ringAdapter);

		ArrayAdapter<String> remainAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mScheduleRemainArr);
		remainAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRemain.setAdapter(remainAdapter);
		mBorrowRemain.setAdapter(remainAdapter);

		ArrayAdapter<String> scheduleWarnAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mScheduleWarnArr);
		scheduleWarnAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleWarn.setAdapter(scheduleWarnAdapter);

		ArrayAdapter<String> borrowWarnAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mBorrowWarnArr);
		borrowWarnAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBorrowWarn.setAdapter(borrowWarnAdapter);
	}

	ArrayAdapter<String> currentAdapter;
	EditText input;
	private OnItemSelectedListener itemSelected = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long resourceId) {
			if (getResources().getString(R.string.others).equals(
					((TextView) view).getText().toString())) {
				currentAdapter = (ArrayAdapter<String>) parent.getAdapter();
				input = new EditText(getBaseContext());
				new AlertDialog.Builder(view.getContext())
						.setTitle(
								getResources().getString(
										R.string.input_dialog_title))
						// .setMessage(message)
						.setView(input)
						.setPositiveButton("Ok", itemClicked)
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).show();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private DialogInterface.OnClickListener itemClicked = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String value = input.getText().toString();
//			ArrayAdapter<String> scheduleWarnAdapter = new ArrayAdapter<String>(
//					getBaseContext(), android.R.layout.simple_spinner_item,
//					getResources().getStringArray(
//							R.array.warning_schedule_warn_array));
//			scheduleWarnAdapter.add(value);
//			scheduleWarnAdapter
//					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//			mScheduleWarn.setAdapter(scheduleWarnAdapter);
			// currentAdapter.setSelection(0);
		}
	};

	@Override
	protected void onResume() {
		try {
			bindAccount();
			super.onResume();
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "SyncSetting.onResume()");
		}
	}

	public void addNewAccount(View view) {

	}

	private void bindAccount() {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mAccountList.removeAllViews();
		for (Account account : AccountProvider.getInstance().getAccounts()) {
			EmailAccountCustomView email = new EmailAccountCustomView(this);
			email.setEmailAccount(account.name);
			email.setStatus(getResources()
					.getString(
							AccountProvider.getInstance().currentAccount != null
									&& account.name.equals(AccountProvider
											.getInstance().currentAccount.name) ? R.string.sync_view_account_active
									: R.string.sync_view_account_deactive));
			mAccountList.addView(email, params);
		}
	}
}
