package money.Tracker.presentation.customviews;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportPieCategoryLegendSubItemView extends LinearLayout {

	public ReportPieCategoryLegendSubItemView(Context context, String name , long value, long totalExpense) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_sub_item, this, true);

		TextView itemName = (TextView) findViewById(R.id.report_category_legend_sub_item_name);
		TextView itemPercent = (TextView) findViewById(R.id.report_category_legend_sub_item_percent);
		TextView itemValue = (TextView) findViewById(R.id.report_category_legend_sub_item_value);
		
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double)value / (double)totalExpense * 100)) + "%");
		itemValue.setText(Converter.toString(value));
	}
}
