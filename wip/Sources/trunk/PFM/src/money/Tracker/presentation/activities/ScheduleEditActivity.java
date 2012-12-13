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
import android.widget.Button;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
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
	private EditText mStartDateEdit;
	private EditText mEndDateEdit;
	private Button mPeriodic;
	private boolean mIsWeek;
	private EditText mTotalBudget;
	private long mPassedScheduleId = -1;
	private LinearLayout mList;
	private int mLastAddedItem;
	private CategoryAdapter mCategoryAdapter;
	private String mRemovedScheduleDetails = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_edit);
		// new ScheduleRepository();
		Bundle extras = getIntent().getExtras();
		mPassedScheduleId = extras.getLong("schedule_id");

		// Add item for detail schedule.
		mCategoryAdapter = new CategoryAdapter(this,
				R.layout.dropdown_list_item, new ArrayList<Category>(
						CategoryRepository.getInstance().categories));

		mCategoryAdapter.notifyDataSetChanged();

		mList = (LinearLayout) findViewById(R.id.list);

		mTotalBudget = (EditText) findViewById(R.id.schedule_total_budget);

		mTotalBudget.setOnFocusChangeListener(completeAfterLostFocus);

		// Add event to total_budget to handle business logic.
		mTotalBudget.addTextChangedListener(new TextWatcher() {
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

		mStartDateEdit = (EditText) findViewById(R.id.schedule_start_date);
		mStartDateEdit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		mEndDateEdit = (EditText) findViewById(R.id.schedule_end_date);
		mPeriodic = (Button) findViewById(R.id.periodic);
		mPeriodic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setChecked(!mIsWeek);
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		String initialValue = mTotalBudget.getText().toString();
		if (initialValue + "" == "") {
			initialValue = "0";
		}

		// New Mode
		if (mPassedScheduleId == -1) {
			updateDisplay();
			addToList(
					new DetailSchedule(-1, 0, Converter.toLong(initialValue)),
					-1, false);
		} else { // Edit mode
			TextView title = (TextView) findViewById(R.id.schedule_edit_tilte);
			title.setText(getResources()
					.getString(R.string.schedule_edit_title));

			Schedule schedule = (Schedule) ScheduleRepository.getInstance()
					.getData("Id = " + mPassedScheduleId).get(0);
			if (schedule != null) {
				setChecked(schedule.type == 1);
				mMonth = schedule.start_date.getMonth();
				mDay = schedule.start_date.getDate();
				mYear = schedule.start_date.getYear() + 1900;
				mStartDateEdit.setText(Converter.toString(schedule.start_date,
						"dd/MM/yyyy"));
				mEndDateEdit.setText(Converter.toString(schedule.end_date,
						"dd/MM/yyyy"));
				mTotalBudget.setText(Converter
						.toString(schedule.budget, "####"));
			}

			ArrayList<DetailSchedule> values = DetailScheduleRepository
					.getInstance()
					.getData("Schedule_Id = " + mPassedScheduleId);
			if (values.size() == 0) {
				addToList(
						new DetailSchedule(-1, 0,
								Converter.toLong(initialValue)), -1, false);
			} else {
				for (DetailSchedule value : values) {
					addToList(value, -1, true);
				}
			}
		}
	}

	private void setChecked(boolean isWeek) {
		mIsWeek = isWeek;
		mPeriodic.setBackgroundResource(isWeek ? R.drawable.calendar_week_icon
				: R.drawable.calendar_month_icon);
		String lastDate = mEndDateEdit.getText().toString();
		updateDisplay();

		Cursor checkExist = SqlHelper.instance.select(
				"Schedule",
				"Id",
				new StringBuilder("Type=")
						.append(isWeek ? 0 : 1)
						.append(" AND End_date='")
						.append(Converter.toString(Converter
								.toDate(mEndDateEdit.getText().toString(),
										"dd/MM/yyyy"))).append("'").toString());
		if (checkExist != null && checkExist.moveToFirst()
				&& checkExist.getLong(0) != mPassedScheduleId) {
			Alert.getInstance().show(getBaseContext(),
					getResources().getString(R.string.schedule_exist_message));
			setChecked(!isWeek);
			mEndDateEdit.setText(lastDate);
		}
	}

	private void addToList(DetailSchedule detail, int index, boolean init) {
		ScheduleItem itemView = new ScheduleItem(this, mCategoryAdapter,
				detail.getId());
		itemView.mCategory.setTag(itemView.mCategoryEdit);

		if (init) {
			itemView.setBudget(detail.getBudget());
		} else {
			itemView.setBudget(detail.getBudget());
		}

		// Add events to to detail budget to handle business logic.
		itemView.getBudgetText().setOnFocusChangeListener(
				completeAfterLostFocus);

		itemView.getBudgetText().addTextChangedListener(new TextWatcher() {
			long sValue = 0;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (!"".equals(s.toString())) {
					sValue = Converter.toLong(s.toString());
				}
			}

			public void afterTextChanged(Editable s) {
				if (!"".equals(s.toString())) {
					updateTotalBudget(Converter.toLong(s.toString()) > sValue);
				}
			}
		});

		itemView.mCategory
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> parent,
							View view, int pos, long id) {
						Category item = (Category) parent
								.getItemAtPosition(pos);
						if (item != null
								&& getResources().getString(R.string.others)
										.equals(item.getName())) {
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

		itemView.mCategory.setSelection(CategoryRepository.getInstance()
				.getIndex(detail.getCategory()));
		if (index < 0) {
			mList.addView(itemView);
		} else {
			mList.addView(itemView, index);
		}

		itemView.setFocusable(true);
		itemView.requestFocus();

		itemView.addBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int lastItem = 0;
				for (int index = 0; index < mList.getChildCount(); index++) {
					if (mList.getChildAt(index) == v.getParent().getParent()) {
						lastItem = index + 1;
					}
					ScheduleItem item = (ScheduleItem) mList.getChildAt(index);
					if (item.getBudget() <= 0) {
						Alert.getInstance().show(
								getBaseContext(),
								getResources().getString(
										R.string.schedule_empty_exists));
						item.getBudgetText().setFocusable(true);
						item.getBudgetText().requestFocus();
						return;
					}

					if (item.mCategoryEdit.getVisibility() == View.VISIBLE
							&& "".equals(item.mCategoryEdit.getText()
									.toString())) {
						Alert.getInstance().show(
								getBaseContext(),
								getResources().getString(
										R.string.schedule_empty_exists));
						item.mCategoryEdit.setFocusable(true);
						item.mCategoryEdit.requestFocus();
						return;
					}
				}

				mLastAddedItem = lastItem;
				addToList(new DetailSchedule(-1, 0, getNextHint()),
						mLastAddedItem, false);
			}
		});

		itemView.removeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (mList.getChildCount() > 1) {
					ScheduleItem item = (ScheduleItem) v.getParent()
							.getParent();
					if (item == null) {
						return;
					}

					if (item.Id != -1) {
						mRemovedScheduleDetails += item.Id + ",";
					}

					// Insert userColor to db again.
					SqlHelper.instance.insert("UserColor",
							new String[] { "User_Color" },
							new String[] { String.valueOf(item.mCategoryEdit
									.getTag()) });

					mList.removeView(item);
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
					((EditText) v).setText(Converter.toString(
							Converter.toLong(str), "####"));
				}
			}
		}
	};

	public void updateHint() {
		if (mList.getChildCount() == 0) {
			return;
		}

		ScheduleItem scheduleItem = (ScheduleItem) mList
				.getChildAt(mLastAddedItem);

		if (scheduleItem == null) {
			return;
		}

		EditText lastBudget = scheduleItem.getBudgetText();
		if (lastBudget == null) {
			return;
		}

		if ("".equals(lastBudget.getText().toString())) {
			lastBudget.setHint(Converter.toString(getNextHint()));
		}
	}

	public long getNextHint() {
		return Math.max(0, getTotalBudget() - getTotalDetailBudget());
	}

	public long getTotalDetailBudget() {
		long total = 0;
		for (int index = 0; index < mList.getChildCount(); index++) {
			total += ((ScheduleItem) mList.getChildAt(index)).getBudget();
		}

		return Math.max(0, total);
	}

	public long getTotalBudget() {
		String budget_value = mTotalBudget.getText().toString();
		if ("".equals(budget_value)) {
			budget_value = mTotalBudget.getHint().toString();

			if ("".equals(budget_value) || budget_value.contains(" ")) {
				budget_value = "0";
			}
		}

		return Math.max(0, Converter.toLong(budget_value));
	}

	public boolean updateTotalBudget(boolean eanbleDialog) {
		long totalDetail = getTotalDetailBudget();

		if ("".equals(mTotalBudget.getText().toString())) {
			mTotalBudget.setHint(Converter.toString(totalDetail));
		} else {
			if (getTotalBudget() < totalDetail && eanbleDialog) {
				Alert.getInstance().showDialog(this,
						getResources().getString(R.string.schedule_overbudget),
						new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mTotalBudget.setText(Converter.toString(
										getTotalDetailBudget(), "####"));
								mTotalBudget.requestFocus();
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
			Alert.getInstance().show(
					this,
					getResources().getString(
							R.string.schedule_please_input_total_budget));
			return;
		}

		if (getTotalBudget() < getTotalDetailBudget()) {
			Alert.getInstance().show(
					this,
					getResources().getString(
							R.string.schedule_over_total_budget));
			return;
		}

		if (!hasNewCategory()) {
			return;
		}

		String Time_id = (!mIsWeek ? "1" : "0");

		if (mPassedScheduleId != -1) {
			String budget_value = String.valueOf(Converter.toLong(mTotalBudget
					.getText().toString()));
			if ("".equals(budget_value)) {
				budget_value = String
						.valueOf(mTotalBudget.getHint().toString());
			}

			// Update schedule record.
			updateSchedule(Time_id, budget_value);
		} else {
			// Add new mode.
			Cursor scheduleCursor = SqlHelper.instance.select(
					"Schedule",
					"End_date",
					new StringBuilder("Type = ")
							.append(Time_id)
							.append(" AND End_date = '")
							.append(Converter.toString(Converter.toDate(
									mEndDateEdit.getText().toString(),
									"dd/MM/yyyy"))).append("'").toString());
			if (scheduleCursor != null && scheduleCursor.moveToFirst()) {
				Alert.getInstance().show(
						this,
						getResources().getString(
								R.string.schedule_exist_message));
				return;
			}

			// Add new schedule.
			addSchedule(Time_id);
		}

		if (mRemovedScheduleDetails.length() > 1) {
			// Delete items that removed before.
			SqlHelper.instance
					.delete("ScheduleDetail",
							"Id IN ("
									+ mRemovedScheduleDetails
											.substring(0,
													mRemovedScheduleDetails
															.length() - 1)
									+ ")");
		}

		CategoryRepository.getInstance().updateData();

		try {
			if (!SynchronizeTask.isSynchronizing()
					&& Boolean.parseBoolean(XmlParser.getInstance()
							.getConfigContent("autoSync"))
					&& !"pfm.com".equals(AccountProvider.getInstance()
							.getCurrentAccount().type)) {
				SynchronizeTask task = new SynchronizeTask();
				task.execute();
			}
		} catch (Exception e) {
			Logger.Log(e.getMessage(), "ScheduleEditActivity");
		}

		setResult(100);
		this.finish();
	}

	private void updateSchedule(String Time_id, String budget_value) {
		SqlHelper.instance
				.update("Schedule",
						new String[] { "Budget", "Start_date", "End_date",
								"Type" },
						new String[] {
								budget_value,
								Converter.toString(Converter.toDate(
										mStartDateEdit.getText().toString(),
										"dd/MM/yyyy")),
								Converter.toString(Converter.toDate(
										mEndDateEdit.getText().toString(),
										"dd/MM/yyyy")), Time_id },
						new StringBuilder("Id = ").append(mPassedScheduleId)
								.toString());

		// Delete all records that have schedule id equals
		// passed_schedule_id
		// SqlHelper.instance.delete("ScheduleDetail", new StringBuilder(
		// "Schedule_Id = ").append(passed_schedule_id).toString());
		// Insert new.
		saveDetailSchedule(mPassedScheduleId);
		Alert.getInstance().show(this, "Updated 1 record sucessfully");
	}

	private void addSchedule(String Time_id) {
		long newScheduleId = SqlHelper.instance
				.insert("Schedule",
						new String[] { "Budget", "Start_date", "End_date",
								"Type" },
						new String[] {
								String.valueOf(getTotalBudget()),
								Converter.toString(Converter.toDate(
										mStartDateEdit.getText().toString(),
										"dd/MM/yyyy")),
								Converter.toString(Converter.toDate(
										mEndDateEdit.getText().toString(),
										"dd/MM/yyyy")), Time_id });
		if (newScheduleId != -1) {
			saveDetailSchedule(newScheduleId);

			Alert.getInstance().show(this,
					getResources().getString(R.string.saved));
		} else {
			Alert.getInstance().show(this,
					getResources().getString(R.string.error_save));
		}
	}

	public boolean hasNewCategory() {
		for (int index = 0; index < mList.getChildCount(); index++) {
			ScheduleItem item = (ScheduleItem) mList.getChildAt(index);
			if (item.mCategoryEdit.getVisibility() == View.VISIBLE
					&& "".equals(item.mCategoryEdit.getText().toString())) {
				Alert.getInstance().show(
						this,
						getResources()
								.getString(R.string.schedule_empty_exists));
				item.mCategoryEdit.requestFocus();
				return false;
			}
		}

		return true;
	}

	private void saveDetailSchedule(long newScheduleId) {
		for (int index = 0; index < mList.getChildCount(); index++) {
			ScheduleItem detailItem = (ScheduleItem) mList.getChildAt(index);
			if (detailItem.getBudget() == 0) {
				continue;
			}

			long category_id = detailItem.getCategory();
			if (detailItem.mCategoryEdit.getVisibility() == View.VISIBLE) {
				category_id = SqlHelper.instance.insert(
						"Category",
						new String[] { "Name", "User_Color" },
						new String[] {
								detailItem.mCategoryEdit.getText().toString(),
								String.valueOf(detailItem.mCategoryEdit
										.getTag()) });
			}

			long budget = detailItem.getBudget();

			// Check existed category.
			Cursor categoryCheck = SqlHelper.instance.select("ScheduleDetail",
					"Id, Budget",
					new StringBuilder("Schedule_id=").append(newScheduleId)
							.append(" AND Category_id=").append(category_id)
							.toString());
			if (categoryCheck != null && categoryCheck.moveToFirst()
					&& detailItem.Id != categoryCheck.getLong(0)) {
				detailItem.Id = categoryCheck.getLong(0);
				budget += categoryCheck.getLong(1);
			}

			String[] columns = new String[] { "Budget", "Category_id",
					"Schedule_id" };
			String[] values = new String[] { String.valueOf(budget),
					String.valueOf(category_id), String.valueOf(newScheduleId) };
			if (detailItem.Id == -1) {
				SqlHelper.instance.insert("ScheduleDetail", columns, values);
			} else {
				SqlHelper.instance.update("ScheduleDetail", columns, values,
						"Id=" + detailItem.Id);
			}
		}
	}

	public void cancelBtnClicked(View v) {
		setResult(100);
		this.finish();
	}

	// updates the date in the TextView
	private void updateDisplay() {
		Date startDate = DateTimeHelper.getDate(mYear, mMonth, mDay);
		mStartDateEdit.setText(Converter.toString(startDate, "dd/MM/yyyy"));
		Date endDate;

		if (!mIsWeek) {
			endDate = DateTimeHelper.getLastDateOfMonth(mYear, mMonth);
		} else {
			endDate = DateTimeHelper.getLastDayOfWeek(startDate);
		}

		mEndDateEdit.setText(Converter.toString(endDate, "dd/MM/yyyy"));
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
