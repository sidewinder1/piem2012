package money.Tracker.presentation.customviews;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryEditCategoryView extends LinearLayout {
	private Spinner category;
	private TextView total_money;
	private Button addBtn, removeBtn;
	private LinearLayout category_list;
	private CategoryAdapter categoryAdapter;
	public EditText category_edit;

	public EntryEditCategoryView(Context context, ArrayList<EntryDetail> data) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_category_item, this, true);

		// Get control from .xml file.
		category = (Spinner) findViewById(R.id.entry_item_category);
		category_edit = (EditText) findViewById(R.id.entry_item_category_edit);
		total_money = (TextView) findViewById(R.id.entry_item_price);
		addBtn = (Button) findViewById(R.id.entry_item_add);
		removeBtn = (Button) findViewById(R.id.entry_item_remove);
		category_list = (LinearLayout) findViewById(R.id.entry_edit_category_list);

		// Initialize a CategoryAdapter.
		categoryAdapter = new CategoryAdapter(getContext(),
				R.layout.dropdown_list_item, new ArrayList<Category>(
						CategoryRepository.getInstance().categories));

		categoryAdapter.notifyDataSetChanged();

		// Set value to category.
		category.setAdapter(categoryAdapter);
		category.setTag(category_edit);

		// Initialize data for entry.
		if (data == null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			EntryEditProductView item = new EntryEditProductView(context, total_money);
			category_list.addView(item, params);
			total_money.setText(String.valueOf(0D));
		} else {
			double total = 0;

			for (EntryDetail entryDetail : data) {
				EntryEditProductView item = new EntryEditProductView(context, total_money);
				item.setName(entryDetail.getName());
				item.setCost(String.valueOf(entryDetail.getMoney()));
				category.setSelection(CategoryRepository.getInstance()
						.getIndex(entryDetail.getCategory_id()));
				total += Double.parseDouble(item.getCost());

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				category_list.addView(item, params);
			}

			total_money.setText(String.valueOf(total));
		}

		category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				Category item = (Category) parent.getItemAtPosition(pos);
				if (item != null && "Others".equals(item.getName())) {
					parent.setVisibility(View.GONE);
					View text = (View) parent.getTag();
					text.setVisibility(View.VISIBLE);
					text.requestFocus();

					// Change color for new category.
					Cursor color = SqlHelper.instance.select("UserColor",
							"User_Color", null);
					if (color != null && color.moveToFirst()) {
						text.setBackgroundColor(Color.parseColor(color
								.getString(0)));
						text.setTag(color.getString(0));
						SqlHelper.instance.delete(
								"UserColor",
								new StringBuilder("User_Color = '")
										.append(color.getString(0)).append("'")
										.toString());
					}
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		// Add event to add button.
		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinearLayout parent = (LinearLayout) v.getParent().getParent()
						.getParent().getParent();
				int addedIndex = parent.getChildCount();
				for (int index = 0; index < parent.getChildCount(); index++) {
					if (parent.getChildAt(index) == v.getParent().getParent()
							.getParent()) {
						addedIndex = index + 1;
					}

					EntryEditCategoryView entryEdit = (EntryEditCategoryView) parent
							.getChildAt(index);
					if (entryEdit != null
							&& entryEdit.category_edit.getVisibility() == View.VISIBLE
							&& "".equals(entryEdit.category_edit.getText()
									.toString())) {
						Alert.getInstance().show(getContext(),
								"New category is empty");
						entryEdit.category_edit.requestFocus();
						return;
					}
				}

				EntryEditCategoryView item = new EntryEditCategoryView(
						getContext(), null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				parent.addView(item, addedIndex, params);
			}
		});

		// Add event to handle clicking remove button.
		removeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LinearLayout list = (LinearLayout) v.getParent().getParent()
						.getParent().getParent();

				if (list.getChildCount() > 1) {
					EntryEditCategoryView item = (EntryEditCategoryView) v
							.getParent().getParent().getParent();
					if (item == null) {
						return;
					}

					// Insert userColor to db again.
					SqlHelper.instance.insert("UserColor",
							new String[] { "User_Color" },
							new String[] { String.valueOf(item.category_edit
									.getTag()) });

					list.removeView(item);
				}
			}
		});
	}

	public String checkBeforeSave() {
		if (category_edit.getVisibility() == View.VISIBLE
				&& "".equals(category_edit.getText().toString())) {
			return "Category is empty!";
		}

		for (int index = 0; index < category_list.getChildCount(); index++) {
			EntryEditProductView item = (EntryEditProductView) category_list
					.getChildAt(index);
			String temp = item.checkBeforeSave();
			if (item != null && temp != null) {
				return temp;
			}
		}

		return null;
	}

	public void save(long entry_id) {
		String[] columns = new String[] { "Name", "Money", "Category_Id",
				"Entry_Id" };
		String[] values;
		String subTable = "EntryDetail";

		for (int index = 0; index < category_list.getChildCount(); index++) {
			EntryEditProductView product = (EntryEditProductView) category_list
					.getChildAt(index);

			if (product == null || product.getMoney() == 0) {
				continue;
			}

			String category_id = String.valueOf(CategoryRepository
					.getInstance().getId(category.getSelectedItemPosition()));
			Cursor oldEntryDetail = SqlHelper.instance.select(subTable, "Id, Money",
					new StringBuilder("Category_Id = ").append(category_id)
							.append(" AND Entry_Id = ").append(entry_id)
							.append(" AND Name = '").append(product.getName()).append("'")
							.toString());
			
			if (oldEntryDetail != null && oldEntryDetail.moveToFirst()) {
				values = new String[] { product.getName(), String.valueOf(product.getMoney() + oldEntryDetail.getDouble(1)),
						category_id, String.valueOf(entry_id) };

				SqlHelper.instance.update(
						subTable,
						columns,
						values,
						new StringBuilder("Id = ").append(
								oldEntryDetail.getInt(0)).toString());
			} else {
				values = new String[] { product.getName(), String.valueOf(product.getMoney()),
						category_id, String.valueOf(entry_id) };

				SqlHelper.instance.insert(subTable, columns, values);
			}
		}
	}
}
