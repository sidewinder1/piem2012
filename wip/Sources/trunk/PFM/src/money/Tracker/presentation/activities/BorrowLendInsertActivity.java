package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.adapters.ContactsAutoCompleteCursorAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.*;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BorrowLendInsertActivity extends BaseActivity {
	private int mStartDate_Year;
	private int mStartDate_Month;
	private int mStartDate_Day;
	private int mExpiredDate_Year;
	private int mExpiredDate_Month;
	private int mExpiredDate_Day;
	private static final int DATE_DIALOG_ID = 0;
	private static final int DATE_DIALOG_ID1 = 10;
	private EditText mStartDateEditText;
	private EditText mExpiredDateEditText;
	private Alert mAlert;
	private Button mSaveButton;
	private Button mCancelButton;
	private TextView mDebtTypeTextView;
	private Button mDebtTypeButton;
	private AutoCompleteTextView mNameEditText;
	private EditText mPhoneEditText;
	private EditText mAddressEditText;
	private EditText mMoneyEditText;
	private ToggleButton mInterestType;
	private EditText mInterestRate;
	private ArrayList<String> mColumn;
	private ArrayList<String> mChangedValues;
	private long borrow_lend_id = -1;
	private boolean mIsBorrow = true;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_insert);

		mSaveButton = (Button) findViewById(R.id.saveButton);
		mCancelButton = (Button) findViewById(R.id.cancelBtn);
		mDebtTypeTextView = (TextView) findViewById(R.id.title_text_view);
		mDebtTypeButton = (Button) findViewById(R.id.borrowLendType);
		mNameEditText = (AutoCompleteTextView) findViewById(R.id.name_edit_text);
		mPhoneEditText = (EditText) findViewById(R.id.phone_edit_text);
		mAddressEditText = (EditText) findViewById(R.id.address_edit_text);
		mMoneyEditText = (EditText) findViewById(R.id.money_edit_text);
		mInterestType = (ToggleButton) findViewById(R.id.interestType);
		mInterestRate = (EditText) findViewById(R.id.interest_rate_edit_text);
		mStartDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		mExpiredDateEditText = (EditText) findViewById(R.id.expired_date_edit_text);

		mAlert = new Alert();

		new BorrowLendRepository();

		// get information from view detail intent
		Bundle extras = getIntent().getExtras();

		try {
			borrow_lend_id = extras.getLong("borrowLendID");
		} catch (Exception e) {

		}

		if (borrow_lend_id != -1) {
			BorrowLendRepository bolere = new BorrowLendRepository();
			BorrowLend values = bolere.getDetailData("ID=" + borrow_lend_id);
			
			if (values.getDebtType().toString().equals("Borrowing")) {
				setChecked(true);
				mDebtTypeTextView.setText(getResources().getString(R.string.borrow_edit_title));
			} else {
				setChecked(false);
				mDebtTypeTextView.setText(getResources().getString(R.string.lend_edit_title));
			}
			
			mNameEditText.setText(String.valueOf(values.getPersonName()));
			mPhoneEditText.setText(String.valueOf(values.getPersonPhone()));
			mAddressEditText.setText(String.valueOf(values.getPersonAddress()));
			mMoneyEditText.setText(String.valueOf(values.getMoney()));
			if (values.getInterestType().equals("Simple"))
			{
				mInterestType.setChecked(true);
			}
			else
			{
				mInterestType.setChecked(false);
			}
			
			if (values.getInterestRate() != 0)
				mInterestRate.setText(String.valueOf(values.getInterestRate()));
			
			mStartDateEditText.setText(Converter.toString(values.getStartDate(),"dd/MM/yyyy"));
			
			String [] startDate = Converter.toString(values.getStartDate(),"dd/MM/yyyy").split("/");
			mStartDate_Day = Integer.parseInt(startDate[0]);
			mStartDate_Month = Integer.parseInt(startDate[1]) - 1;
			mStartDate_Year = Integer.parseInt(startDate[2]);
			
			if (values.getExpiredDate() != null)
			{
				mExpiredDateEditText.setText(Converter.toString(values.getExpiredDate(), "dd/MM/yyyy"));
				String [] expiredDate = Converter.toString(values.getExpiredDate(), "dd/MM/yyyy").split("/");
				mExpiredDate_Day = Integer.parseInt(expiredDate[0]);
				mExpiredDate_Month = Integer.parseInt(expiredDate[1]) - 1;
				mExpiredDate_Year = Integer.parseInt(expiredDate[2]);
			}else
			{
				Calendar c = Calendar.getInstance();
				mExpiredDate_Year = c.get(Calendar.YEAR);
				mExpiredDate_Month = c.get(Calendar.MONTH);
				mExpiredDate_Day = c.get(Calendar.DAY_OF_MONTH);
			}
			
			mColumn = new ArrayList<String>();
			mChangedValues = new ArrayList<String>();

			mNameEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Person_name");
					mChangedValues.add(String.valueOf(mNameEditText.getText()));
				}
			});

			mPhoneEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Person_Phone");
					mChangedValues.add(mPhoneEditText.getText().toString());
				}
			});

			mAddressEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Person_address");
					mChangedValues.add(mAddressEditText.getText().toString());
				}
			});

			mMoneyEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Money");
					mChangedValues.add(mMoneyEditText.getText().toString());
				}
			});

			mInterestRate.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Interest_rate");
					mChangedValues.add(mInterestRate.getText().toString());
				}
			});

			mStartDateEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Start_date");
					mChangedValues.add(Converter.toString(Converter.toDate(mStartDateEditText.getText().toString(),"dd/MM/yyyy")));
				}
			});

			mExpiredDateEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					mColumn.add("Expired_date");
					mChangedValues.add(Converter.toString(Converter.toDate(mExpiredDateEditText.getText().toString(),"dd/MM/yyyy")));
				}
			});
		}

		final ContactInfoRepository cont = new ContactInfoRepository(getApplicationContext());
		Cursor contacts = cont.getContacts2(null);
		startManagingCursor(contacts);
		ContactsAutoCompleteCursorAdapter adapter = new ContactsAutoCompleteCursorAdapter(this, contacts);
			mNameEditText.setAdapter(adapter);
			mNameEditText.setThreshold(1);
			mNameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
						String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						mNameEditText.setText(name);
						String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
						mPhoneEditText.setText(number);

						String address = "No data";
						address = cont.getAddress(name);
						mAddressEditText.setText(address);

					}
				});

		mSaveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (borrow_lend_id != -1) {
					updateData();
				} else {
					insertData();
				}

			}
		});

		// Hand on Cancel button
		mCancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				BorrowLendInsertActivity.this.finish();
			}
		});

		// Hand on debt type
		mDebtTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				setChecked(!mIsBorrow);
				mDebtTypeTextView.setText(getResources().getString(borrow_lend_id == -1 ? (mIsBorrow ? R.string.borrow_add_title: R.string.lend_add_title): (mIsBorrow ? R.string.borrow_edit_title: R.string.lend_edit_title)));
			}
		});

		// get the current date
		if (borrow_lend_id == -1)
		{
			final Calendar c = Calendar.getInstance();
			mExpiredDate_Year = mStartDate_Year = c.get(Calendar.YEAR);
			mExpiredDate_Month = mStartDate_Month = c.get(Calendar.MONTH);
			mExpiredDate_Day = mStartDate_Day = c.get(Calendar.DAY_OF_MONTH);
	
			// display the current date (this method is below)
			updateDisplayStartDate();
			// updateDisplayExpiredDate();
		}

		// make dialog for start date
		EditText startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		startDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.d("st", "Check 1");
				showDialog(DATE_DIALOG_ID);
			}
		});

		// make dialog for expired date
		EditText expiredDateEditText = (EditText) findViewById(R.id.expired_date_edit_text);
		expiredDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Log.d("ex", "Check 1");
				showDialog(DATE_DIALOG_ID1);
			}
		});

	}

	
	private void setChecked(boolean isBorrow) {
		mIsBorrow = isBorrow;
		mDebtTypeButton.setBackgroundResource(isBorrow ? R.drawable.borrow_icon : R.drawable.lending_icon);
	}

	// updates the date in the TextView
	private void updateDisplayStartDate() {
		Log.d("st", "Check 2");
		mStartDateEditText.setText(new StringBuilder().append(mStartDate_Day)
				.append("/").append(mStartDate_Month + 1).append("/")
				.append(mStartDate_Year).toString());
	}

	// updates the date in the TextView
	private void updateDisplayExpiredDate() {
		mExpiredDateEditText.setText(new StringBuilder().append(mExpiredDate_Day)
				.append("/").append(mExpiredDate_Month + 1).append("/")
				.append(mExpiredDate_Year).toString());
	}

	// the callback received when the user "sets" the start date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerStartDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mStartDate_Year = year;
			mStartDate_Month = monthOfYear;
			mStartDate_Day = dayOfMonth;
			updateDisplayStartDate();
		}
	};

	/**
	 * the callback received when the user "sets" the expired date in the dialog
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListenerExpiredDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mExpiredDate_Year = year;
			mExpiredDate_Month = monthOfYear;
			mExpiredDate_Day = dayOfMonth;

			String startDateString = mStartDate_Day + "/" + (mStartDate_Month + 1) + "/" + mStartDate_Year;
			Date _startDate = Converter.toDate(startDateString, "dd/MM/yyyy");
			String expiredDateString = mExpiredDate_Day + "/" + (mExpiredDate_Month + 1) + "/" + mExpiredDate_Year;
			Date _expiredDate = Converter.toDate(expiredDateString, "dd/MM/yyyy");
			Long startDate = _startDate.getTime();
			Long expiredDate = _expiredDate.getTime();

			if (expiredDate > startDate || (expiredDate - startDate) == 0) {
				updateDisplayExpiredDate();
			} else {
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_expired_date_less_than_start_date));
				// get the current date
				final Calendar c = Calendar.getInstance();
				mExpiredDate_Year = c.get(Calendar.YEAR);
				mExpiredDate_Month = c.get(Calendar.MONTH);
				mExpiredDate_Day = c.get(Calendar.DAY_OF_MONTH);
			}
		}
	};

	
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mStartDate_Year, mStartDate_Month, mStartDate_Day);
			break;
		case DATE_DIALOG_ID1:
			((DatePickerDialog) dialog).updateDate(mExpiredDate_Year, mExpiredDate_Month, mExpiredDate_Day);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Log.d("st", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerStartDate, mStartDate_Year, mStartDate_Month, mStartDate_Day);
		case DATE_DIALOG_ID1:
			Log.d("ex", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerExpiredDate, mExpiredDate_Year, mExpiredDate_Month, mExpiredDate_Day);
		}
		return null;
	}

	/**
	 *	update data to database after user click save button 
	 */
	
	private void updateData() {
		String interestTypeString = "";
		String debtType = "";
		boolean checkCondition = false;
		
		if (mInterestType.isChecked())
			interestTypeString = "Simple";
		else
			interestTypeString = "Compound";

		mColumn.add("Interest_type");
		mChangedValues.add(interestTypeString);

		if (mIsBorrow)
			debtType = "Borrowing";
		else
			debtType = "Lending";

		mColumn.add("Debt_type");
		mChangedValues.add(debtType);

		String[] columnUpdate = new String[mColumn.size()];
		columnUpdate = mColumn.toArray(columnUpdate);

		String[] valusChangedUpdate = new String[mChangedValues.size()];
		valusChangedUpdate = mChangedValues.toArray(valusChangedUpdate);
		
		if (mNameEditText.getText().toString().equals("") && mMoneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender_total_money));
			else
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower_total_money));
		} else if (mNameEditText.getText().toString().equals("") && !mMoneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender));
			else
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower));
		} else if(!mNameEditText.getText().toString().equals("") && mMoneyEditText.getText().toString().equals(""))
		{
			mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_total_money));
		} else if (!mInterestRate.getText().toString().equals("") && mExpiredDateEditText.getText().toString().equals(""))
		{
			mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_expired_date));
		} else
		{
			checkCondition = true;
		}
		
		if (checkCondition) 
		{
			if (!mInterestRate.getText().toString().trim().equals("")) {
				if (Integer.parseInt(mInterestRate.getText().toString().trim()) == 0) {
					mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_0));
					checkCondition = false;
				} else {
					checkCondition = true;
				}
			}
		}
		
		if (checkCondition)
		{
			if (Long.parseLong(mMoneyEditText.getText().toString().trim()) == 0)
			{
				mAlert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_total_money_0));
				checkCondition = false;
			} else {
				checkCondition = true;
			}
		}
		
		if (checkCondition)
		{
			SqlHelper.instance.update("BorrowLend", columnUpdate, valusChangedUpdate, "ID = " + borrow_lend_id);
			setResult(100);
			try {
				if (!SynchronizeTask.isSynchronizing() && Boolean.parseBoolean(XmlParser.getInstance() .getConfigContent("autoSync")) && !"pfm.com".equals(AccountProvider.getInstance().getCurrentAccount().type)) {
					SynchronizeTask task = new SynchronizeTask();
					task.execute();
				}
			} catch (Exception e) {
				Logger.Log(e.getMessage(), "BorrowLendInsertActivity");
			}
			
			mAlert.show(getApplicationContext(), getResources().getString(R.string.saved));
			
			BorrowLendInsertActivity.this.finish();
		}
	}

	/**
	 * insert data to database after user click save button
	 */
	private void insertData() {
		String interestTypeString = "";
		String debtType = "";
		boolean checkCondition = false;
		
		if (mInterestType.isChecked()) {
			interestTypeString = "Simple";
		} else {
			interestTypeString = "Compound";
		}
		
		if (mIsBorrow) {
			debtType = "Borrowing";
		} else {
			debtType = "Lending";
		}
		
		if (mNameEditText.getText().toString().equals("") && mMoneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender_total_money));
			else
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower_total_money));
		} else if (mNameEditText.getText().toString().equals("") && !mMoneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender));
			else
				mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower));
		} else if(!mNameEditText.getText().toString().equals("") && mMoneyEditText.getText().toString().equals(""))
		{
			mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_total_money));
		} else if (!mInterestRate.getText().toString().equals("") && mExpiredDateEditText.getText().toString().equals(""))
		{
			mAlert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_expired_date));
		} else
		{
			checkCondition = true;
		}
		
		if (checkCondition) 
		{
			if(!mInterestRate.getText().toString().trim().equals(""))
			{
				if (Integer.parseInt(mInterestRate.getText().toString().trim()) == 0) 
				{
					mAlert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_interest_rate_0));
					checkCondition = false;
				} else {
					checkCondition = true;
				}
			}
		}
		
		if (checkCondition)
		{
			if (Long.parseLong(mMoneyEditText.getText().toString().trim()) == 0)
			{
				mAlert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_total_money_0));
				checkCondition = false;
			} else {
				checkCondition = true;
			}
		}
		
		if (checkCondition)
		{
			String expiredDateString = "";
			
			if (!mExpiredDateEditText.getText().toString().equals(""))
				expiredDateString = Converter.toString(Converter.toDate(mExpiredDateEditText.getText().toString(), "dd/MM/yyyy"));
			
			SqlHelper.instance.insert("BorrowLend",
							new String[] { "Debt_type", "Money",
									"Interest_type", "Interest_rate",
									"Start_date", "Expired_date",
									"Person_name", "Person_phone",
									"Person_address" },
							new String[] {
									debtType,
									mMoneyEditText.getText().toString(),
									interestTypeString,
									mInterestRate.getText().toString(),
									Converter.toString(Converter.toDate(mStartDateEditText.getText().toString(), "dd/MM/yyyy")),
									expiredDateString,
									mNameEditText.getText().toString(),
									mPhoneEditText.getText().toString(),
									mAddressEditText.getText().toString() });
			setResult(100);

			try {
				if (!SynchronizeTask.isSynchronizing() && Boolean.parseBoolean(XmlParser.getInstance().getConfigContent("autoSync")) && !"pfm.com".equals(AccountProvider.getInstance().getCurrentAccount().type)) {
					SynchronizeTask task = new SynchronizeTask();
					task.execute();
				}
			} catch (Exception e) {
				Logger.Log(e.getMessage(), "BorrowLendInsertActivity");
			}
			
			mAlert.show(getApplicationContext(), getResources().getString(R.string.saved));
			
			BorrowLendInsertActivity.this.finish();
		}
	}
}
