package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Arrays;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.ViewHelper;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import money.Tracker.presentation.customviews.SyncSettingInputDialogView;
import android.accounts.Account;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
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
	private Spinner mScheduleWarn, // mScheduleRemind, mLanguage,
			mBorrowWarn, mBorrowRing, mBorrowRemind;
	private CheckBox mAutoSync;
	private ArrayList<String> mScheduleWarnArr, mScheduleRemindArr,
			mBorrowWarnArr, mBorrowRemindArr, mLanguageArr, mBorrowRingArr;
	private int currentAdapterIndex;
	private EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sync_setting);
		sAccountList = (LinearLayout) findViewById(R.id.sync_view_account_list);
		mAutoSync = (CheckBox) findViewById(R.id.sync_auto_checkbox);
		mScheduleWarn = (Spinner) findViewById(R.id.warning_schedule_warn_before);
		// TODO: remove language feature.
		// mLanguage = (Spinner) findViewById(R.id.general_setting_language);
		// mScheduleRemind = (Spinner)
		// findViewById(R.id.warning_schedule_remain);
		mBorrowWarn = (Spinner) findViewById(R.id.warning_borrow_warn_before);
		mBorrowRing = (Spinner) findViewById(R.id.warning_borrow_ring);
		mBorrowRemind = (Spinner) findViewById(R.id.warning_borrow_remain);

		mScheduleWarn.setOnItemSelectedListener(itemSelected);
		// mLanguage.setOnItemSelectedListener(itemSelected);
		// mScheduleRemind.setOnItemSelectedListener(itemSelected);
		mBorrowWarn.setOnItemSelectedListener(itemSelected);
		mBorrowRing.setOnItemSelectedListener(itemSelected);
		mBorrowRemind.setOnItemSelectedListener(itemSelected);

		mScheduleWarn.setTag(1);
		// mLanguage.setTag(2);
		// mScheduleRemind.setTag(3);
		mBorrowWarn.setTag(4);
		mBorrowRing.setTag(5);
		mBorrowRemind.setTag(6);

		mScheduleWarnArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_schedule_warn_array)));
		mScheduleRemindArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_remain_array)));
		mBorrowWarnArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_borrow_warn_array)));
		mBorrowRemindArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_remain_array)));
		mBorrowRingArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.warning_ring_array)));
		mLanguageArr = new ArrayList<String>(Arrays.asList(getResources()
				.getStringArray(R.array.languages)));

		ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mLanguageArr);
		languageAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ArrayAdapter<String> borrowRingAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mBorrowRingArr);
		borrowRingAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// mLanguage.setAdapter(languageAdapter);

		mBorrowRing.setAdapter(borrowRingAdapter);

		ArrayAdapter<String> scheduleRemindAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mScheduleRemindArr);
		scheduleRemindAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// mScheduleRemind.setAdapter(scheduleRemindAdapter);

		ArrayAdapter<String> borrowRemindAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mBorrowRemindArr);
		borrowRemindAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mBorrowRemind.setAdapter(borrowRemindAdapter);

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
								LayoutParams.FILL_PARENT, 250));
						scroll.addView(list, new LayoutParams(
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT));
						list.setOrientation(LinearLayout.VERTICAL);

						bindAccount(list, true);

						((EmailAccountCustomView) list.getChildAt(0))
								.setActive(true);
						final Dialog dialog = ViewHelper.createAppDialog(
								arg0.getContext(),
								R.string.choose_account_dialog_title, scroll);

						ViewHelper.attachAction(dialog,
								new View.OnClickListener() {
									public void onClick(View v) {
										try {
											PfmApplication.startSynchronize();
										} catch (Exception e) {
											Logger.Log(e.getMessage(),
													"SyncSettingActivity");
										}

										dialog.dismiss();
									}
								}, new View.OnClickListener() {
									public void onClick(View v) {
										mAutoSync.setChecked(false);
										dialog.dismiss();
									}
								});
						dialog.show();
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
					// SynchronizeTask.sButton = email.getButton();
					email.setActive(true);
				}
			}
		}
	}

	private void initializeWarningSetting() {
		Cursor warningSetting = SqlHelper.instance
				.select("AppInfo",
						new StringBuilder(
								"ScheduleWarn, ScheduleRing, ScheduleRemind,")
								.append("BorrowWarn, BorrowRing, BorrowRemind, Language")
								.toString(),
						new StringBuilder("UserName='")
								.append(AccountProvider.getInstance()
										.getCurrentAccount().name).append("'")
								.toString());

		if (warningSetting != null && warningSetting.moveToFirst()) {
			getIndexFromStringArray(warningSetting.getString(0), getResources()
					.getString(R.string.percent), mScheduleWarnArr,
					mScheduleWarn);
			// getIndexFromStringArray(warningSetting.getString(6), "",
			// mLanguageArr, mLanguage);
			// getIndexFromStringArray(warningSetting.getString(2), " "
			// + getResources().getString(R.string.minutes),
			// mScheduleRemindArr, mScheduleRemind);
			getIndexFromStringArray(warningSetting.getString(3), " "
					+ getResources().getString(R.string.hours), mBorrowWarnArr,
					mBorrowWarn);
			getIndexFromStringArray(warningSetting.getString(4), "",
					mBorrowRingArr, mBorrowRing);
			getIndexFromStringArray(warningSetting.getString(5), " "
					+ getResources().getString(R.string.minutes),
					mBorrowRemindArr, mBorrowRemind);
		}
	}

	private void getIndexFromStringArray(String key, String unit,
			ArrayList<String> list, Spinner parentSpinner) {

		int valueIndex = -1;
		// if (parentSpinner == mLanguage) {
		// valueIndex = 0;
		// for (String code :
		// getResources().getStringArray(R.array.language_codes)){
		// if (code.equals(key)){
		// break;
		// }
		//
		// valueIndex++;
		// }
		// } else
		{
			int div = getResources().getString(R.string.hours).equals(
					unit.trim()) ? 60 : 1;
			if (key.startsWith("#")) {
				if ("#NONE".equals(key)) {
					valueIndex = 0;
				} else {
					valueIndex = 1;
				}
			} else {
				for (int index = 0; index < list.size(); index++) {
					if (key.equals(getUnit(list.get(index), div))) {
						valueIndex = index;
					}
				}
			}
		}

		if (valueIndex == -1) {
			createSpinnerItem(key, unit, parentSpinner, list);
		} else {
			parentSpinner.setSelection(valueIndex);
			((ArrayAdapter<?>) parentSpinner.getAdapter())
					.notifyDataSetChanged();
		}
	}

	private OnItemSelectedListener itemSelected = new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long resourceId) {
			currentAdapterIndex = Integer.parseInt(String.valueOf(parent
					.getTag()));
			// Select ring of application.
			if (currentAdapterIndex == 5) {
				if (position == parent.getCount() - 1) {
					Intent i = new Intent();
					i.setAction(Intent.ACTION_GET_CONTENT);
					i.setType("audio/*");
					startActivityForResult(
							Intent.createChooser(i, "Select audio file"),
							106 + currentAdapterIndex);
					return;
				}

				switch (position) {
				case 0:
					// None.
					updateConfig(currentAdapterIndex == 2 ? "ScheduleRing"
							: "BorrowRing", "#NONE");
					break;
				case 1:
					// default ring.
					updateConfig(currentAdapterIndex == 2 ? "ScheduleRing"
							: "BorrowRing", "#DEFAULT");
					break;
				}

				return;
			}

			if (view == null) {
				return;
			}

			// Select time that before warning or reminding.
			if (getResources().getString(R.string.others).equals(
					((TextView) view).getText().toString())) {
				int unitType = currentAdapterIndex == 1 ? SyncSettingInputDialogView.SCHEDULE_WARNING_BEFORE
						: (currentAdapterIndex == 4 ? SyncSettingInputDialogView.BORROW_WARNING_BEFORE
								: SyncSettingInputDialogView.WARNING_REMIND);

				SyncSettingInputDialogView dialogView = new SyncSettingInputDialogView(
						getBaseContext(), unitType);
				input = dialogView.getInputValue();

				final Dialog dialog = new Dialog(view.getContext(),
						R.style.CustomDialogTheme);

				dialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface arg0) {
						initializeWarningSetting();
					}
				});
				
				dialog.setContentView(dialogView);
				dialogView.setPositiveButton(new View.OnClickListener() {
					public void onClick(View v) {
						positiveAction();
						dialog.dismiss();
					}
				});

				dialogView.setNegativeButton(new View.OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();
			} else {
				switch (currentAdapterIndex) {
				case 1:
					updateConfig("ScheduleWarn", mScheduleWarnArr.get(position)
							.split(" ")[0]);
					break;
				case 2:
					// Localization.
					updateConfig(
							"Language",
							getResources().getStringArray(
									R.array.language_codes)[position]);
					Alert.getInstance().show(
							view.getContext(),
							getResources().getString(
									R.string.update_after_close_app));
					break;
				case 3:
					updateConfig("ScheduleRemind",
							getUnit(mScheduleRemindArr.get(position), 1));
					break;
				case 4:
					updateConfig("BorrowWarn",
							getUnit(mBorrowWarnArr.get(position), 60));
					break;
				case 6:
					updateConfig("BorrowRemind",
							getUnit(mBorrowRemindArr.get(position), 1));
					break;
				}
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	private String getUnit(String valueOfItem, int div) {
		int unit = 1;
		String[] valuesOfItem = valueOfItem.split(" ");
		long value = 0;
		if (valuesOfItem.length != 2
				|| getResources().getString(R.string.others).equals(
						valuesOfItem[0])) {
			return valueOfItem;
		}

		value = Converter.toLong(valuesOfItem[0]);
		if (getResources().getString(R.string.hours).equals(valuesOfItem[1])) {
			unit = 60 / div;
		} else if (getResources().getString(R.string.days).equals(
				valuesOfItem[1])) {
			unit = 1440 / div;
		} else if (getResources().getString(R.string.weeks).equals(
				valuesOfItem[1])) {
			unit = 10080 / div;
		}

		return Converter.toString(value * unit);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && data != null) {
			// User choose a music file to set default notification's ring.
			switch (requestCode) {
			case 108:
				// Ring of Schedule function.
				updateConfig("ScheduleRing", data.getDataString());
				// createSpinnerItem(data.getDataString(), "", mScheduleRing,
				// mScheduleRingArr);
				break;
			case 111:
				// Ring of Borrow function.
				updateConfig("BorrowRing", data.getDataString());
				createSpinnerItem(data.getDataString(), "", mBorrowRing,
						mBorrowRingArr);
				break;
			}
		} else {
			// User cancel choosing.
			initializeWarningSetting();
		}
	}

	private void createSpinnerItem(String path, String unit,
			Spinner parentSpinner, ArrayList<String> parentArray) {
		parentArray.add(parentArray.size() - 1,
				new StringBuilder(path).append(unit).toString());
		parentSpinner.setSelection(parentArray.size() - 2);
		((ArrayAdapter<?>) parentSpinner.getAdapter()).notifyDataSetChanged();
	}

	private void updateConfig(String column, String value) {
		Cursor checkExist = SqlHelper.instance.select(
				"AppInfo",
				"UserName",
				new StringBuilder("UserName='")
						.append(AccountProvider.getInstance()
								.getCurrentAccount().name).append("'")
						.toString());

		if (checkExist == null || !checkExist.moveToFirst()) {
			SqlHelper.instance.insert("AppInfo", new String[] { "UserName",
					"LastSync", "Status", "ScheduleWarn", "ScheduleRing",
					"ScheduleRemind", "BorrowWarn", "BorrowRing",
					"BorrowRemind" }, new String[] {
					AccountProvider.getInstance().getCurrentAccount().name,
					"1990-01-20 00:00:00", "0", "50", "#DEFAULT", "10", "168",
					"#DEFAULT", "10" });
		}

		SqlHelper.instance.update(
				"AppInfo",
				new String[] { column },
				new String[] { value },
				new StringBuilder("UserName='")
						.append(AccountProvider.getInstance()
								.getCurrentAccount().name).append("'")
						.toString());
	}

	private void positiveAction() {
		String value = input.getText().toString();
		switch (currentAdapterIndex) {
		case 1:
			createSpinnerItem(value, " %", mScheduleWarn, mScheduleWarnArr);
			updateConfig("ScheduleWarn", value);
			break;
		case 3:
			// createSpinnerItem(value,
			// " " + getResources().getString(R.string.minutes),
			// mScheduleRemind, mScheduleRemindArr);
			// updateConfig("ScheduleRemind", value);
			break;
		case 4:
			createSpinnerItem(value,
					" " + getResources().getString(R.string.hours),
					mBorrowWarn, mBorrowWarnArr);
			updateConfig("BorrowWarn", value);
			break;
		case 6:
			createSpinnerItem(value,
					" " + getResources().getString(R.string.minutes),
					mBorrowRemind, mBorrowRemindArr);
			updateConfig("BorrowRemind", value);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			bindAccount(sAccountList, false);
			initializeWarningSetting();
			if (Boolean.parseBoolean(XmlParser.getInstance().getConfigContent(
					"autoSync"))) {
				mAutoSync.setChecked(true);
				initializeStatusOfAccountList(true);
			}
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "SyncSetting.onResume()");
		}
	}

	@Override
	protected void onPause() {
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
			email.setAutoSync(mAutoSync.isChecked());
			email.setActive(AccountProvider.getInstance().getCurrentAccount() != null
					&& account.name.equals(AccountProvider.getInstance()
							.getCurrentAccount().name));
			accountList.addView(email, params);
		}
	}
}
