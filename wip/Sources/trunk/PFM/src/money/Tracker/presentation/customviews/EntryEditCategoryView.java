package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class EntryEditCategoryView extends LinearLayout {
	private Spinner category;
	private TextView total_money;
	private Button addBtn, removeBtn;
	private LinearLayout category_list;

	public EntryEditCategoryView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_edit_category_item, this, true);

		category = (Spinner) findViewById(R.id.entry_item_category);
		total_money = (TextView) findViewById(R.id.entry_item_price);
		addBtn = (Button) findViewById(R.id.entry_item_add);
		removeBtn = (Button) findViewById(R.id.entry_item_remove);
		category_list = (LinearLayout) findViewById(R.id.entry_edit_category_list);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		EntryEditProductView item = new EntryEditProductView(context);
		category_list.addView(item, params);
		
		addBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			LinearLayout parent = (LinearLayout) v.getParent().getParent().getParent().getParent();
			EntryEditCategoryView item = new EntryEditCategoryView(getContext());
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			 
			parent.addView(item, params);
			}
		});
	}
}
