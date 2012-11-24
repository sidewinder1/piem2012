package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Arrays;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class SyncSettingActivity extends Activity {
	public static LinearLayout sAccountList;
	private Spinner mScheduleWarn, mScheduleRing, mScheduleRemain, mBorrowWarn,
			mBorrowRing, mBorrowRemain;
	private CheckBox mAutoSync;
	private ArrayList<String> mScheduleWarnArr, mScheduleRemainArr,
			mBorrowWarnArr, mBorrowRemainArr;
	private int currentAdapterIndex;
	private EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_setting);
		sAccountList = (LinearLayout) findViewById(R.id.sync_view_account_list);
		mAutoSync = (CheckBox) findViewById(R.id.sync_auto_checkbox);
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

		mScheduleWarn.setTag(1);
		mScheduleRing.setTag(2);
		mScheduleRemain.setTag(3);
		mBorrowWarn.setTag(4);
		mBorrowRing.setTag(5);
		mBorrowRemain.setTag(6);

		mScheduleWarnArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_schedule_warn_array)));
		mScheduleRemainArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_remain_array)));
		mBorrowWarnArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_borrow_warn_array)));
		mBorrowRemainArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_remain_array)));

		ArrayAdapter<String> ringAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, getResources()
						.getStringArray(R.array.warning_ring_array));
		ringAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRing.setAdapter(ringAdapter);

		mBorrowRing.setAdapter(ringAdapter);

		ArrayAdapter<String> scheduleRemainAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mScheduleRemainArr);
		scheduleRemainAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mScheduleRemain.setAdapter(scheduleRemainAdapter);

		ArrayAdapter<String> borrowRemainAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mBorrowRemainArr);
		borrowRemainAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBorrowRemain.setAdapter(borrowRemainAdapter);

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

		mAutoSync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				initializeStatusOfAccountList(arg1);

				if (arg1) {
					if (AccountProvider.getInstance().getAccounts().size() != 0) {
						if (!"pfm.com".equals(AccountProvider.getInstance()
								.getCurrentAccount().type)) {
							try {
								PfmApplication.startSynchronize();
							} catch (Exception e) {
								Logger.Log(e.getMessage(),
										"SyncSettingActivity");
							}
							return;
						}

						EmailAccountCustomView firstAccount = (EmailAccountCustomView) sAccountList
								.getChildAt(0);
						if (firstAccount != null) {
							firstAccount.setActive(true);
							AccountProvider.getInstance().setCurrentAccount(
									firstAccount.getText());
						}

						ScrollView scroll = new ScrollView(getBaseContext());
						LinearLayout list = new LinearLayout(getBaseContext());
						scroll.setLayoutParams(new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.FILL_PARENT));
						scroll.addView(list, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
						list.setOrientation(LinearLayout.VERTICAL);

						bindAccount(list, true);

						((EmailAccountCustomView) list.getChildAt(0))
								.setActive(true);

						new AlertDialog.Builder(arg0.getContext())
								.setTitle(
										getResources().getString(
												R.string.input_dialog_title))
								// .setMessage(message)
								.setView(scroll)
								.setPositiveButton(
										getResources().getString(R.string.ok),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface arg0,
													int arg1) {
												try {
													PfmApplication.startSynchronize();
												} catch (Exception e) {
													Logger.Log(e.getMessage(),
															"SyncSettingActivity");
												}
											}
										})
								.setNegativeButton(
										getResources().getString(
												R.string.cancel),
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												mAutoSync.setChecked(false);
											}
										}).show();
					} else {
						Alert.getInstance().showDialog(
								arg0.getContext(),
								getResources().getString(
										R.string.sync_no_account_message),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										addNewAccount(null);
									}
								}, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										mAutoSync.setChecked(false);
									}
								});
					}
				} else {
					try {
						PfmApplication.stopSynchronize();
					} catch (Exception e) {
						Logger.Log(e.getMessage(), "SyncSettingsActivity");
					}
				}
			}
		});
	}

	/**
	 * @param isAutoSync
	 */
	private void initializeStatusOfAccountList(boolean isAutoSync) {
		for (int index = 0; index < sAccountList.getChildCount(); index++) {
			EmailAccountCustomView email = (EmailAccountCustomView) sAccountList
					.getChildAt(index);
			if (email != null) {
				email.setAutoSync(isAutoSync);
				if (isAutoSync
						&& email.getText().equals(
								AccountProvider.getInstance()
										.getCurrentAccount().name)) {
					SynchronizeTask.sButton = email.getButton();
					email.setActive(true);
				}
			}
		}
	}
	
	private OnItemSelectedListener itemSelected = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long resourceId) {
			if (getResources().getString(R.string.others).equals(
					((TextView) view).getText().toString())) {
				currentAdapterIndex = Integer.parseInt(String.valueOf(parent
						.getTag()));
				LayoutParams inputParams = new LayoutParams(0,
						LayoutParams.WRAP_CONTENT, 1);
				LayoutParams textParams = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0);
				String unit = currentAdapterIndex == 1 ? " %" : getResources()
						.getString(
								currentAdapterIndex == 4 ? R.string.hours
										: R.string.minutes);
				TextView unitText = new TextView(getBaseContext());
				unitText.setText(unit);
				input = new EditText(getBaseContext());
				LinearLayout content = new LinearLayout(getBaseContext());
				content.addView(input, inputParams);
				content.addView(unitText, textParams);
				input.setInputType(InputType.TYPE_CLASS_NUMBER);
				new AlertDialog.Builder(view.getContext())
						.setTitle(
								getResources().getString(
										R.string.input_dialog_title))
						.setView(content)
						.setPositiveButton(
								getResources().getString(R.string.ok),
								itemClicked)
						.setNegativeButton(
								getResources().getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).show();
			} else if (position == parent.getCount()) {
				// TODO: Show folder to user select a music file.

			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private DialogInterface.OnClickListener itemClicked = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
			String value = input.getText().toString();
			switch (currentAdapterIndex) {
			case 1:
				mScheduleWarnArr.add(mScheduleWarnArr.size() - 1,
						new StringBuilder(value).append(" %").toString());
				mScheduleWarn.setSelection(mScheduleWarnArr.size() - 2);
				((ArrayAdapter<?>) mScheduleWarn.getAdapter())
						.notifyDataSetChanged();
				break;
			case 3:
				mScheduleRemainArr.add(
						mScheduleRemainArr.size() - 1,
						new StringBuilder(value)
								.append(" ")
								.append(getResources().getString(
										R.string.minutes)).toString());
				mScheduleRemain.setSelection(mScheduleRemainArr.size() - 2);
				((ArrayAdapter<?>) mScheduleRemain.getAdapter())
						.notifyDataSetChanged();
				break;
			case 4:
				mBorrowWarnArr.add(
						mBorrowWarnArr.size() - 1,
						new StringBuilder(value)
								.append(" ")
								.append(getResources()
										.getString(R.string.hours)).toString());
				mBorrowWarn.setSelection(mBorrowWarnArr.size() - 2);
				((ArrayAdapter<?>) mBorrowWarn.getAdapter())
						.notifyDataSetChanged();
				break;
			case 6:
				mBorrowRemainArr.add(
						mBorrowRemainArr.size() - 1,
						new StringBuilder(value)
								.append(" ")
								.append(getResources().getString(
										R.string.minutes)).toString());
				mBorrowRemain.setSelection(mBorrowRemainArr.size() - 2);
				((ArrayAdapter<?>) mBorrowRemain.getAdapter())
						.notifyDataSetChanged();
				break;
			}
		}
	};

	@Override
	protected void onResume() {
		try {
			bindAccount(sAccountList, false);

			if (Boolean.parseBoolean(XmlParser.getInstance().getConfigContent(
					"autoSync"))) {
				mAutoSync.setChecked(true);
				initializeStatusOfAccountList(true);
			}

			super.onResume();
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "SyncSetting.onResume()");
		}
	}

	@Override
	protected void onPause() {
		// TODO: Save user's setting to DB and start user's configuration.
		SqlHelper.instance.update("AppInfo", new String[] { "Status" },
				new String[] { "0" }, "Status = 1");
		if (mAutoSync.isChecked()) {
			SqlHelper.instance
					.update("AppInfo", new String[] { "Status" },
							new String[] { "1" }, "UserName = '"
									+ AccountProvider.getInstance()
											.getCurrentAccount().name + "'");
		}

		XmlParser.getInstance().setConfigContent("autoSync",
				String.valueOf(mAutoSync.isChecked()));
		super.onPause();
	}

	public void addNewAccount(View view) {
		startActivity(new Intent(Settings.ACTION_ADD_ACCOUNT));
	}

	private void bindAccount(LinearLayout accountList, boolean isSubList) {
		AccountProvider.getInstance().refreshAccount();

		if (AccountProvider.getInstance().getAccounts().size() == 0) {
			findViewById(R.id.sync_no_data).setVisibility(View.VISIBLE);
			accountList.setVisibility(View.GONE);
			return;
		} else {
			findViewById(R.id.sync_no_data).setVisibility(View.GONE);
			accountList.setVisibility(View.VISIBLE);
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		accountList.removeAllViews();

		for (Account account : AccountProvider.getInstance().getAccounts()) {
			EmailAccountCustomView email = new EmailAccountCustomView(this,
					isSubList);
			email.setEmailAccount(account.name);
			email.setActive(AccountProvider.getInstance().getCurrentAccount() != null
					&& account.name.equals(AccountProvider.getInstance()
							.getCurrentAccount().name));
			accountList.addView(email, params);
		}
	}
}
