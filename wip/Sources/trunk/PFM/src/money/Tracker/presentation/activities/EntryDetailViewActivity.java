package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.presentation.customviews.EntryDetailCategoryView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.EntryDetailRepository;
import money.Tracker.repository.EntryRepository;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class EntryDetailViewActivity extends Activity {
	public static long sEntryId;
	LinearLayout entry_list;
	TextView entry_title;
	TextView total_entry_title, total_entry_value, remain_budget_title,
			remain_budget_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_detail_view);

		Bundle extras = getIntent().getExtras();
		sEntryId = extras.getLong("entry_id");
		entry_list = (LinearLayout) findViewById(R.id.entry_detail_list_item);
		entry_title = (TextView) findViewById(R.id.entry_detail_view_title);
		total_entry_title = (TextView) findViewById(R.id.entry_detail_day_total_entry_title);
		total_entry_value = (TextView) findViewById(R.id.entry_detail_day_total_entry_value);
		remain_budget_title = (TextView) findViewById(R.id.entry_detail_total_budget_title);
		remain_budget_value = (TextView) findViewById(R.id.entry_detail_total_budget_value);
		bindData();
	}

	@Override
	protected void onRestart() {
		bindData();
		super.onRestart();
	}

	private void bindData() {
		Entry entry = (Entry) EntryRepository.getInstance()
				.getData(new StringBuilder("Id=").append(sEntryId).toString())
				.get(0);
		entry_title.setText(getResources().getString(
				(entry.getType() == 1 ? R.string.entry_daily_expense_title
						: R.string.entry_daily_income_title)).replace("{0}",
				Converter.toString(entry.getDate(), "dd/MM/yyyy")));
		EntryDetailRepository.getInstance().updateData(
				new StringBuilder("Entry_Id = ").append(sEntryId).toString(),
				"Category_Id");
		total_entry_title
				.setText(getResources()
						.getString(
								(entry.getType() == 1 ? R.string.entry_daily_total_expense_title
										: R.string.entry_daily_total_income_title)));

		entry_list.removeAllViews();
		for (ArrayList<EntryDetail> array : EntryDetailRepository.getInstance().entries
				.values()) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

			entry_list
					.addView(new EntryDetailCategoryView(this, array), params);
		}

		// Updated data for summary part.
		// Get total expense or income.
		EntryRepository.getInstance()
				.updateData(
						new StringBuilder("Type = ").append(entry.getType())
								.toString());
		ArrayList<Entry> entries = EntryRepository.getInstance().orderedEntries
				.get(Converter.toString(entry.getDate(), "MMMM, yyyy"));
		double total_entry = 0;
		total_entry_value.setText(Converter.toString(entry.getTotal()));
		for (Entry entryItem : entries) {
			total_entry += entryItem.getTotal();
		}

		if (entry.getType() == 0) {
			remain_budget_title.setVisibility(View.GONE);
			remain_budget_value.setVisibility(View.GONE);
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
				remain_budget_value
						.setText(Converter.toString(totalBudgetCursor
								.getDouble(0) - total_entry));
				remain_budget_title
						.setText(getResources()
								.getString(
										totalBudgetCursor.getInt(1) == 1 ? R.string.entry_total_budget_month
												: R.string.entry_total_budget_week));
				if (totalBudgetCursor.getInt(1) == 0) {
					break;
				}
			} while (totalBudgetCursor.moveToNext());
			remain_budget_title.setVisibility(View.VISIBLE);
			remain_budget_value.setVisibility(View.VISIBLE);
		} else {
			remain_budget_title.setVisibility(View.GONE);
			remain_budget_value.setVisibility(View.GONE);
		}
	}

	public void editBtnClicked(View v) {
		Intent edit = new Intent(this, EntryEditActivity.class);
		edit.putExtra("entry_id", sEntryId);
		startActivity(edit);
	}
}
