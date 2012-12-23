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
	private int startDate_Year;
	private int startDate_Month;
	private int startDate_Day;
	private int expiredDate_Year;
	private int expiredDate_Month;
	private int expiredDate_Day;
	private static final int DATE_DIALOG_ID = 0;
	private static final int DATE_DIALOG_ID1 = 10;
	private EditText startDateEditText;
	private EditText expiredDateEditText;
	private Alert alert;
	private Button saveButton;
	private Button cancelButton;
	private TextView debtTypeTextView;
	private Button debtTypeButton;
	private AutoCompleteTextView nameEditText;
	private EditText phoneEditText;
	private EditText addressEditText;
	private EditText moneyEditText;
	private ToggleButton interestType;
	private EditText interestRate;
	private ArrayList<String> column;
	private ArrayList<String> valuesChanged;
	private long borrow_lend_id = -1;
	private boolean mIsBorrow = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_insert);

		saveButton = (Button) findViewById(R.id.saveButton);
		cancelButton = (Button) findViewById(R.id.cancelBtn);
		debtTypeTextView = (TextView) findViewById(R.id.title_text_view);
		debtTypeButton = (Button) findViewById(R.id.borrowLendType);
		nameEditText = (AutoCompleteTextView) findViewById(R.id.name_edit_text);
		phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
		addressEditText = (EditText) findViewById(R.id.address_edit_text);
		moneyEditText = (EditText) findViewById(R.id.money_edit_text);
		interestType = (ToggleButton) findViewById(R.id.interestType);
		interestRate = (EditText) findViewById(R.id.interest_rate_edit_text);
		startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		expiredDateEditText = (EditText) findViewById(R.id.expired_date_edit_text);

		alert = new Alert();

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
				debtTypeTextView.setText(getResources().getString(R.string.borrow_edit_title));
			} else {
				setChecked(false);
				debtTypeTextView.setText(getResources().getString(R.string.lend_edit_title));
			}
			
			nameEditText.setText(String.valueOf(values.getPersonName()));
			phoneEditText.setText(String.valueOf(values.getPersonPhone()));
			addressEditText.setText(String.valueOf(values.getPersonAddress()));
			moneyEditText.setText(Converter.toString(values.getMoney()).replaceAll(",", ""));
			if (values.getInterestType().equals("Simple"))
			{
				interestType.setChecked(true);
			}
			else
			{
				interestType.setChecked(false);
			}
			
			if (values.getInterestRate() != 0)
				interestRate.setText(String.valueOf(values.getInterestRate()));
			
			startDateEditText.setText(Converter.toString(values.getStartDate(),"dd/MM/yyyy"));
			
			String [] startDate = Converter.toString(values.getStartDate(),"dd/MM/yyyy").split("/");
			startDate_Day = Integer.parseInt(startDate[0]);
			startDate_Month = Integer.parseInt(startDate[1]) - 1;
			startDate_Year = Integer.parseInt(startDate[2]);
			Log.d("Check edit borrow lend dialog picker", startDate_Day + " - " + startDate_Month + " - " + startDate_Year);
			
			if (values.getExpiredDate() != null)
			{
				expiredDateEditText.setText(Converter.toString(values.getExpiredDate(), "dd/MM/yyyy"));
				Log.d("Check edit borrow lend", Converter.toString(values.getExpiredDate(), "dd/MM/yyyy"));
				String [] expiredDate = Converter.toString(values.getExpiredDate(), "dd/MM/yyyy").split("/");
				expiredDate_Day = Integer.parseInt(expiredDate[0]);
				expiredDate_Month = Integer.parseInt(expiredDate[1]) - 1;
				expiredDate_Year = Integer.parseInt(expiredDate[2]);
			}else
			{
				Calendar c = Calendar.getInstance();
				expiredDate_Year = c.get(Calendar.YEAR);
				expiredDate_Month = c.get(Calendar.MONTH);
				expiredDate_Day = c.get(Calendar.DAY_OF_MONTH);
			}
			
			Log.d("Check edit borrow lend dialog picker", expiredDate_Day + " - " + expiredDate_Month + " - " + expiredDate_Year);
			
			column = new ArrayList<String>();
			valuesChanged = new ArrayList<String>();

			nameEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Person_name");
					valuesChanged.add(String.valueOf(nameEditText.getText()));
				}
			});

			phoneEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Person_Phone");
					valuesChanged.add(phoneEditText.getText().toString());
				}
			});

			addressEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Person_address");
					valuesChanged.add(addressEditText.getText().toString());
				}
			});

			moneyEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Money");
					valuesChanged.add(moneyEditText.getText().toString());
				}
			});

			interestRate.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Interest_rate");
					valuesChanged.add(interestRate.getText().toString());
				}
			});

			startDateEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Start_date");
					valuesChanged.add(Converter.toString(Converter.toDate(startDateEditText.getText().toString(),"dd/MM/yyyy")));
				}
			});

			expiredDateEditText.addTextChangedListener(new TextWatcher() {

				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				public void afterTextChanged(Editable s) {
					column.add("Expired_date");
					valuesChanged.add(Converter.toString(Converter.toDate(expiredDateEditText.getText().toString(),"dd/MM/yyyy")));
				}
			});
		}

		final ContactInfoRepository cont = new ContactInfoRepository(getApplicationContext());
		Cursor contacts = cont.getContacts2(null);
		startManagingCursor(contacts);
		ContactsAutoCompleteCursorAdapter adapter = new ContactsAutoCompleteCursorAdapter(this, contacts);
			nameEditText.setAdapter(adapter);
			nameEditText.setThreshold(1);
			nameEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
						String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						nameEditText.setText(name);
						String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneEditText.setText(number);

						String address = "No data";
						address = cont.getAddress(name);
						addressEditText.setText(address);

					}
				});

		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (borrow_lend_id != -1) {
					updateData();
				} else {
					insertData();
				}

			}
		});

		// Hand on Cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				BorrowLendInsertActivity.this.finish();
			}
		});

		// Hand on debt type
		debtTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				setChecked(!mIsBorrow);
				debtTypeTextView.setText(getResources().getString(borrow_lend_id == -1 ? (mIsBorrow ? R.string.borrow_add_title: R.string.lend_add_title): (mIsBorrow ? R.string.borrow_edit_title: R.string.lend_edit_title)));
			}
		});

		// get the current date
		if (borrow_lend_id == -1)
		{
			final Calendar c = Calendar.getInstance();
			expiredDate_Year = startDate_Year = c.get(Calendar.YEAR);
			expiredDate_Month = startDate_Month = c.get(Calendar.MONTH);
			expiredDate_Day = startDate_Day = c.get(Calendar.DAY_OF_MONTH);
	
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
		debtTypeButton.setBackgroundResource(isBorrow ? R.drawable.borrow_icon
				: R.drawable.lending_icon);
	}

	// updates the date in the TextView
	private void updateDisplayStartDate() {
		Log.d("st", "Check 2");
		startDateEditText.setText(new StringBuilder().append(startDate_Day)
				.append("/").append(startDate_Month + 1).append("/")
				.append(startDate_Year).toString());
	}

	// updates the date in the TextView
	private void updateDisplayExpiredDate() {
		expiredDateEditText.setText(new StringBuilder().append(expiredDate_Day)
				.append("/").append(expiredDate_Month + 1).append("/")
				.append(expiredDate_Year).toString());
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerStartDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			startDate_Year = year;
			startDate_Month = monthOfYear;
			startDate_Day = dayOfMonth;
			updateDisplayStartDate();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerExpiredDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			expiredDate_Year = year;
			expiredDate_Month = monthOfYear;
			expiredDate_Day = dayOfMonth;

			String startDateString = startDate_Day + "/" + (startDate_Month + 1) + "/" + startDate_Year;
			Date _startDate = Converter.toDate(startDateString, "dd/MM/yyyy");
			String expiredDateString = expiredDate_Day + "/" + (expiredDate_Month + 1) + "/" + expiredDate_Year;
			Date _expiredDate = Converter.toDate(expiredDateString, "dd/MM/yyyy");
			Long startDate = _startDate.getTime();
			Long expiredDate = _expiredDate.getTime();

			if (expiredDate > startDate || (expiredDate - startDate) == 0) {
				updateDisplayExpiredDate();
			} else {
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_expired_date_less_than_start_date));
				// get the current date
				final Calendar c = Calendar.getInstance();
				expiredDate_Year = c.get(Calendar.YEAR);
				expiredDate_Month = c.get(Calendar.MONTH);
				expiredDate_Day = c.get(Calendar.DAY_OF_MONTH);
			}
		}
	};

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(startDate_Year, startDate_Month, startDate_Day);
			break;
		case DATE_DIALOG_ID1:
			((DatePickerDialog) dialog).updateDate(expiredDate_Year, expiredDate_Month, expiredDate_Day);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Log.d("st", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerStartDate, startDate_Year, startDate_Month, startDate_Day);
		case DATE_DIALOG_ID1:
			Log.d("ex", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerExpiredDate, expiredDate_Year, expiredDate_Month, expiredDate_Day);
		}
		return null;
	}

	private void updateData() {
		String interestTypeString = "";
		String debtType = "";
		boolean checkCondition = false;
		
		if (interestType.isChecked())
			interestTypeString = "Simple";
		else
			interestTypeString = "Compound";

		column.add("Interest_type");
		valuesChanged.add(interestTypeString);

		if (mIsBorrow)
			debtType = "Borrowing";
		else
			debtType = "Lending";

		column.add("Debt_type");
		valuesChanged.add(debtType);

		String[] columnUpdate = new String[column.size()];
		columnUpdate = column.toArray(columnUpdate);

		String[] valusChangedUpdate = new String[valuesChanged.size()];
		valusChangedUpdate = valuesChanged.toArray(valusChangedUpdate);
		
		if (nameEditText.getText().toString().equals("") && moneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender_total_money));
			else
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower_total_money));
		} else if (nameEditText.getText().toString().equals("") && !moneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender));
			else
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower));
		} else if(!nameEditText.getText().toString().equals("") && moneyEditText.getText().toString().equals(""))
		{
			alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_total_money));
		} else if (!interestRate.getText().toString().equals("") && expiredDateEditText.getText().toString().equals(""))
		{
			alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_expired_date));
		} else
		{
			checkCondition = true;
		}
		
		if (checkCondition) 
		{
			if (!interestRate.getText().toString().trim().equals("")) {
				if (Integer.parseInt(interestRate.getText().toString().trim()) == 0) {
					alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_0));
					checkCondition = false;
				} else {
					checkCondition = true;
				}
			}
		}
		
		if (checkCondition)
		{
			if (Integer.parseInt(moneyEditText.getText().toString().trim()) == 0)
			{
				alert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_total_money_0));
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
			
			alert.show(getApplicationContext(), getResources().getString(R.string.saved));
			
			BorrowLendInsertActivity.this.finish();
		}
	}

	private void insertData() {
		String interestTypeString = "";
		String debtType = "";
		boolean checkCondition = false;
		
		if (interestType.isChecked()) {
			interestTypeString = "Simple";
		} else {
			interestTypeString = "Compound";
		}
		
		if (mIsBorrow) {
			debtType = "Borrowing";
		} else {
			debtType = "Lending";
		}
		
		if (nameEditText.getText().toString().equals("") && moneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender_total_money));
			else
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower_total_money));
		} else if (nameEditText.getText().toString().equals("") && !moneyEditText.getText().toString().equals(""))
		{
			if(debtType.equals("Borrowing"))
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_lender));
			else
				alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_name_borrower));
		} else if(!nameEditText.getText().toString().equals("") && moneyEditText.getText().toString().equals(""))
		{
			alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_total_money));
		} else if (!interestRate.getText().toString().equals("") && expiredDateEditText.getText().toString().equals(""))
		{
			alert.show(getApplicationContext(), getResources().getString(R.string.borrow_lend_warning_interest_rate_expired_date));
		} else
		{
			checkCondition = true;
		}
		
		if (checkCondition) 
		{
			if(!interestRate.getText().toString().trim().equals(""))
			{
				if (Integer.parseInt(interestRate.getText().toString().trim()) == 0) 
				{
					alert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_interest_rate_0));
					checkCondition = false;
				} else {
					checkCondition = true;
				}
			}
		}
		
		if (checkCondition)
		{
			if (Long.parseLong(moneyEditText.getText().toString().trim()) == 0)
			{
				alert.show(getApplicationContext(), getResources().getString( R.string.borrow_lend_warning_total_money_0));
				checkCondition = false;
			} else {
				checkCondition = true;
			}
		}
		
		if (checkCondition)
		{
			String expiredDateString = "";
			
			if (!expiredDateEditText.getText().toString().equals(""))
				expiredDateString = Converter.toString(Converter.toDate(expiredDateEditText.getText().toString(), "dd/MM/yyyy"));
			
			SqlHelper.instance.insert("BorrowLend",
							new String[] { "Debt_type", "Money",
									"Interest_type", "Interest_rate",
									"Start_date", "Expired_date",
									"Person_name", "Person_phone",
									"Person_address" },
							new String[] {
									debtType,
									moneyEditText.getText().toString(),
									interestTypeString,
									interestRate.getText().toString(),
									Converter.toString(Converter.toDate(startDateEditText.getText().toString(), "dd/MM/yyyy")),
									expiredDateString,
									nameEditText.getText().toString(),
									phoneEditText.getText().toString(),
									addressEditText.getText().toString() });
			setResult(100);

			try {
				if (!SynchronizeTask.isSynchronizing() && Boolean.parseBoolean(XmlParser.getInstance().getConfigContent("autoSync")) && !"pfm.com".equals(AccountProvider.getInstance().getCurrentAccount().type)) {
					SynchronizeTask task = new SynchronizeTask();
					task.execute();
				}
			} catch (Exception e) {
				Logger.Log(e.getMessage(), "BorrowLendInsertActivity");
			}
			
			alert.show(getApplicationContext(), getResources().getString(R.string.saved));
			
			BorrowLendInsertActivity.this.finish();
		}
	}
}
