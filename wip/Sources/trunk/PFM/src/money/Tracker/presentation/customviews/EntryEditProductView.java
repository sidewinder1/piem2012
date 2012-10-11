package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class EntryEditProductView extends LinearLayout {
	private EditText product, price;
	private Button addBtn, removeBtn;

	public EntryEditProductView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_product_item, this, true);
		
		product = (EditText) findViewById(R.id.entry_edit_product_name);
		price = (EditText) findViewById(R.id.entry_edit_product_price);
		addBtn = (Button) findViewById(R.id.entry_edit_product_item_add);
		removeBtn = (Button) findViewById(R.id.entry_edit_product_remove);

		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LinearLayout parent = (LinearLayout) v.getParent().getParent().getParent();
				EntryEditProductView item = new EntryEditProductView(
						getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				 
				parent.addView(item, params);
			}
		});
	}

	public void setName(String name)
	{
		product.setText(name);
	}
	
	public void setCost(String cost)
	{
		price.setText(cost);
	}
	
	public String getName()
	{
		return String.valueOf(product.getText());
	}
	
	public String getCost()
	{
		return String.valueOf(price.getText());
	}
}
