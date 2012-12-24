package money.Tracker.presentation.customviews;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class EntryEditCategoryView extends LinearLayout {
	private Spinner mCategory;
	private TextView mTotal_money;
	private Button mAddBtn, mRemoveBtn;
	private LinearLayout mCategoryList;
	private CategoryAdapter mCategoryAdapter;
	public EditText mCategoryEdit;
	private Date mCurrentDate = DateTimeHelper.now(false);
	private int mType = 1;

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
						if (item != null
								&& getResources().getString(R.string.others)
										.equals(item.getName())) {
							if (CategoryRepository.getInstance().categories
									.size() <= 25) {
								((RelativeLayout) parent.getParent())
										.setVisibility(View.GONE);
								View text = (View) parent.getTag();
								text.setVisibility(View.VISIBLE);
								text.requestFocus();

								// Change color for new category.
								Cursor color = SqlHelper.instance.select(
										"UserColor", "User_Color", null);
								try {
									if (color != null && color.moveToFirst()) {
										text.setBackgroundColor(Color
												.parseColor(color.getString(0)));
										text.setTag(color.getString(0));
										SqlHelper.instance
												.delete("UserColor",
														new StringBuilder(
																"User_Color = '")
																.append(color
																		.getString(0))
																.append("'")
																.toString());
									}
								} catch (Exception e) {
									Logger.Log(e.getMessage(),
											"EditEntryCategoryView");
								} finally {
									color.close();
								}
							} else {
								Alert.getInstance()
										.show(getContext(),
												getResources()
														.getString(
																R.string.limited_category_message));
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
						Alert.getInstance().show(
								getContext(),
								getResources().getString(
										R.string.schedule_empty_exists));
						entryEdit.mCategoryEdit.requestFocus();
						return;
					}
				}

				EntryEditCategoryView item = new EntryEditCategoryView(
						getContext(), null);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				parent.addView(item, addedIndex, params);

				// Get first item.
				EntryEditProductView firstItem = (EntryEditProductView) item.mCategoryList
						.getChildAt(0);
				if (firstItem != null) {
					firstItem.setFocus();
				}
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
					if (mType == 0) {
						return;
					}

					Cursor checkOverBudget = SqlHelper.instance.select(
							"AppInfo",
							"ScheduleWarn",
							new StringBuilder("UserName='")
									.append(AccountProvider.getInstance()
											.getCurrentAccount().name)
									.append("'").toString());
					long[] budgetInfo = PfmApplication
							.getTotalBudget(mCurrentDate);
					if (checkOverBudget != null
							&& checkOverBudget.moveToFirst()
							&& budgetInfo[0] != 0) {
						double percent = checkOverBudget.getLong(0) / 100d;
						long expense = PfmApplication
								.getTotalEntry(mCurrentDate)
								+ Converter.toLong(arg0.toString());
						if (budgetInfo[0] <= expense) {
							Alert.getInstance().show(
									getContext(),
									getResources().getString(
											R.string.overbudget));
						} else if (budgetInfo[0] * percent <= expense) {
							Alert.getInstance()
									.show(getContext(),
											getResources()
													.getString(
															budgetInfo[1] == 1 ? R.string.warning_month_borrow_overbudget
																	: R.string.warning_week_borrow_overbudget)
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

						if (item.mCategoryList.getTag() == null) {
							item.mCategoryList.setTag("");
						}

						if (itemOfCategoryEdit != null
								&& itemOfCategoryEdit.Id != -1) {
							item.mCategoryList.setTag(item.mCategoryList
									.getTag()
									+ ""
									+ itemOfCategoryEdit.Id
									+ ",");
						}
					}

					if (list.getTag() == null) {
						list.setTag("");
					}

					list.setTag(list.getTag() + ""
							+ item.mCategoryList.getTag());

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
					mCategory.setSelection(CategoryRepository.getInstance()
							.getIndex(mCategoryEdit.getText().toString()));
					((RelativeLayout) mCategory.getParent())
							.setVisibility(View.VISIBLE);
					mCategoryEdit.setVisibility(View.GONE);
					((CategoryAdapter) mCategory.getAdapter())
							.notifyDataSetChanged();
				}
			}
		});
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

	public void updateDate(Date date) {
		mCurrentDate = date;
	}

	public void updateType(int type) {
		mType = type;
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
				return getResources().getString(R.string.schedule_empty_exists);
			} else {
				// Check duplicate.
				Cursor oldCategory = SqlHelper.instance.select(
						"Category",
						"Name",
						new StringBuilder("Name = '")
								.append(mCategoryEdit.getText().toString())
								.append("'").toString());
				if (oldCategory != null && oldCategory.moveToFirst()) {
					return getResources().getString(
							R.string.existed_category_message);
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

		String category_id_str = String.valueOf(mCategoryAdapter.mCategories
				.get((mCategory.getSelectedItemPosition())).getId());
		// Save custom category
		if (mCategoryEdit.getVisibility() == View.VISIBLE) {
			// Check duplicate.
			if (CategoryRepository.getInstance().isExisted(
					mCategoryEdit.getText().toString())) {
				category_id_str = String.valueOf(CategoryRepository
						.getInstance()
						.getId(mCategoryEdit.getText().toString()));
			} else {
				category_id_str = String.valueOf(SqlHelper.instance.insert(
						"Category", new String[] { "Name", "User_Color" },
						new String[] {
								mCategoryEdit.getText().toString().trim(),
								String.valueOf(mCategoryEdit.getTag()) }));

				CategoryRepository.getInstance().updateData();
			}
		}

		for (int index = 0; index < mCategoryList.getChildCount(); index++) {
			EntryEditProductView product = (EntryEditProductView) mCategoryList
					.getChildAt(index);

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

		if (mCategoryList.getTag() == null) {
			return;
		}

		String removedItemList = mCategoryList.getTag() + "";
		if (removedItemList.length() > 1) {
			// Delete items that removed before.
			SqlHelper.instance.delete(
					subTable,
					"Id IN ("
							+ removedItemList.substring(0,
									removedItemList.length() - 1) + ")");
		}
	}
}
