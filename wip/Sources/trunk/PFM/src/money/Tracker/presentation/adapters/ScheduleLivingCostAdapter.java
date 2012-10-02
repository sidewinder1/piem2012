package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.customviews.*;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.repository.CategoryRepository;

public class ScheduleLivingCostAdapter extends ArrayAdapter<DetailSchedule> {
	private ArrayList<DetailSchedule> array;
	private CategoryAdapter categoryAdapter;

	public ScheduleLivingCostAdapter(Context context, int resource,
			ArrayList<DetailSchedule> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.array = objects;

		// Create an ArrayAdapter using the string array and a default
		// spinner layout
		categoryAdapter = new CategoryAdapter(getContext(),
				R.layout.dropdown_list_item,
				CategoryRepository.getInstance().categories);

		categoryAdapter.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ScheduleItem scheduleItemView = (ScheduleItem) convertView;

		if (scheduleItemView == null) {
			scheduleItemView = new ScheduleItem(getContext());
		}

		final DetailSchedule livingCost = array.get(position);

		if (livingCost != null) {
			final Spinner category = ((ScheduleItem) scheduleItemView).category;
			final EditText budget = ((ScheduleItem) scheduleItemView).budget;
			Button addButton = ((ScheduleItem) scheduleItemView).addBtn;
			Button removeButton = ((ScheduleItem) scheduleItemView).removeBtn;

			// Set tag to create a sign for removing later.
			removeButton.setTag(position);

			// Set tag to create a sign for adding later.
			addButton.setTag(position);

			budget.setHint(String.valueOf(livingCost.getBudget()));
			budget.setTag(position);
			// Apply the adapter to the spinner
			category.setAdapter(categoryAdapter);
			category.setSelection(livingCost.getCategory());

			category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					Category item = (Category) parent.getItemAtPosition(pos);
					if (item != null) {
						parent.setTag(item.getId());
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			budget.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					// TODO Auto-generated method stub
					if (((EditText) v).getText() + "" != "") {
						DetailSchedule item = array.get(Integer.parseInt(String
								.valueOf(v.getTag())));
						item.setBudget(Double.parseDouble(String
								.valueOf(((EditText) v).getText())));
					}

					return false;
				}
			});

			// Add new schedule item.
			addButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String value = budget.getText().toString();

					if (value + "" == "") {
						value = budget.getHint().toString();
					}

					if (value + "" == "") {
						value = "0";
					}

					livingCost.setBudget(Double.parseDouble(value));
					livingCost.setCategory(Integer.parseInt(String.valueOf(category.getTag())));
					
					// category.setSelection(livingCost.getCategory());

					array.add(Integer.parseInt(((Button) v).getTag() + "") + 1,
							new DetailSchedule(0, 2000));
					notifyDataSetChanged();

				}
			});

			// Remove this schedule item.
			removeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					array.remove(Integer.parseInt(((Button) v).getTag() + ""));
					notifyDataSetChanged();
				}
			});
		}

		return scheduleItemView;
	}
}
