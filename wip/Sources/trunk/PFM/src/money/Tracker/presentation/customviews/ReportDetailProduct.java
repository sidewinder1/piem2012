package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportDetailProduct extends LinearLayout{

	public ReportDetailProduct(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ReportDetailProduct(Context context, String name, double value) {
		// TODO Auto-generated constructor stub
		super(context);
		
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_detail_product, this, true);

		// Get control from .xml file.
		TextView product_name = (TextView) findViewById(R.id.report_detail_product_item_name);
		TextView product_total = (TextView) findViewById(R.id.report_detail_product_item_total);

		// Set value to category.
		product_name.setText(name);
		product_total.setText(Converter.toString(value));

	}
	
}
