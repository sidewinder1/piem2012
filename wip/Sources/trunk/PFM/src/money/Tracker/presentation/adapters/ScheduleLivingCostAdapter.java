package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Toast;
import money.Tracker.common.utilities.CustomTextWatcher;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ScheduleEditActivity;
import money.Tracker.presentation.customviews.*;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.repository.CategoryRepository;

public class ScheduleLivingCostAdapter extends ArrayAdapter<DetailSchedule> {
	private ArrayList<DetailSchedule> array;
	private CategoryAdapter categoryAdapter;
	private int lastPosition;
	private EditText lastBudget;
	private boolean editMode;

	public ScheduleLivingCostAdapter(Context context, int resource,
			ArrayList<DetailSchedule> objects, boolean editMode) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.array = objects;
		this.editMode = editMode;
		// Create an ArrayAdapter using the string array and a default
		// spinner layout
		categoryAdapter = new CategoryAdapter(getContext(),
				R.layout.dropdown_list_item,
				CategoryRepository.getInstance().categories);

		categoryAdapter.notifyDataSetChanged();
	}

	public void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ScheduleItem scheduleItemView = (ScheduleItem) convertView;

		if (scheduleItemView == null) {
			scheduleItemView = new ScheduleItem(getContext(), categoryAdapter);
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
			if (editMode) {
				budget.setText(String.valueOf(livingCost.getBudget()));
			} else {
				if ("".equals(budget.getText().toString())) {
					budget.setHint(String.valueOf(livingCost.getBudget()));
				}
			}
			budget.setTag(position);

			if (position == lastPosition) {
				lastBudget = budget;
			}

			category.setTag(position);
			category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					Category item = (Category) parent.getItemAtPosition(pos);
					if (item != null) {
						DetailSchedule detail = array.get(Integer
								.parseInt(String.valueOf(parent.getTag())));
						detail.setCategory(item.getId());
					}
				}

				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});

			budget.addTextChangedListener(new CustomTextWatcher(budget) {
				public void afterTextChanged(Editable s) {
					if (editMode){
						return;
						}
					if (s + "" != "") {
						DetailSchedule item = array.get(Integer.parseInt(String
								.valueOf(mEditText.getTag())));
						item.setBudget(Double.parseDouble(String.valueOf(s)));

						if (!((ScheduleEditActivity) getContext())
								.updateTotalBudget()) {
							mEditText.setText(s.subSequence(0, s.length() - 1));
						}
					}
				}
			});

			// Add new schedule item.
			addButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					String value = budget.getText().toString();

					if (value + "" == "") {
						value = budget.getHint().toString();
					}

					if (value + "" == "") {
						value = "0";
					}

					int selectedIndex = Integer.parseInt(((Button) v).getTag()
							+ "");
					lastPosition = selectedIndex + 1;

					// Save value from view to model;
					DetailSchedule detailSchedule = array.get(selectedIndex);
					if (detailSchedule != null) {
						detailSchedule.setBudget(Double.parseDouble(value));
					}

					// Check before create new item.
					for (DetailSchedule detail : array) {
						if (detail.getBudget() == 0) {
							Toast.makeText(getContext(), "A slot is empty!",
									Toast.LENGTH_SHORT).show();
							return;
						}
					}

					// Create new item.
					array.add(selectedIndex + 1, new DetailSchedule(0, 0,
							getNextHint()));
					notifyDataSetChanged();
				}
			});

			// Remove this schedule item.
			removeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (array.size() < 2)
					{
						return;
					}
					
					array.remove(Integer.parseInt(((Button) v).getTag() + ""));
					notifyDataSetChanged();
				}
			});
		}

		return scheduleItemView;
	}

	public void updateHint() {
		if ("".equals(lastBudget.getText().toString())) {
			lastBudget.setHint(String.valueOf(getNextHint()));
		}
	}

	public double getNextHint() {
		double total = ((ScheduleEditActivity) getContext()).getTotalBudget();
		for (DetailSchedule item : array) {
			total -= item.getBudget();
		}

		return total;
	}
}
