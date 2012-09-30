package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.adapters.ScheduleLivingCostAdapter;
import money.Tracker.presentation.model.DetailSchedule;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_edit);
		// new ScheduleRepository();
		total_budget = (EditText) findViewById(R.id.schedule_total_budget);
		total_budget.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (total_budget.getText().toString() != "") {
					// for (ScheduleLivingCost item : array)
					{
						// double value = item.getCategory();
					}
				}
				return false;
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

		// display the current date (this method is below)
		updateDisplay();

		array = new ArrayList<DetailSchedule>();
		livingCostAdapter = new ScheduleLivingCostAdapter(this,
				R.layout.schedule_edit_item, array);

		String initialValue = total_budget.getText().toString();
		if (initialValue + "" == "") {
			initialValue = "0";
		}

		array.add(new DetailSchedule(0, Double.parseDouble(initialValue)));
		livingCostAdapter.notifyDataSetChanged();

		final ListView list = (ListView) findViewById(R.id.schedule_item_list);
		list.setAdapter(livingCostAdapter);

	}

	public void doneBtnClicked(View v) {
		String Time_id = (periodic.isChecked() ? "1" : "0");
		long newScheduleId = SqlHelper.instance
				.insert("Schedule",
						new String[] { "Budget", "Start_date", 
									"End_date", "Time_Id" },
						new String[] {
								String.valueOf(total_budget.getText()
										.toString()),
								Converter.toString(Converter.toDate(
										startDateEdit.getText().toString(),
										"MMMM dd, yyyy")),
								Converter.toString(Converter.toDate(endDateEdit
										.getText().toString(), "MMMM dd, yyyy")),
										Time_id});
		if (newScheduleId != -1) {
			for (DetailSchedule detailItem : array) {
				SqlHelper.instance.insert("ScheduleDetail", new String[] {
						"Budget", "Category_id", "Schedule_id" },
						new String[] { String.valueOf(detailItem.getBudget()),
								"0", String.valueOf(newScheduleId) });
			}
		}
		
		setResult(100);
		this.finish();
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
