package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TooManyListenersException;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.adapters.ContactsAutoCompleteCursorAdapter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.presentation.model.Contact;
import money.Tracker.repository.*;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BorrowLendInsertActivity extends Activity {
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_lend_insert);

		final Button saveButton = (Button) findViewById(R.id.saveButton);
		final Button cancelButton = (Button) findViewById(R.id.cancelButton);
		final TextView debtTypeTextView = (TextView) findViewById(R.id.title_text_view);
		final ToggleButton debtTypeButton = (ToggleButton) findViewById(R.id.borrowLendType);
		final AutoCompleteTextView nameEditText = (AutoCompleteTextView) findViewById(R.id.name_edit_text);
		final EditText phoneEditText = (EditText) findViewById(R.id.phone_edit_text);
		final EditText addressEditText = (EditText) findViewById(R.id.address_edit_text);
		final EditText moneyEditText = (EditText) findViewById(R.id.money_edit_text);
		final ToggleButton interestType = (ToggleButton) findViewById(R.id.interestType);
		final EditText interestRate = (EditText) findViewById(R.id.interest_rate_edit_text);
		startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		expiredDateEditText = (EditText) findViewById(R.id.expired_date_edit_text);
		
		alert = new Alert();

		new BorrowLendRepository();
		
		// get information from view detail intent
		Bundle extras = getIntent().getExtras();
		boolean checkBorrowing = true;
		int borrow_lend_id = -1;
		String tableName = "";
		
		try
		{
			borrow_lend_id = extras.getInt("borrowLendID");
			checkBorrowing = extras.getBoolean("checkBorrowing");
		} catch (Exception e)
		{
			
		}
		
		if (borrow_lend_id != -1)
		{
			debtTypeButton.setClickable(false);
		
			if(checkBorrowing)
				tableName += "Borrowing";
			else
				tableName += "Lending";
			
			debtTypeButton.setText(tableName);
			debtTypeTextView.setText("Edit " + tableName);
					
			BorrowLendRepository bolere = new BorrowLendRepository();		
			BorrowLend values = bolere.getDetailData(tableName, "ID=" + borrow_lend_id);
			
			nameEditText.setText(String.valueOf(values.getPersonName()));
			phoneEditText.setText(String.valueOf(values.getPersonPhone()));
			addressEditText.setText(String.valueOf(values.getPersonAddress()));
			moneyEditText.setText(String.valueOf(values.getMoney()));
			interestType.setText(String.valueOf(values.getInterestType()));
			interestRate.setText(String.valueOf(values.getInterestRate()));
			startDateEditText.setText(Converter.toString(values.getStartDate(), "dd/MM/yyyy"));
			expiredDateEditText.setText(Converter.toString(values.getExpiredDate(), "dd/MM/yyyy"));
			
		}
		
		final int borrowLendID;
		borrowLendID = borrow_lend_id;
		final String _tableName;
		_tableName = tableName;
		
		final ContactInfoRepository cont = new ContactInfoRepository(
				getApplicationContext());
		Cursor contacts = cont.getContacts2(null);
		startManagingCursor(contacts);
		ContactsAutoCompleteCursorAdapter adapter = new ContactsAutoCompleteCursorAdapter(
				this, contacts);
		nameEditText.setAdapter(adapter);
		nameEditText.setThreshold(1);
		nameEditText
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
						String name = cursor.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
						nameEditText.setText(name);
						String number = cursor.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
						phoneEditText.setText(number);

						// String id = cont.findContact(name, number);
						String id = cursor.getString(cursor
								.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone._ID));

						Cursor cursor1 = cont.getContactAddress(id);
						String address = cursor1.getString(cursor1
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
						// cursor1.close();
						addressEditText.setText(address);
						// addressEditText.setText(id);
					}
				});

		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if (borrowLendID != 1)
					SqlHelper.instance.delete(_tableName, "ID = " + borrowLendID);

				String interestTypeString;

				if (interestType.isChecked()) {
					interestTypeString = "Simple";
				} else {
					interestTypeString = "Compound";
				}

				if (!nameEditText.getText().toString().equals("")) {
					if ((!interestRate.getText().toString().equals("") && !expiredDateEditText
							.getText().toString().equals(""))
							|| (interestRate.getText().toString().equals("") && expiredDateEditText
									.getText().toString().equals(""))) {
						if (debtTypeButton.isChecked()) {
							long check = SqlHelper.instance
									.insert("Borrowing", new String[] {
											"Money", "Interest_type",
											"Interest_rate", "Start_date",
											"Expired_date", "Person_name",
											"Person_Phone", "Person_address" },
											new String[] {
													moneyEditText.getText().toString(),
													interestTypeString,
													interestRate.getText().toString(),
													startDateEditText.getText().toString().trim(),
													expiredDateEditText.getText().toString().trim(),
													nameEditText.getText().toString(),
													phoneEditText.getText().toString(),
													addressEditText.getText().toString()});
							Log.d("Insert", startDateEditText.getText()
									.toString());
							Log.d("Insert", expiredDateEditText.getText()
									.toString());
						} else {
							SqlHelper.instance.insert("Lending", new String[] {
									"Money", "Interest_type", "Interest_rate",
									"Start_date", "Expired_date",
									"Person_name", "Person_Phone",
									"Person_address" }, new String[] {
									moneyEditText.getText().toString(),
									interestTypeString,
									interestRate.getText().toString(),
									startDateEditText.getText().toString(),
									expiredDateEditText.getText().toString(),
									nameEditText.getText().toString(),
									phoneEditText.getText().toString(),
									addressEditText.getText().toString() });
						}

					}
				}

				setResult(100);
				BorrowLendInsertActivity.this.finish();
			}
		});

		// Hand on Cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				BorrowLendInsertActivity.this.finish();
			}
		});

		// Hand on debt type
		debtTypeButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (debtTypeButton.isChecked()) {
					debtTypeTextView.setText("New Borrowing");
				} else {
					debtTypeTextView.setText("New Lending");
				}
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		expiredDate_Year = startDate_Year = c.get(Calendar.YEAR);
		expiredDate_Month = startDate_Month = c.get(Calendar.MONTH);
		expiredDate_Day = startDate_Day = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplayStartDate();
		// updateDisplayExpiredDate();

		// make dialog for start date
		EditText startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		startDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("st", "Check 1");
				showDialog(DATE_DIALOG_ID);
			}
		});

		// make dialog for expired date
		EditText expiredDateEditText = (EditText) findViewById(R.id.expired_date_edit_text);
		expiredDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("ex", "Check 1");
				showDialog(DATE_DIALOG_ID1);
			}
		});

	}

	// updates the date in the TextView
	private void updateDisplayStartDate() {
		Log.d("st", "Check 2");
		startDateEditText.setText(new StringBuilder().append(startDate_Day)
				.append("/").append(startDate_Month + 1).append("/")
				.append(startDate_Year).append(" "));
	}

	// updates the date in the TextView
	private void updateDisplayExpiredDate() {
		expiredDateEditText.setText(new StringBuilder().append(expiredDate_Day)
				.append("/").append(expiredDate_Month + 1).append("/")
				.append(expiredDate_Year).append(" "));
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
			expiredDate_Year = dayOfMonth;

			Log.d("errorDateTime", "Check 1");
			String startDateString = startDate_Day + "/"
					+ (startDate_Month + 1) + "/" + startDate_Year;
			Log.d("errorDateTime", startDateString);
			Date _startDate = Converter.toDate(startDateString, "dd/MM/yyyy");
			Log.d("errorDateTime", "Check 2");
			String expiredDateString = expiredDate_Day + "/"
					+ (expiredDate_Month + 1) + "/" + expiredDate_Year;
			Log.d("errorDateTime", expiredDateString);
			Date _expiredDate = Converter.toDate(expiredDateString,
					"dd/MM/yyyy");
			Log.d("errorDateTime", "Check 3");
			Long startDate = _startDate.getTime();
			Log.d("errorDateTime", "Check 4");
			Long expiredDate = _expiredDate.getTime();
			Log.d("errorDateTime", "Check 5");

			if (expiredDate > startDate || expiredDate == startDate) {
				updateDisplayExpiredDate();
				Log.d("errorDateTime", "Check 6");
			} else {
				alert.show(getApplicationContext(), "Wrong input. Try again");
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
			Log.d("st", "Check 4");
			((DatePickerDialog) dialog).updateDate(startDate_Year,
					startDate_Month, startDate_Day);
			break;
		case DATE_DIALOG_ID1:
			Log.d("ex", "Check 4");
			((DatePickerDialog) dialog).updateDate(expiredDate_Year,
					expiredDate_Month, expiredDate_Day);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Log.d("st", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerStartDate,
					startDate_Year, startDate_Month, startDate_Day);
		case DATE_DIALOG_ID1:
			Log.d("ex", "Check 5");
			return new DatePickerDialog(this, mDateSetListenerExpiredDate,
					expiredDate_Year, expiredDate_Month, expiredDate_Day);
		}
		return null;
	}
}
