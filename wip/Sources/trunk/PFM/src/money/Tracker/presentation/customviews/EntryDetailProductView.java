package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.EntryDetail;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryDetailProductView extends LinearLayout {
	private TextView product_name, product_total;

	public EntryDetailProductView(Context context) {
		super(context);
	}

	public EntryDetailProductView(Context context, EntryDetail data) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.entry_detail_product_item, this, true);

		// Get control from .xml file.
		product_name = (TextView) findViewById(R.id.entry_detail_product_item_name);
		product_total = (TextView) findViewById(R.id.entry_detail_product_item_total);

		// Set value to category.
		product_name.setText(data.getName());
		this.product_total.setText(Converter.toString(data.getMoney()));
	}
}