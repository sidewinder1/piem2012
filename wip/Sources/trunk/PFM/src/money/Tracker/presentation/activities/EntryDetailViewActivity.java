package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.repository.EntryDetailRepository;
import money.Tracker.repository.EntryRepository;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * @author Kaminari.hp Control flows of display detail expenses and incomes
 *         management function.
 */
public class EntryDetailViewActivity extends BaseActivity {
	public static long sEntryId;
	LinearLayout mEntryList;
	TextView mEntryTitle;
	TextView mDateView;
	TextView mTotalEntryTitle, mTotalEntryValue, mRemainBudgetTitle,
			mRemainBudgetValue;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_detail_view);

		Bundle extras = getIntent().getExtras();
		sEntryId = extras.getLong("entry_id");
		mEntryList = (LinearLayout) findViewById(R.id.entry_detail_list_item);
		mEntryTitle = (TextView) findViewById(R.id.entry_detail_view_title);
		mTotalEntryTitle = (TextView) findViewById(R.id.entry_detail_day_total_entry_title);
		mTotalEntryValue = (TextView) findViewById(R.id.entry_detail_day_total_entry_value);
		mRemainBudgetTitle = (TextView) findViewById(R.id.entry_detail_total_budget_title);
		mDateView = (TextView) findViewById(R.id.entry_detail_display_date);

		mRemainBudgetValue = (TextView) findViewById(R.id.entry_detail_total_budget_value);
		bindData();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	/**
	 * Bind data to view for displaying.
	 */
	private void bindData() {
		ArrayList<IModelBase> allEntries = EntryRepository.getInstance()
				.getData(new StringBuilder("Id=").append(sEntryId).toString());
		if (allEntries == null || allEntries.size() == 0) {
			return;
		}

		Entry entry = (Entry) allEntries.get(0);
		mEntryTitle.setText(getResources().getString(
				(entry.getType() == 1 ? R.string.entry_daily_expense_title
						: R.string.entry_daily_income_title)));

		mDateView.setText(Converter.toString(entry.getDate(), "dd/MM/yyyy"));

		EntryDetailRepository.getInstance().updateData(
				new StringBuilder("Entry_Id = ").append(sEntryId).toString(),
				"Category_Id");
		mTotalEntryTitle
				.setText(getResources()
						.getString(
								(entry.getType() == 1 ? R.string.entry_daily_total_expense_title
										: R.string.entry_daily_total_income_title)));

		mEntryList.removeAllViews();
		for (ArrayList<EntryDetail> array : EntryDetailRepository.getInstance().entries
				.values()) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			mEntryList
					.addView(new EntryDetailCategoryView(this, array), params);
		}

		// Updated data for summary part.
		// Get total expense or income.
		EntryRepository.getInstance()
				.updateData(
						new StringBuilder("Type = ").append(entry.getType())
								.toString());
		ArrayList<Entry> entries = EntryRepository.getInstance().orderedEntries
				.get(Converter.toString(entry.getDate(), "MM/yyyy"));
		double total_entry = 0;
		mTotalEntryValue.setText(Converter.toString(entry.getTotal(null)));
		for (Entry entryItem : entries) {
			total_entry += entryItem.getTotal(null);
		}

		if (entry.getType() == 0) {
			mRemainBudgetTitle.setVisibility(View.GONE);
			mRemainBudgetValue.setVisibility(View.GONE);
			return;
		}

		// Get total budget.
		Cursor totalBudgetCursor = SqlHelper.instance.select(
				"Schedule",
				"Budget, Type",
				new StringBuilder("End_date = '")
						.append(Converter.toString(DateTimeHelper
								.getLastDayOfWeek(entry.getDate())))
						.append("' OR End_date = '")
						.append(Converter.toString(DateTimeHelper
								.getLastDateOfMonth(
										entry.getDate().getYear() + 1900, entry
												.getDate().getMonth())))
						.append("'").toString());
		if (totalBudgetCursor != null && totalBudgetCursor.moveToFirst()) {
			do {
				mRemainBudgetValue
						.setText(Converter.toString(totalBudgetCursor
								.getDouble(0) - total_entry));
				mRemainBudgetTitle
						.setText(getResources()
								.getString(
										totalBudgetCursor.getInt(1) == 1 ? R.string.entry_total_budget_month
												: R.string.entry_total_budget_week));
				if (totalBudgetCursor.getInt(1) == 0) {
					break;
				}
			} while (totalBudgetCursor.moveToNext());
			mRemainBudgetTitle.setVisibility(View.VISIBLE);
			mRemainBudgetValue.setVisibility(View.VISIBLE);
		} else {
			mRemainBudgetTitle.setVisibility(View.GONE);
			mRemainBudgetValue.setVisibility(View.GONE);
		}
		
		totalBudgetCursor.close();
	}

	/**
	 * Handle when user clicks edit button.
	 * @param v
	 * Edit button.
	 */
	public void editBtnClicked(View v) {
		Intent edit = new Intent(this, EntryEditActivity.class);
		edit.putExtra("entry_id", sEntryId);
		startActivity(edit);
	}
}
