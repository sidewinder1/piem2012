package money.Tracker.presentation.activities;

import java.util.Calendar;
import java.util.Date;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.customviews.EntryEditCategoryView;
import money.Tracker.repository.CategoryRepository;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EntryEditActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
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
				TextView title = (TextView) findViewById(R.id.entry_edit_tilte);
				title.setText(getResources().getString(
						(isChecked ? R.string.entry_edit_expense_title
								: R.string.entry_edit_income_title)));
				Alert.getInstance().show(getBaseContext(), "Not implemented");
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// New Mode
		if (passed_entry_id == -1) {
			updateDisplay();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			entryList.addView(new EntryEditCategoryView(this, null), params);
			
		} else { // Edit mode
			// TextView title = (TextView)
			// findViewById(R.id.schedule_edit_tilte);
			// title.setText("Schedule");
			//
			// Schedule schedule = (Schedule) ScheduleRepository.getInstance()
			// .getData("Id = " + passed_entry_id).get(0);
			// if (schedule != null) {
			// periodic.setChecked(schedule.time_id == 1);
			// startDateEdit.setText(Converter.toString(schedule.start_date,
			// "MMMM dd, yyyy"));
			// endDateEdit.setText(Converter.toString(schedule.end_date,
			// "MMMM dd, yyyy"));
			// total_budget.setText(Converter.toString(schedule.budget));
			// }
			//
			// ArrayList<DetailSchedule> values = DetailScheduleRepository
			// .getInstance().getData(
			// "Schedule_Id = " + passed_entry_id);
			// if (values.size() == 0) {
			// addToList(
			// new DetailSchedule(0, 0,
			// Double.parseDouble(initialValue)), -1, false);
			// } else {
			// for (DetailSchedule value : values) {
			// addToList(value, -1, true);
			// }
			// }
		}
	}

	int lastAddedItem;

	public void doneBtnClicked(View v) {
		save();
		CategoryRepository.getInstance().updateData();
		setResult(100);
		this.finish();
	}

	private void save() {
		int type = entryType.isChecked() ? 1 : 0;
		String date = String.valueOf(dateEdit.getText());
		
		for (int index = 0; index < entryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) entryList
					.getChildAt(index);
			if (item != null) {
				item.save(date, type, passed_entry_id);
			}
		}
	}

	public boolean hasNewCategory() {
		// for (int index = 0; index < list.getChildCount(); index++) {
		// ScheduleItem item = (ScheduleItem) list.getChildAt(index);
		// if (item.category_edit.getVisibility() == View.VISIBLE
		// && "".equals(item.category_edit.getText().toString())) {
		// Alert.getInstance().show(this, "Category is empty");
		// item.category_edit.requestFocus();
		// return false;
		// }
		// }

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
