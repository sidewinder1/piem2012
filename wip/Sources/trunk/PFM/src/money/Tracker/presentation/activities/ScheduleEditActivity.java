package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.adapters.ScheduleLivingCostAdapter;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.DetailScheduleRepository;
import money.Tracker.repository.ScheduleRepository;

public class ScheduleEditActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 0;
	private EditText startDateEdit;
	private EditText endDateEdit;
	private ToggleButton periodic;
	private ArrayList<DetailSchedule> array;
	private ScheduleLivingCostAdapter livingCostAdapter;
	private EditText total_budget;
	private int passed_schedule_id = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_edit);
		// new ScheduleRepository();
		Bundle extras = getIntent().getExtras();
		passed_schedule_id = extras.getInt("schedule_id");

		total_budget = (EditText) findViewById(R.id.schedule_total_budget);
		total_budget.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (livingCostAdapter != null) {
					livingCostAdapter.updateHint();
				}
			}
		});

		startDateEdit = (EditText) findViewById(R.id.schedule_start_date);
		startDateEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		endDateEdit = (EditText) findViewById(R.id.schedule_end_date);
		periodic = (ToggleButton) findViewById(R.id.periodic);
		periodic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				updateDisplay();
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		String initialValue = total_budget.getText().toString();
		if (initialValue + "" == "") {
			initialValue = "0";
		}

		// New Mode
		if (passed_schedule_id == -1) {
			array = new ArrayList<DetailSchedule>();
			// display the current date (this method is below)
			updateDisplay();
			array.add(new DetailSchedule(0, Double.parseDouble(initialValue)));
		} else { // Edit mode
			Schedule schedule = (Schedule) ScheduleRepository.getInstance()
					.getData("Id = " + passed_schedule_id).get(0);
			if (schedule != null) {
				periodic.setChecked(schedule.time_id == 1);
				startDateEdit.setText(Converter.toString(schedule.start_date,
						"MMMM dd, yyyy"));
				endDateEdit.setText(Converter.toString(schedule.end_date,
						"MMMM dd, yyyy"));
				total_budget.setText(String.valueOf(schedule.budget));
			}

			array = DetailScheduleRepository.getInstance().getData(
					"Schedule_Id = " + passed_schedule_id);
		}

		livingCostAdapter = new ScheduleLivingCostAdapter(this,
				R.layout.schedule_edit_item, array, passed_schedule_id != -1);

		livingCostAdapter.notifyDataSetChanged();

		ListView list = (ListView) findViewById(R.id.schedule_item_list);
		list.setAdapter(livingCostAdapter);

		Spinner currency = (Spinner) findViewById(R.id.currency_symbol);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.currency_symbol, R.layout.spinner_dropdown);

		adapter.setDropDownViewResource(R.layout.spinner_dropdown);

		currency.setAdapter(adapter);
	}

	public double getTotalBudget() {
		String budget_value = total_budget.getText().toString();
		if ("".equals(budget_value)) {
			budget_value = total_budget.getHint().toString();

			if ("".equals(budget_value) || budget_value.contains(" ")) {
				budget_value = "0";
			}
		}

		return Double.parseDouble(budget_value);
	}

	public boolean updateTotalBudget() {
		double total = 0;

		for (DetailSchedule detail : array) {
			total += detail.getBudget();
		}

		if ("".equals(total_budget.getText().toString())) {
			total_budget.setHint(String.valueOf(total));
		} else {
			if (Double.parseDouble(total_budget.getText().toString()) < total) {
				Toast toast = Toast.makeText(this, "Over budget!",
						Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
		}

		return true;
	}

	public void doneBtnClicked(View v) {
		String Time_id = (periodic.isChecked() ? "1" : "0");
		Cursor scheduleCursor = SqlHelper.instance.select(
				"Schedule",
				"End_date",
				new StringBuilder("Time_Id = ")
						.append(Time_id)
						.append(" AND End_date = '")
						.append(Converter.toString(Converter.toDate(endDateEdit
								.getText().toString(), "MMMM dd, yyyy")))
						.append("'").toString());
		if (passed_schedule_id != -1)
		{
			// Update schedule record.
			
			return;
		}
		
		if (scheduleCursor != null && scheduleCursor.moveToFirst()) {
			Toast.makeText(this, "A schedule for this time is existing!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		// Add new schedule.
		addSchedule(Time_id);
		
		setResult(100);
		this.finish();
	}

	private void addSchedule(String Time_id) {
		String budget_value = String.valueOf(total_budget.getText().toString());
		if ("".equals(budget_value))
		{
			budget_value = String.valueOf(total_budget.getHint().toString());
		}
		long newScheduleId = SqlHelper.instance.insert(
				"Schedule",
				new String[] { "Budget", "Start_date", "End_date", "Time_Id" },
				new String[] {
						budget_value,
						Converter.toString(Converter.toDate(startDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Converter.toString(Converter.toDate(endDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Time_id });
		if (newScheduleId != -1) {
			for (DetailSchedule detailItem : array) {
				SqlHelper.instance.insert("ScheduleDetail", new String[] {
						"Budget", "Category_id", "Schedule_id" },
						new String[] { String.valueOf(detailItem.getBudget()),
								String.valueOf(detailItem.getCategory()),
								String.valueOf(newScheduleId) });
			}

			Toast.makeText(this, "Save sucessfully", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Can not save data", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void cancelBtnClicked(View v) {
		setResult(100);
		this.finish();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		Date startDate = DateTimeHelper.getDate(mYear, mMonth, mDay);
		startDateEdit.setText(Converter.toString(startDate, "MMMM dd, yyyy"));
		Date endDate;

		if (periodic.isChecked()) {
			endDate = DateTimeHelper.getLastDateOfMonth(mYear, mMonth);
		} else {
			endDate = DateTimeHelper.getLastDayOfWeek(startDate);
		}

		endDateEdit.setText(Converter.toString(endDate, "MMMM dd, yyyy"));
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
