package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TooManyListenersException;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.adapters.ContactsAutoCompleteCursorAdapter;
import money.Tracker.presentation.model.Contact;
import money.Tracker.repository.*;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.ToggleButton;

public class BorrowLendInsertActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 0;
	private static final int DATE_DIALOG_ID1 = 1;
	private EditText startDateEditText;
	private EditText expiredDateEditText;


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

		new BorrowLendRepository();

		final ContactInfoRepository cont = new ContactInfoRepository(getApplicationContext());
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
					}
				});

		saveButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				String interestTypeString;

				if (interestType.isChecked()) {
					interestTypeString = "Simple";
				} else {
					interestTypeString = "Compound interest";
				}

				if (debtTypeButton.isChecked()) {
					long check = SqlHelper.instance.insert("Borrowing",
							new String[] { "Money", "Interest_type",
									"Interest_rate", "Start_date",
									"Expired_date", "Person_name",
									"Person_Phone", "Person_address" },
							new String[] {
									moneyEditText.getText().toString(),
									"'" + interestTypeString + "'",
									interestRate.getText().toString(),
									startDateEditText.getText().toString()
											.trim(),
									expiredDateEditText.getText().toString()
											.trim(),
									"'" + nameEditText.getText().toString()
											+ "'",
									"'" + phoneEditText.getText().toString()
											+ "'",
									"'" + addressEditText.getText().toString()
											+ "'" });
					Log.d("Insert", startDateEditText.getText().toString());
					Log.d("Insert", expiredDateEditText.getText().toString());
				} else {
					SqlHelper.instance.insert("Lending", new String[] {
							"Money", "Interest_type", "Interest_rate",
							"Start_date", "Expired_date", "Person_name",
							"Person_Phone", "Person_address" }, new String[] {
							moneyEditText.getText().toString(),
							interestTypeString,
							interestRate.getText().toString(),
							startDateEditText.getText().toString(),
							expiredDateEditText.getText().toString(),
							nameEditText.getText().toString(),
							phoneEditText.getText().toString(),
							addressEditText.getText().toString() });
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
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplayStartDate();
		updateDisplayExpiredDate();

		// make dialog for start date
		EditText startDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		startDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID);
			}
		});

		// make dialog for expired date
		EditText expiredDateEditText = (EditText) findViewById(R.id.start_date_edit_text);
		expiredDateEditText.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(DATE_DIALOG_ID1);
			}
		});

	}

	// updates the date in the TextView
	private void updateDisplayStartDate() {
		startDateEditText.setText(new StringBuilder().append(mDay).append("/")
				.append(mMonth + 1).append("/").append(mYear).append(" "));
	}

	// updates the date in the TextView
	private void updateDisplayExpiredDate() {
		expiredDateEditText.setText(new StringBuilder().append(mDay)
				.append("/").append(mMonth + 1).append("/").append(mYear)
				.append(" "));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerStartDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplayStartDate();
		}
	};

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListenerExpiredDate = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplayExpiredDate();
		}
	};

	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		case DATE_DIALOG_ID1:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListenerStartDate, mYear,
					mMonth, mDay);
		case DATE_DIALOG_ID1:
			return new DatePickerDialog(this, mDateSetListenerExpiredDate,
					mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_borrow_lend_insert, menu);
		return true;
	}
}
