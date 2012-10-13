package money.Tracker.presentation.customviews;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryEditCategoryView extends LinearLayout {
	private Spinner category;
	private TextView total_money;
	private Button addBtn, removeBtn;
	private LinearLayout category_list;
	private CategoryAdapter categoryAdapter;

	public EntryEditCategoryView(Context context, ArrayList<EntryDetail> data) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_category_item, this, true);

		// Get control from .xml file.
		category = (Spinner) findViewById(R.id.entry_item_category);
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

		// Initialize data for entry.
		if (data == null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			EntryEditProductView item = new EntryEditProductView(context);
			category_list.addView(item, params);
		} else {
			double total = 0;

			for (EntryDetail entryDetail : data) {
				EntryEditProductView item = new EntryEditProductView(context);
				item.setName(entryDetail.getName());
				item.setCost(String.valueOf(entryDetail.getMoney()));

				total += Double.parseDouble(item.getCost());

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				category_list.addView(item, params);
			}

			total_money.setText(String.valueOf(total));
		}

		// Add event to add button.
		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinearLayout parent = (LinearLayout) v.getParent().getParent()
						.getParent().getParent();
				EntryEditCategoryView item = new EntryEditCategoryView(
						getContext(), null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

				parent.addView(item, params);
			}
		});
	}

	public void save(String date, int type, int entry_id) {
		String[] columns = new String[] { "Name", "Money", "Category_Id",
				"Entry_Id" };
		String[] values;
		String table = "Entry";
		String subTable = "EntryDetail";

		long id = entry_id;

		if (entry_id == -1) {
			id = SqlHelper.instance.insert(
					table,
					new String[] { "Date", "Type" },
					new String[] {
							Converter.toString(Converter.toDate(date,
									"dd/MM/yyyy")), String.valueOf(type) });
		} else {
			SqlHelper.instance.update(
					table,
					new String[] { "Date", "Type" },
					new String[] {
							Converter.toString(Converter.toDate(date,
									"dd/MM/yyyy")), String.valueOf(type) },
					new StringBuilder("Id = ").append(entry_id).toString());
			SqlHelper.instance.delete(subTable,
					new StringBuilder("Entry_Id = ").append(entry_id)
							.toString());
		}

		for (int index = 0; index < category_list.getChildCount(); index++) {
			EntryEditProductView product = (EntryEditProductView) category_list
					.getChildAt(index);

			if (product == null) {
				return;
			}

			values = new String[] {
					product.getName(),
					product.getCost(),
					String.valueOf(CategoryRepository.getInstance().getId(
							category.getSelectedItemPosition())),
					String.valueOf(id) };

			SqlHelper.instance.insert(subTable, columns, values);
		}
	}
}
