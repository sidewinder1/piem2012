package money.Tracker.presentation.customviews;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryEditProductView extends LinearLayout {
	private EditText product, price;
	private Button addBtn, removeBtn;
	final TextView total_text_view;
	private LinearLayout.LayoutParams mLastParams;
	public long Id;

	public EntryEditProductView(Context context) {
		super(context);
		total_text_view = null;
	}

	public EntryEditProductView(Context context, TextView total_view, long id) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_product_item, this, true);
		total_text_view = total_view;
		product = (EditText) findViewById(R.id.entry_edit_product_name);
		price = (EditText) findViewById(R.id.entry_edit_product_price);
		addBtn = (Button) findViewById(R.id.entry_edit_product_item_add);
		removeBtn = (Button) findViewById(R.id.entry_edit_product_remove);
		Id = id;
		mLastParams = (LayoutParams) product.getLayoutParams();
		product.setFocusable(true);
		price.setFocusable(true);

		price.addTextChangedListener(new TextWatcher() {
			long sValue = 0;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if ("".equals(s.toString())) {
					sValue = 0;
				} else {
					sValue = Converter.toLong(s.toString());
				}
			}

			public void afterTextChanged(Editable s) {
				String cValue = s.toString();
				if ("".equals(s.toString())) {
					cValue = "0";
				}

				if ("".equals(total_text_view.getText())) {
					total_text_view.setText(Converter.toString(0));
				}

				long currentValue = Converter.toLong(String
						.valueOf(total_text_view.getText()));
				long total = currentValue + Converter.toLong(cValue.toString())
						- sValue;
				total_text_view.setText(Converter.toString(total));
			}
		});

		product.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							0, LayoutParams.WRAP_CONTENT, 5);
					params.setMargins(5, 0, 0, 0);
					view.setLayoutParams(params);
					((EditText) view).setEllipsize(null);
					((EditText) view).setSingleLine(false);
				} else {
					view.setLayoutParams(mLastParams);
					((EditText) view).setEllipsize(TextUtils.TruncateAt.END);
					((EditText) view).setSingleLine();
				}
			}
		});

		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinearLayout parent = (LinearLayout) v.getParent().getParent()
						.getParent();
				int addedIndex = parent.getChildCount();
				for (int index = 0; index < parent.getChildCount(); index++) {
					if (parent.getChildAt(index) == v.getParent().getParent()) {
						addedIndex = index + 1;
					}

					EntryEditProductView entryEdit = (EntryEditProductView) parent
							.getChildAt(index);
					if (entryEdit != null) {
						if ("".equals(entryEdit.product.getText().toString())) {
							Alert.getInstance().show(getContext(),
									"A product field is empty");
							entryEdit.product.requestFocus();
							return;
						}

						if ("".equals(entryEdit.price.getText().toString())) {
							Alert.getInstance().show(getContext(),
									"A price field is empty");
							entryEdit.price.requestFocus();
							return;
						}
					}
				}

				EntryEditProductView item = new EntryEditProductView(
						getContext(), total_text_view, -1);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

				parent.addView(item, addedIndex, params);

				item.product.requestFocus();
			}
		});

		// Add event to handle clicking remove button.
		removeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				LinearLayout list = (LinearLayout) v.getParent().getParent()
						.getParent();

				if (list.getChildCount() > 1) {
					EntryEditProductView item = (EntryEditProductView) v
							.getParent().getParent();
					if (item == null) {
						return;
					}

					item.removeItem();
					list.removeView(item);
				}
			}
		});
	}

	public void removeItem() {
		if (Id != -1) {
			SqlHelper.instance.delete("EntryDetail", "Id = " + Id);
		}
	}

	public String checkBeforeSave() {
		if ("".equals(getCost())) {
			return "A price is empty!";
		} else if ("".equals(getName())) {
			return "A product is empty!";
		}

		return null;
	}

	public void setFocus() {
		product.requestFocus();
	}

	public void setName(String name) {
		product.setText(name);
	}

	public void setCost(String cost) {
		price.setText(cost.replace(",", "").replace("[.]", ""));
	}

	public String getName() {
		return String.valueOf(product.getText());
	}

	public String getCost() {
		return String.valueOf(price.getText());
	}

	public long getMoney() {
		String value = getCost();
		if ("".equals(value)) {
			return 0;
		}

		return Converter.toLong(value);
	}
}
