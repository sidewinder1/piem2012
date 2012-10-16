package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.customviews.EntryEditCategoryView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import money.Tracker.repository.EntryDetailRepository;
import money.Tracker.repository.EntryRepository;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EntryEditActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private TextView title;
	private static final int DATE_DIALOG_ID = 0;
	private EditText dateEdit;
	private ToggleButton entryType;
	private int passed_entry_id = -1;
	LinearLayout entryList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_edit);

		Bundle extras = getIntent().getExtras();
		passed_entry_id = extras.getInt("entry_id");

		entryList = (LinearLayout) findViewById(R.id.entry_edit_list);
		title = (TextView) findViewById(R.id.entry_edit_tilte);
		dateEdit = (EditText) findViewById(R.id.entry_edit_date);
		dateEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		entryType = (ToggleButton) findViewById(R.id.entry_edit_type);
		entryType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		// New Mode
		if (passed_entry_id == -1) {
			updateDisplay();

			entryList.addView(new EntryEditCategoryView(this, null), params);

		} else { // Edit mode
			Entry entry = (Entry) EntryRepository.getInstance()
					.getData("Id = " + passed_entry_id).get(0);
			if (entry != null) {
				dateEdit.setText(Converter.toString(entry.getDate(),
						"dd/MM/yyyy"));

				entryType.setChecked(entry.getType() == 1);
			}

			HashMap<String, ArrayList<EntryDetail>> values = EntryDetailRepository
					.getInstance().updateData("Entry_Id = " + passed_entry_id,
							"Category_Id");
			for (ArrayList<EntryDetail> entryDetail : values.values()) {
				entryList.addView(new EntryEditCategoryView(this, entryDetail),
						params);
			}
		}

		updateTitle();
	}

	private void updateTitle() {
		title.setText(getResources()
				.getString(
						((passed_entry_id != -1) ? (entryType.isChecked() ? R.string.entry_edit_expense_title
								: R.string.entry_edit_income_title)
								: (entryType.isChecked() ? R.string.entry_new_expense_title
										: R.string.entry_new_income_title)))
				.replace(
						"{0}",
						Converter.toString(
								Converter.toDate(dateEdit.getText().toString()),
								"dd/MM/yyyy")));
	}

	public void doneBtnClicked(View v) {
		if (save()) {
			CategoryRepository.getInstance().updateData();
			setResult(100);
			this.finish();
		}
	}

	private String checkBeforeSave() {
		for (int index = 0; index < entryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) entryList
					.getChildAt(index);
			String temp = item.checkBeforeSave();
			if (item != null && temp != null) {
				return temp;
			}
		}

		return null;
	}

	private boolean save() {
		String temp = checkBeforeSave();
		if (temp != null) {
			Alert.getInstance().show(this, temp);
			return false;
		}

		int type = entryType.isChecked() ? 1 : 0;
		String date = Converter
				.toString(Converter.toDate(String.valueOf(dateEdit.getText()),
						"dd/MM/yyyy"), "yyyy/MM/dd");
		String table = "Entry";
		String subTable = "EntryDetail";
		long id = passed_entry_id;

		if (passed_entry_id == -1) {
			Cursor oldEntry = SqlHelper.instance.select("Entry", "Id",
					new StringBuilder("Date = '").append(date).append("'")
							.toString());
			if (oldEntry != null && oldEntry.moveToFirst()) {
				id = oldEntry.getInt(0);
			} else {
				id = SqlHelper.instance.insert(table, new String[] { "Date",
						"Type" }, new String[] { date, String.valueOf(type) });
			}
		} else {
			SqlHelper.instance.update(table, new String[] { "Date", "Type" },
					new String[] { date, String.valueOf(type) },
					new StringBuilder("Id = ").append(passed_entry_id)
							.toString());
			SqlHelper.instance.delete(subTable,
					new StringBuilder("Entry_Id = ").append(passed_entry_id)
							.toString());
		}

		for (int index = 0; index < entryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) entryList
					.getChildAt(index);
			if (item != null) {
				item.save(id);
			}
		}

		Alert.getInstance().show(this, "Save successfully!");
		return true;
	}

	public void cancelBtnClicked(View v) {
		setResult(100);
		this.finish();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		Date startDate = DateTimeHelper.getDate(mYear, mMonth, mDay);
		dateEdit.setText(Converter.toString(startDate, "dd/MM/yyyy"));
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
}
