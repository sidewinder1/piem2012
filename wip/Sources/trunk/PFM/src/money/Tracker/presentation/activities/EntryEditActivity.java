package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.presentation.customviews.EntryEditCategoryView;
import money.Tracker.presentation.customviews.ScheduleItem;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.CategoryRepository;
import money.Tracker.repository.DetailScheduleRepository;
import money.Tracker.repository.ScheduleRepository;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
	private static final int DATE_DIALOG_ID = 0;
	private EditText dateEdit;
	private ToggleButton entryType;
	private int passed_entry_id = -1;
	LinearLayout entryList;
	private CategoryAdapter categoryAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_edit);

		Bundle extras = getIntent().getExtras();
		passed_entry_id = extras.getInt("entry_id");

		// Add item for detail schedule.
		categoryAdapter = new CategoryAdapter(this,
				R.layout.dropdown_list_item, new ArrayList<Category>(
						CategoryRepository.getInstance().categories));

		categoryAdapter.notifyDataSetChanged();

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
			entryList.addView(new EntryEditCategoryView(this), params);
			// addToList(
			// new DetailSchedule(0, 0, Double.parseDouble(initialValue)),
			// -1, false);

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

	private void addToList(DetailSchedule detail, int index, boolean init) {

	}

	private OnFocusChangeListener completeAfterLostFocus = new OnFocusChangeListener() {
		public void onFocusChange(View v, boolean hasFocus) {
			completeAfterMove(v, hasFocus);
		}

		private void completeAfterMove(View v, boolean hasFocus) {
			if (!hasFocus) {
				String str = ((EditText) v).getText().toString();
				if (!"".equals(str)) {
					((EditText) v).setText(Converter.toString(Double
							.parseDouble(str)));
				}
			}
		}
	};

	public void doneBtnClicked(View v) {
		save();
		CategoryRepository.getInstance().updateData();
		setResult(100);
		this.finish();
	}

	private void save() {
		int type = entryType.isChecked() ? 1 : 0;
		String date = String.valueOf(dateEdit.getText());
		String condition = passed_entry_id == -1 ? new StringBuilder("")
				.toString() : new StringBuilder("Id = ").append(passed_entry_id)
				.toString();
		for (int index = 0; index < entryList.getChildCount(); index++) {
			EntryEditCategoryView item = (EntryEditCategoryView) entryList
					.getChildAt(index);
			if (item != null) {
				item.save(date, type, condition);
			}
		}
	}

	private void addSchedule(String Time_id) {
		// long newScheduleId = SqlHelper.instance.insert(
		// "Schedule",
		// new String[] { "Budget", "Start_date", "End_date", "Time_Id" },
		// new String[] {
		// Converter.toString(getTotalBudget()),
		// Converter.toString(Converter.toDate(startDateEdit
		// .getText().toString(), "MMMM dd, yyyy")),
		// Converter.toString(Converter.toDate(endDateEdit
		// .getText().toString(), "MMMM dd, yyyy")),
		// Time_id });
		// if (newScheduleId != -1) {
		// saveDetailSchedule(newScheduleId);
		//
		// Alert.getInstance().show(this, "Save sucessfully");
		// } else {
		// Alert.getInstance().show(this, "Can not save data");
		// }
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

	private void saveDetailSchedule(long newScheduleId) {
		// for (int index = 0; index < list.getChildCount(); index++) {
		// ScheduleItem detailItem = (ScheduleItem) list.getChildAt(index);
		// if (detailItem.getBudget() == 0) {
		// continue;
		// }
		//
		// long category_id = detailItem.getCategory();
		// if (detailItem.category_edit.getVisibility() == View.VISIBLE) {
		// category_id = SqlHelper.instance.insert(
		// "Category",
		// new String[] { "Name", "User_Color" },
		// new String[] {
		// detailItem.category_edit.getText().toString(),
		// String.valueOf(detailItem.category_edit
		// .getTag()) });
		// }
		// SqlHelper.instance.insert(
		// "ScheduleDetail",
		// new String[] { "Budget", "Category_id", "Schedule_id" },
		// new String[] { Converter.toString(detailItem.getBudget()),
		// String.valueOf(category_id),
		// String.valueOf(newScheduleId) });
		// }
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
