package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportPieCategoryLegendSubItemView extends LinearLayout {

	public ReportPieCategoryLegendSubItemView(Context context, String name , long value, long totalExpense) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_sub_item, this, true);

		TextView itemName = (TextView) findViewById(R.id.report_category_legend_sub_item_name);
		TextView itemPercent = (TextView) findViewById(R.id.report_category_legend_sub_item_percent);
		TextView itemValue = (TextView) findViewById(R.id.report_category_legend_sub_item_value);
		
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double)value / (double)totalExpense * 100), "0.00") + "%");
		itemValue.setText(Converter.toString(value));
	}
}
