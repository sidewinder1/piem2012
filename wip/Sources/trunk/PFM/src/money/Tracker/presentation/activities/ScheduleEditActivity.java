package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;
import money.Tracker.presentation.adapters.ScheduleLivingCostAdapter;
import money.Tracker.presnetation.model.*;

public class ScheduleEditActivity extends Activity {
	private int mYear;
	private int mMonth;
	private int mDay;
	private static final int DATE_DIALOG_ID = 0;
	private EditText startDateEdit;
	private EditText endDateEdit;
	private ToggleButton periodic;
	private ArrayList<ScheduleLivingCost> array;
	private ScheduleLivingCostAdapter livingCostAdapter;
	private EditText total_budget;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_edit);

		total_budget = (EditText) findViewById(R.id.schedule_total_budget); 
		startDateEdit = (EditText) findViewById(R.id.schedule_start_date);
		startDateEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		endDateEdit = (EditText) findViewById(R.id.schedule_end_date);
		periodic = (ToggleButton) findViewById(R.id.periodic);
		periodic.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				String [] date = startDateEdit.getText().toString().split("-");
				mMonth = Integer.parseInt(date[0]) - 1;
				mDay = Integer.parseInt(date[1]);
				mYear = Integer.parseInt(date[2].trim());
				int day;
				
				if (periodic.isChecked())
				{
					day = 30;
				}
				else 
				{
					day = mDay + 5;
				}

				endDateEdit.setText(new StringBuilder().append(mMonth + 1).append("-")
						.append(day).append("-").append(mYear).append(" "));
			}
		});
		
		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplay();
		 
		array = new ArrayList<ScheduleLivingCost>();
		livingCostAdapter = new ScheduleLivingCostAdapter(this, 
                R.layout.schedule_edit_item, array);
		
		String initialValue = total_budget.getText().toString();
		if (initialValue + "" == "")
		{
			initialValue = "0";
		}
		array.add(0, new ScheduleLivingCost("Category 1",// 12000));
				Integer.parseInt(initialValue)));
        livingCostAdapter.notifyDataSetChanged();
        
        
		final ListView list = (ListView) findViewById(R.id.schedule_item_list);
        list.setAdapter(livingCostAdapter);
        
	}

	/*@Override
	protected void onStart() 
	{
		String initialValue = total_budget.getText().toString();
		if (initialValue + "" == "")
		{
			initialValue = "0";
		}
		array.add(0, new ScheduleLivingCost("Category 1",// 12000));
				Integer.parseInt(initialValue)));
        livingCostAdapter.notifyDataSetChanged();
	}
	*/
	
	// updates the date in the TextView
	private void updateDisplay() {
		startDateEdit.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear).append(" "));
		int day;
		
		if (periodic.isChecked())
		{
			day = 30;
		}
		else 
		{
			day = mDay + 5;
		}

		endDateEdit.setText(new StringBuilder().append(mMonth + 1).append("-")
				.append(day).append("-").append(mYear).append(" "));
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
