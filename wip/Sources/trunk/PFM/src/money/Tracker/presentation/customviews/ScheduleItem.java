package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.EditText;

public class ScheduleItem extends LinearLayout {
	public Spinner mCategory;
	private EditText mBudget;
	public Button addBtn, removeBtn;
	public EditText mCategoryEdit;
	public long Id;

	public ScheduleItem(Context context, CategoryAdapter categoryAdapter,
			long id) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.schedule_edit_item, this, true);
		Id = id;
		mCategory = (Spinner) findViewById(R.id.schedule_item_category);
		mBudget = (EditText) findViewById(R.id.schedule_item_price);
		addBtn = (Button) findViewById(R.id.schedule_item_add);
		removeBtn = (Button) findViewById(R.id.schedule_item_remove);
		mCategoryEdit = (EditText) findViewById(R.id.schedule_item_category_edit);

		// Apply the adapter to the spinner.
		mCategory.setAdapter(categoryAdapter);
		
		mCategoryEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			public void onFocusChange(View arg0, boolean arg1) {
				if (!arg1
						&& CategoryRepository.getInstance().isExisted(
								mCategoryEdit.getText().toString())) {
					Alert.getInstance().show(
							getContext(),
							getResources().getString(
									R.string.existed_category_message));
					mCategory.setSelection(CategoryRepository.getInstance().getIndex(mCategoryEdit.getText().toString()));
					mCategory.setVisibility(View.VISIBLE);
					mCategoryEdit.setVisibility(View.GONE);
					((CategoryAdapter)mCategory.getAdapter()).notifyDataSetChanged();
				}
			}
		});
	}

	public ScheduleItem(Context context) {
		super(context);
	}

	public long getCategory() {
		return CategoryRepository.getInstance().getId(
				mCategory.getSelectedItemPosition());
	}

	public void setBudget(long money){
		mBudget.setText(Converter.toString(money, "####"));
	}
	
	public EditText getBudgetText(){
		return mBudget;
	}
	
	public long getBudget() {
		String budgetValue = String.valueOf(mBudget.getText());
		if ("".equals(budgetValue)) {
			budgetValue = "0";
		}
		return Converter.toLong(budgetValue);
	}
}
