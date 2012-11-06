package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.NfcHelper;
import money.Tracker.presentation.customviews.EntryEditCategoryView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import money.Tracker.repository.EntryDetailRepository;
import money.Tracker.repository.EntryRepository;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EntryEditActivity extends NfcDetectorActivity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private TextView title;
	private static final int DATE_DIALOG_ID = 0;
	private EditText mDateEdit;
	private ToggleButton mEntryType;
	private int mPassedEntryId = -1;
	private LinearLayout mEntryList;
	private static boolean sIsSaveCached;
	private static LinkedHashMap<String, ArrayList<EntryDetail>> sCachedData;
	private NdefMessage[] msgs;

	private final LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
			LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_edit);
		sIsSaveCached = true;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("entry_id")) {
				mPassedEntryId = extras.getInt("entry_id");
			}

			
			if (extras.containsKey("nfc_entry_id")) {
				ArrayList<String> nfcList = extras
						.getStringArrayList("nfc_entry_id");
				if (nfcList != null && nfcList.size() != 0) {
					if (sCachedData == null) {
						sCachedData = new LinkedHashMap<String, ArrayList<EntryDetail>>();
					}

					for (String nfc : nfcList) {
						ArrayList<EntryDetail> array = new ArrayList<EntryDetail>();
						array.add(getEntryDetail(nfc));
						sCachedData.put(String.valueOf(sCachedData.size()),
								array);
						//TODO: delete after checking.
						Logger.Log("On create:", "EntryEdit.add nfc item.");
					}
					//TODO: delete after checking.
					Logger.Log("On create:", "EntryEdit");
					
					Alert.getInstance().show(
							this,
							getResources().getString(
									R.string.entry_detect_record).replace(
									"{0}", String.valueOf(nfcList.size())));
				}
			}
