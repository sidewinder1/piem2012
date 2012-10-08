package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.EditText;

public class ScheduleItem extends LinearLayout {
	public Spinner category;
	public EditText budget;
	public Button addBtn, removeBtn;
	public EditText category_edit;

	public ScheduleItem(Context context, CategoryAdapter categoryAdapter) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.schedule_edit_item, this, true);

		category = (Spinner) findViewById(R.id.schedule_item_category);
		budget = (EditText) findViewById(R.id.schedule_item_price);
		addBtn = (Button) findViewById(R.id.schedule_item_add);
		removeBtn = (Button) findViewById(R.id.schedule_item_remove);
		category_edit = (EditText) findViewById(R.id.schedule_item_category_edit);
		
		// Apply the adapter to the spinner.
		category.setAdapter(categoryAdapter);
	}

	public int getCategory() {
		return CategoryRepository.getInstance().getId(
				category.getSelectedItemPosition());
	}

	public double getBudget() {
		String budgetValue = String.valueOf(budget.getText());
		if ("".equals(budgetValue)) {
			budgetValue = "0";
		}
		return Double.parseDouble(budgetValue);
	}
}
