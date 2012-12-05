package money.Tracker.presentation.customviews;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.adapters.CategoryAdapter;
import money.Tracker.presentation.model.Category;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryEditCategoryView extends LinearLayout {
	private Spinner mCategory;
	private TextView mTotal_money;
	private Button mAddBtn, mRemoveBtn;
	private LinearLayout mCategoryList;
	private CategoryAdapter mCategoryAdapter;
	public EditText mCategoryEdit;
	private Date mEntryDate;

	public EntryEditCategoryView(Context context) {
		super(context);
	}

	public EntryEditCategoryView(Context context, ArrayList<EntryDetail> data) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_category_item, this, true);

		// Get control from .xml file.
		mCategory = (Spinner) findViewById(R.id.entry_item_category);
		mCategoryEdit = (EditText) findViewById(R.id.entry_item_category_edit);
		mTotal_money = (TextView) findViewById(R.id.entry_item_price);
		mAddBtn = (Button) findViewById(R.id.entry_item_add);
		mRemoveBtn = (Button) findViewById(R.id.entry_item_remove);
		mCategoryList = (LinearLayout) findViewById(R.id.entry_edit_category_list);

		// Initialize a CategoryAdapter.
		mCategoryAdapter = new CategoryAdapter(getContext(),
				R.layout.dropdown_list_item, new ArrayList<Category>(
						CategoryRepository.getInstance().categories));

		mCategoryAdapter.notifyDataSetChanged();

		// Set value to category.
		mCategory.setAdapter(mCategoryAdapter);
		mCategory.setTag(mCategoryEdit);

		// Initialize data for entry.
		if (data == null) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			EntryEditProductView item = new EntryEditProductView(context,
					mTotal_money, -1);
			mCategoryList.addView(item, params);
			mTotal_money.setText(Converter.toString(0D));
		} else {
			long total = 0;

			for (EntryDetail entryDetail : data) {
				EntryEditProductView item = new EntryEditProductView(context,
						mTotal_money, entryDetail.getId());
				item.setName(entryDetail.getName());
				item.setCost(Converter.toString(entryDetail.getMoney()));
				mCategory.setSelection(CategoryRepository.getInstance()
						.getIndex(entryDetail.getCategory_id()));
				total += entryDetail.getMoney();

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				mCategoryList.addView(item, params);
			}

			mTotal_money.setText(Converter.toString(total));
		}

		mCategory
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

		// Add event to add button.
		mAddBtn.setOnClickListener(new OnClickListener() {
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
							&& entryEdit.mCategoryEdit.getVisibility() == View.VISIBLE
							&& "".equals(entryEdit.mCategoryEdit.getText()
									.toString())) {
						Alert.getInstance().show(getContext(),
								"New category is empty");
						entryEdit.mCategoryEdit.requestFocus();
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

		mTotal_money.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void afterTextChanged(Editable arg0) {
				try {
					Cursor checkOverBudget = SqlHelper.instance.select(
							"AppInfo",
							"ScheduleWarn",
							new StringBuilder("UserName='")
									.append(AccountProvider.getInstance()
											.getCurrentAccount().name)
									.append("'").toString());
					long budget = PfmApplication.getTotalBudget();
					if (checkOverBudget != null
							&& checkOverBudget.moveToFirst() && budget != 0) {
						double percent = checkOverBudget.getLong(0) / 100d;

						if (budget * percent <= PfmApplication.getTotalEntry()
								+ Long.parseLong(arg0.toString())) {
							Alert.getInstance().show(
									getContext(),
									getResources().getString(
											R.string.warning_borrow_overbudget)
											.replace(
													"{0}",
													checkOverBudget
															.getString(0)));
						}
					}
				} catch (Exception e) {
					Logger.Log(e.getMessage(), "EntryEditCategoryView");
				}
			}
		});

		// Add event to handle clicking remove button.
		mRemoveBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LinearLayout list = (LinearLayout) v.getParent().getParent()
						.getParent().getParent();

				if (list.getChildCount() > 1) {
					EntryEditCategoryView item = (EntryEditCategoryView) v
							.getParent().getParent().getParent();
					if (item == null) {
						return;
					}

					for (int index = 0; index < item.mCategoryList
							.getChildCount(); index++) {
						EntryEditProductView itemOfCategoryEdit = (EntryEditProductView) item.mCategoryList
								.getChildAt(index);

						if (itemOfCategoryEdit != null) {
							itemOfCategoryEdit.removeItem();
						}
					}

					// Insert userColor to db again.
					SqlHelper.instance.insert("UserColor",
							new String[] { "User_Color" },
							new String[] { String.valueOf(item.mCategoryEdit
									.getTag()) });

					list.removeView(item);
				}
			}
		});

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

	public void setEntryDate(Date date) {
		mEntryDate = date;
	}

	public boolean removeEmptyEntry() {
		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView item = (EntryEditProductView) mCategoryList
					.getChildAt(index);

			if (item != null && "".equals(item.getName())) {
				mCategoryList.removeView(item);
			}
		}

		return mCategoryList.getChildCount() == 0;
	}

	public boolean removeEmptyCatagory() {
		boolean check = false;

		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView item = (EntryEditProductView) mCategoryList
					.getChildAt(index);

			if (item != null && "".equals(item.getName())) {
				check = true;
			}
		}

		return check;
	}

	public String checkBeforeSave() {
		if (mCategoryEdit.getVisibility() == View.VISIBLE) {
			if ("".equals(mCategoryEdit.getText().toString())) {
				return "Category is empty!";
			} else {
				// Check duplicate.
				Cursor oldCategory = SqlHelper.instance.select(
						"Category",
						"Name",
						new StringBuilder("Name = '")
								.append(mCategoryEdit.getText().toString())
								.append("'").toString());
				if (oldCategory != null && oldCategory.moveToFirst()) {
					return "Duplicate category";
				}
			}
		}

		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView item = (EntryEditProductView) mCategoryList
					.getChildAt(index);
			String temp = item.checkBeforeSave();
			if (item != null && temp != null) {
				return temp;
			}
		}

		return null;
	}

	public ArrayList<EntryDetail> getDetails() {
		ArrayList<EntryDetail> data = new ArrayList<EntryDetail>();
		long category_id_str = CategoryRepository.getInstance().getId(
				mCategory.getSelectedItemPosition());
		if (mCategoryEdit.getVisibility() == View.VISIBLE) {

			category_id_str = CategoryRepository.getInstance().getId(
					mCategoryEdit.getText().toString());
		}

		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView item = (EntryEditProductView) mCategoryList
					.getChildAt(index);
			if (item != null && !"".equals(item.getName())) {
				EntryDetail entryDetail = new EntryDetail(category_id_str,
						item.getName(), item.getMoney());
				data.add(entryDetail);
			}
		}

		return data;
	}

	public void save(long entry_id) {
		String[] columns = new String[] { "Name", "Money", "Category_Id",
				"Entry_Id" };
		String[] values;
		String subTable = "EntryDetail";

		String category_id_str = String.valueOf(CategoryRepository
				.getInstance().getId(mCategory.getSelectedItemPosition()));
		// Save custom category
		if (mCategoryEdit.getVisibility() == View.VISIBLE) {
			category_id_str = String.valueOf(SqlHelper.instance.insert(
					"Category",
					new String[] { "Name", "User_Color" },
					new String[] { mCategoryEdit.getText().toString(),
							String.valueOf(mCategoryEdit.getTag()) }));

			CategoryRepository.getInstance().updateData();
		}

		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView product = (EntryEditProductView) mCategoryList
					.getChildAt(index);

			Logger.Log("Length of items: " + mCategory.getChildCount(),
					"EntryEditCategoryView");
			if (product == null || product.getMoney() == 0) {
				continue;
			}

			values = new String[] { product.getName(),
					String.valueOf(product.getMoney()), category_id_str,
					String.valueOf(entry_id) };

			if (product.Id == -1) {
				SqlHelper.instance.insert(subTable, columns, values);
			} else {
				SqlHelper.instance.update(subTable, columns, values, "Id="
						+ product.Id);
			}
		}
	}
}