//			
//			// TODO: Hardcode for testing.
//			if (sCachedData == null) {
//				sCachedData = new HashMap<String, ArrayList<EntryDetail>>();
//			}
//			ArrayList<EntryDetail> array = new ArrayList<EntryDetail>();
//			array.add(getEntryDetail("ten: Banh quy\n gia: 120000000"));
//			sCachedData.put(String.valueOf(sCachedData.size()),
//					array);
		}

		mEntryList = (LinearLayout) findViewById(R.id.entry_edit_list);
		title = (TextView) findViewById(R.id.entry_edit_tilte);
		mDateEdit = (EditText) findViewById(R.id.entry_edit_date);
		mDateEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		mEntryType = (ToggleButton) findViewById(R.id.entry_edit_type);
		mEntryType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				updateTitle();
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// New Mode
		if (mPassedEntryId == -1) {
			updateDisplay();
			if (sCachedData != null && sCachedData.size() != 0) {
				for (ArrayList<EntryDetail> entryDetail : sCachedData.values()) {
					mEntryList.addView(new EntryEditCategoryView(this,
							entryDetail), mParams);
				}
			} else {
				mEntryList.addView(new EntryEditCategoryView(this, null),
						mParams);
			}
		} else { // Edit mode
			Entry entry = (Entry) EntryRepository.getInstance()
					.getData("Id = " + mPassedEntryId).get(0);
			if (entry != null) {
				mDateEdit.setText(Converter.toString(entry.getDate(),
						"dd/MM/yyyy"));

				mEntryType.setChecked(entry.getType() == 1);
			}

			HashMap<String, ArrayList<EntryDetail>> values = EntryDetailRepository
					.getInstance().updateData("Entry_Id = " + mPassedEntryId,
							"Category_Id");
			for (ArrayList<EntryDetail> entryDetail : values.values()) {
				mEntryList.addView(
						new EntryEditCategoryView(this, entryDetail), mParams);
			}
		}

		updateTitle();
	}

	private void getQRCode() {
		ZXingLibConfig zxingLibConfig = new ZXingLibConfig();
		zxingLibConfig.useFrontLight = true;

		IntentIntegrator.initiateScan(EntryEditActivity.this, zxingLibConfig);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case IntentIntegrator.REQUEST_CODE:
			IntentResult scanResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, data);
			if (scanResult == null) {
				return;
			}
			final String result = scanResult.getContents();
			if (result != null) {

				String[] _result = result.split("\n");
				String nameProduct = _result[0].substring(14);
				String price = _result[1].substring(5, _result[1].length() - 3)
						.replace(".", "");
				
				
				EntryDetail entryDetail = new EntryDetail();
				entryDetail.setEntry_id(1);
				entryDetail.setName(nameProduct);
				entryDetail.setMoney(Converter.toDouble(price.trim()));
				ArrayList<EntryDetail> dataEntryDetail = new ArrayList<EntryDetail>();
				dataEntryDetail.add(entryDetail);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

				mEntryList.addView(new EntryEditCategoryView(this,
						dataEntryDetail), params);
				// txtScanResult.setText(result);
			}

			break;
		}
	}
	
	private void updateTitle() {
		title.setText(getResources()
				.getString(
						((mPassedEntryId != -1) ? (mEntryType.isChecked() ? R.string.entry_edit_expense_title
								: R.string.entry_edit_income_title)
								: (mEntryType.isChecked() ? R.string.entry_new_expense_title
										: R.string.entry_new_income_title)))
				.replace("{0}", mDateEdit.getText().toString()));
	}

	public void doneBtnClicked(View v) {
		if (save()) {
			CategoryRepository.getInstance().updateData();
			setResult(100);
			this.finish();
		}
	}

	private String checkBeforeSave() {
		for (int index = 0; index < mEntryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) mEntryList
					.getChildAt(index);
			String temp = item.checkBeforeSave();
			if (item != null && temp != null) {
				return temp;
			}
		}

		return null;
	}

	@Override
	public void onBackPressed() {
		sIsSaveCached = false;
		super.onBackPressed();
	};

	private boolean save() {
		String temp = checkBeforeSave();
		if (temp != null) {
			Alert.getInstance().show(this, temp);
			return false;
		}

		int type = mEntryType.isChecked() ? 1 : 0;
		Date inputDate = Converter.toDate(String.valueOf(mDateEdit.getText()),
				"dd/MM/yyyy");
		if (inputDate.after(new Date())) {
			Alert.getInstance().show(this, "Not the future!");
			return false;
		}

		String date = Converter.toString(inputDate);
		String table = "Entry";
		String subTable = "EntryDetail";
		long id = mPassedEntryId;

		if (mPassedEntryId == -1) {
			Cursor oldEntry = SqlHelper.instance.select("Entry", "Id",
					new StringBuilder("Date = '").append(date).append("'")
							.append(" AND Type = ").append(type).toString());
			if (oldEntry != null && oldEntry.moveToFirst()) {
				id = oldEntry.getInt(0);
			} else {
				id = SqlHelper.instance.insert(table, new String[] { "Date",
						"Type" }, new String[] { date, String.valueOf(type) });
			}
		} else {
			SqlHelper.instance.update(table, new String[] { "Date", "Type" },
					new String[] { date, String.valueOf(type) },
					new StringBuilder("Id = ").append(mPassedEntryId)
							.toString());
			SqlHelper.instance.delete(subTable,
					new StringBuilder("Entry_Id = ").append(mPassedEntryId)
							.toString());
		}

		for (int index = 0; index < mEntryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) mEntryList
					.getChildAt(index);
			if (item != null) {
				item.save(id);
			}
		}

		Alert.getInstance().show(this, "Save successfully!");
		sIsSaveCached = false;
		return true;
	}

	public void cancelBtnClicked(View v) {
		sIsSaveCached = false;
		setResult(100);
		this.finish();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		Date startDate = DateTimeHelper.getDate(mYear, mMonth, mDay);
		mDateEdit.setText(Converter.toString(startDate, "dd/MM/yyyy"));
		updateTitle();
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}

		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.entry_edit_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.entry_edit_get_QR_Code:
			getQRCode();
			return true;
		case R.id.entry_edit_get_NFC:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private EntryDetail getEntryDetail(String tag) {
		String[] strs = tag.split("\n");
		EntryDetail value = new EntryDetail();
		try {
			for (String str : strs) {
				if (str.toLowerCase().contains("name")
						|| str.toLowerCase().contains("ten")) {
					value.setName(str.split(":")[1].trim());
				} else {
					if (str.toLowerCase().contains("price")
							|| str.toLowerCase().contains("gia")) {
						String money = str.split(":")[1];
						money = money.replace(".", "").replace(",", ".").trim();
						value.setMoney(Double.parseDouble(money));
					} else {
						if (str.toLowerCase().contains("category")
								|| str.toLowerCase().contains("loai")) {
							value.setCategory_id(CategoryRepository
									.getInstance().getId(
											str.split(":")[1].trim()));
						} else {
							value.setCategory_id(CategoryRepository
									.getInstance().getId("Others"));
						}
					}
				}
			}
		} catch (Exception e) {
			Alert.getInstance().show(this,
					getResources().getString(R.string.entry_unformated_data));
		}

		return value;
	}

	@Override
	protected void onNfcFeatureNotFound() {
		//TODO: Hardcode to test.
//		ArrayList<String> nfcData = new ArrayList<String>();
//		nfcData.add("ten: Bim bim\n gia: 2.500,00");
//		nfcData.add("ten: My tom\n gia: 4.710,00");
//		nfcData.add("ten: Trung ga\n gia: 3.340,00");
//		
//		Intent edit = new Intent(this, EntryEditActivity.class);
//		edit.putExtra("nfc_entry_id", nfcData);
//		startActivity(edit);
	}

	@Override
	protected void onNfcFeatureFound() {
		Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
				NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			ArrayList<EntryDetail> nfcData = new ArrayList<EntryDetail>();
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];

				for (NdefRecord record : msgs[i].getRecords()) {
					String[] result = NfcHelper.parse(record).getTag()
							.split("(v|V)(n|N)(d|D)");
					for (String string : result) {
						nfcData.add(getEntryDetail(string));
					}
				}
			}
			
			mEntryList.addView(new EntryEditCategoryView(this, nfcData));
		}
	}
	
	public void nfcIntentDetected(Intent intent, String action) {
	}
}
