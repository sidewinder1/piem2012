package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportPieCategoryLegendItemView extends LinearLayout {
	private LinearLayout colorContent;
	private TextView itemName;
	private TextView itemPercent;
	private TextView itemValue;

	public ReportPieCategoryLegendItemView(Context context, String color, String name , long value, long totalExpense) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_item, this, true);

		colorContent = (LinearLayout) findViewById(R.id.report_category_legend_color_item);
		itemName = (TextView) findViewById(R.id.report_category_legend_item_name);
		itemPercent = (TextView) findViewById(R.id.report_category_legend_item_percent);
		itemValue = (TextView) findViewById(R.id.report_category_legend_item_value);
		
		colorContent.setBackgroundColor(Color.parseColor(color));
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double)value / (double)totalExpense * 100)) + "%");
		itemValue.setText(Converter.toString(value));
	}
}
