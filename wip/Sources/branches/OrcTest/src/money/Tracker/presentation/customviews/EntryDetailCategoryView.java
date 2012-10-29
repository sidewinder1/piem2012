package money.Tracker.presentation.customviews;

import java.util.ArrayList;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.EntryDetail;
import money.Tracker.repository.CategoryRepository;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryDetailCategoryView extends LinearLayout {
	private TextView category_name, category_count, category_total;
	private LinearLayout category_list;

	public EntryDetailCategoryView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public EntryDetailCategoryView(Context context, ArrayList<EntryDetail> data) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_detail_category_item, this, true);

		// Get control from .xml file.
		category_name = (TextView) findViewById(R.id.entry_detail_category_item_name);
		category_count = (TextView) findViewById(R.id.entry_detail_category_item_count);
		category_total = (TextView) findViewById(R.id.entry_detail_category_item_total);
		category_list = (LinearLayout) findViewById(R.id.entry_detail_category_list);

		// Set value to category.
		category_name.setText(CategoryRepository.getInstance().getName(
				(data.get(0).getCategory_id())));

		double total = 0;
		int count = 0;
		category_list.removeAllViews();
		for (EntryDetail entryDetail : data) {
			EntryDetailProductView item = new EntryDetailProductView(context,
					entryDetail);

			total += entryDetail.getMoney();
			count++;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			category_list.addView(item, params);
		}

		category_count.setText(new StringBuilder(" (").append(count)
				.append(")").toString());
		this.category_total.setText(Converter.toString(total));
	}
}