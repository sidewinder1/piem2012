package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.presentation.customviews.ScheduleItem;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.CategoryRepository;
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
	private EditText total_budget;
	private int passed_schedule_id = -1;
	LinearLayout list;
	private CategoryAdapter categoryAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_edit);
		// new ScheduleRepository();
		Bundle extras = getIntent().getExtras();
		passed_schedule_id = extras.getInt("schedule_id");

		// Add item for detail schedule.
		categoryAdapter = new CategoryAdapter(this,
				R.layout.dropdown_list_item, new ArrayList<Category>(
						CategoryRepository.getInstance().categories));

		categoryAdapter.notifyDataSetChanged();

		list = (LinearLayout) findViewById(R.id.list);

		total_budget = (EditText) findViewById(R.id.schedule_total_budget);

		total_budget.setOnFocusChangeListener(completeAfterLostFocus);

		// Add event to total_budget to handle business logic.
		total_budget.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				updateHint();
			}
		});

		total_budget.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String str = total_budget.getText().toString();
					if (!"".equals(str) && str.endsWith(".")) {
						total_budget.setText(Converter.toString(Double
								.parseDouble(str)));
					}
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
			updateDisplay();
			addToList(
					new DetailSchedule(0, 0, Double.parseDouble(initialValue)),
					-1, false);

		} else { // Edit mode
			TextView title = (TextView) findViewById(R.id.schedule_edit_tilte);
			title.setText("Schedule");

			Schedule schedule = (Schedule) ScheduleRepository.getInstance()
					.getData("Id = " + passed_schedule_id).get(0);
			if (schedule != null) {
				periodic.setChecked(schedule.time_id == 1);
				startDateEdit.setText(Converter.toString(schedule.start_date,
						"MMMM dd, yyyy"));
				endDateEdit.setText(Converter.toString(schedule.end_date,
						"MMMM dd, yyyy"));
				total_budget.setText(Converter.toString(schedule.budget));
			}

			ArrayList<DetailSchedule> values = DetailScheduleRepository
					.getInstance().getData(
							"Schedule_Id = " + passed_schedule_id);
			if (values.size() == 0) {
				addToList(
						new DetailSchedule(0, 0,
								Double.parseDouble(initialValue)), -1, false);
			} else {
				for (DetailSchedule value : values) {
					addToList(value, -1, true);
				}
			}
		}

		Spinner currency = (Spinner) findViewById(R.id.currency_symbol);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.currency_symbol, R.layout.spinner_dropdown);

		adapter.setDropDownViewResource(R.layout.spinner_dropdown);

		currency.setAdapter(adapter);
	}

	int lastAddedItem;

	private void addToList(DetailSchedule detail, int index, boolean init) {
		ScheduleItem itemView = new ScheduleItem(this, categoryAdapter);
		itemView.category.setTag(itemView.category_edit);

		if (init) {
			itemView.budget.setText(Converter.toString(detail.getBudget()));
		} else {
			itemView.budget.setHint(Converter.toString(detail.getBudget()));
		}

		// Add events to to detail budget to handle business logic.
		itemView.budget.setOnFocusChangeListener(completeAfterLostFocus);

		itemView.budget.addTextChangedListener(new TextWatcher() {
			double sValue = 0;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (!"".equals(s.toString())) {
					sValue = Double.parseDouble(s.toString());
				}
			}

			public void afterTextChanged(Editable s) {
				if (!"".equals(s.toString())) {
					updateTotalBudget(Double.parseDouble(s.toString()) > sValue);
				}
			}
		});

		itemView.category
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						Category item = (Category) parent
								.getItemAtPosition(pos);
						if (item != null && "Others".equals(item.getName())) {
							parent.setVisibility(View.GONE);
							View text = (View) parent.getTag();
							text.setVisibility(View.VISIBLE);
							text.requestFocus();

							// Change color for new category.
							Cursor color = SqlHelper.instance.select(
									"UserColor", "User_Color", null);
							if (color != null && color.moveToFirst()) {
								text.setBackgroundColor(Color.parseColor(color
										.getString(0)));
								text.setTag(color.getString(0));
								SqlHelper.instance.delete("UserColor",
										new StringBuilder("User_Color = '")
												.append(color.getString(0))
												.append("'").toString());
							}
						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		itemView.category.setSelection(CategoryRepository.getInstance()
				.getIndex(detail.getCategory()));
		if (index < 0) {
			list.addView(itemView);
		} else {
			list.addView(itemView, index);
		}

		itemView.addBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int lastItem = 0;
				for (int index = 0; index < list.getChildCount(); index++) {
					if (list.getChildAt(index) == v.getParent().getParent()) {
						lastItem = index + 1;
					}
					ScheduleItem item = (ScheduleItem) list.getChildAt(index);
					if (item.getBudget() <= 0) {
						Alert.getInstance().show(getBaseContext(),
								"A slot is empty");
						item.budget.requestFocus();
						return;
					}

					if (item.category_edit.getVisibility() == View.VISIBLE
							&& "".equals(item.category_edit.getText()
									.toString())) {
						Alert.getInstance().show(getBaseContext(),
								"New category is empty");
						item.category_edit.requestFocus();
						return;
					}
				}

				lastAddedItem = lastItem;
				addToList(new DetailSchedule(0, 0, getNextHint()),
						lastAddedItem, false);
			}
		});

		itemView.removeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (list.getChildCount() > 1) {
					ScheduleItem item = (ScheduleItem) v.getParent()
							.getParent();
					if (item == null) {
						return;
					}

					// Insert userColor to db again.
					SqlHelper.instance.insert("UserColor",
							new String[] { "User_Color" },
							new String[] { String.valueOf(item.category_edit
									.getTag()) });

					list.removeView(item);
					updateTotalBudget(true);
				}

			}
		});
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

	public void updateHint() {
		if (list.getChildCount() == 0) {
			return;
		}

		ScheduleItem scheduleItem = (ScheduleItem) list
				.getChildAt(lastAddedItem);

		if (scheduleItem == null) {
			return;
		}

		EditText lastBudget = scheduleItem.budget;
		if (lastBudget == null) {
			return;
		}

		if ("".equals(lastBudget.getText().toString())) {
			lastBudget.setHint(Converter.toString(getNextHint()));
		}
	}

	public double getNextHint() {
		return Math.max(0, getTotalBudget() - getTotalDetailBudget());
	}

	public double getTotalDetailBudget() {
		double total = 0;
		for (int index = 0; index < list.getChildCount(); index++) {
			total += ((ScheduleItem) list.getChildAt(index)).getBudget();
		}

		return Math.max(0, total);
	}

	public double getTotalBudget() {
		String budget_value = total_budget.getText().toString();
		if ("".equals(budget_value)) {
			budget_value = total_budget.getHint().toString();

			if ("".equals(budget_value) || budget_value.contains(" ")) {
				budget_value = "0";
			}
		}

		return Math.max(0, Double.parseDouble(budget_value));
	}

	public boolean updateTotalBudget(boolean eanbleDialog) {
		double totalDetail = getTotalDetailBudget();

		if ("".equals(total_budget.getText().toString())) {
			total_budget.setHint(Converter.toString(totalDetail));
		} else {
			if (getTotalBudget() < totalDetail && eanbleDialog) {
				Alert.getInstance().showDialog(this, "Over budget! Add more?",
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								total_budget.setText(Converter
										.toString(getTotalDetailBudget()));
								total_budget.requestFocus();
							}
						}, new OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});

				return false;
			}
		}

		return true;
	}

	public void doneBtnClicked(View v) {
		if (getTotalBudget() <= 0) {
			Alert.getInstance().show(this, "Input Total budget!");
			return;
		}

		if (getTotalBudget() < getTotalDetailBudget()) {
			Alert.getInstance().show(this, "Total budget not enough!");
			return;
		}

		if (!hasNewCategory()) {
			return;
		}

		String Time_id = (periodic.isChecked() ? "1" : "0");

		if (passed_schedule_id != -1) {
			String budget_value = String.valueOf(total_budget.getText());
			if ("".equals(budget_value)) {
				budget_value = String
						.valueOf(total_budget.getHint().toString());
			}

			// Update schedule record.
			updateSchedule(Time_id, budget_value);
		} else {
			// Add new mode.
			Cursor scheduleCursor = SqlHelper.instance.select(
					"Schedule",
					"End_date",
					new StringBuilder("Time_Id = ")
							.append(Time_id)
							.append(" AND End_date = '")
							.append(Converter.toString(Converter.toDate(
									endDateEdit.getText().toString(),
									"MMMM dd, yyyy"))).append("'").toString());
			if (scheduleCursor != null && scheduleCursor.moveToFirst()) {
				Alert.getInstance().show(this,
						"A schedule for this time is existing!");
				return;
			}

			// Add new schedule.
			addSchedule(Time_id);
		}

		CategoryRepository.getInstance().updateData();
		setResult(100);
		this.finish();
	}

	private void updateSchedule(String Time_id, String budget_value) {
		SqlHelper.instance.update(
				"Schedule",
				new String[] { "Budget", "Start_date", "End_date", "Time_Id" },
				new String[] {
						budget_value,
						Converter.toString(Converter.toDate(startDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Converter.toString(Converter.toDate(endDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Time_id },
				new StringBuilder("Id = ").append(passed_schedule_id)
						.toString());

		// Delete all records that have schedule id equals
		// passed_schedule_id
		SqlHelper.instance.delete("ScheduleDetail", new StringBuilder(
				"Schedule_Id = ").append(passed_schedule_id).toString());
		// Insert new.
		saveDetailSchedule(passed_schedule_id);
		Alert.getInstance().show(this, "Updated 1 record sucessfully");
	}

	private void addSchedule(String Time_id) {
		long newScheduleId = SqlHelper.instance.insert(
				"Schedule",
				new String[] { "Budget", "Start_date", "End_date", "Time_Id" },
				new String[] {
						Converter.toString(getTotalBudget()),
						Converter.toString(Converter.toDate(startDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Converter.toString(Converter.toDate(endDateEdit
								.getText().toString(), "MMMM dd, yyyy")),
						Time_id });
		if (newScheduleId != -1) {
			saveDetailSchedule(newScheduleId);

			Alert.getInstance().show(this, "Save sucessfully");
		} else {
			Alert.getInstance().show(this, "Can not save data");
		}
	}

	public boolean hasNewCategory() {
		for (int index = 0; index < list.getChildCount(); index++) {
			ScheduleItem item = (ScheduleItem) list.getChildAt(index);
			if (item.category_edit.getVisibility() == View.VISIBLE
					&& "".equals(item.category_edit.getText().toString())) {
				Alert.getInstance().show(this, "Category is empty");
				item.category_edit.requestFocus();
				return false;
			}
		}

		return true;
	}

	private void saveDetailSchedule(long newScheduleId) {
		for (int index = 0; index < list.getChildCount(); index++) {
			ScheduleItem detailItem = (ScheduleItem) list.getChildAt(index);
			if (detailItem.getBudget() == 0) {
				continue;
			}

			long category_id = detailItem.getCategory();
			if (detailItem.category_edit.getVisibility() == View.VISIBLE) {
				category_id = SqlHelper.instance.insert(
						"Category",
						new String[] { "Name", "User_Color" },
						new String[] {
								detailItem.category_edit.getText().toString(),
								String.valueOf(detailItem.category_edit
										.getTag()) });
			}
			SqlHelper.instance.insert(
					"ScheduleDetail",
					new String[] { "Budget", "Category_id", "Schedule_id" },
					new String[] { Converter.toString(detailItem.getBudget()),
							String.valueOf(category_id),
							String.valueOf(newScheduleId) });
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
