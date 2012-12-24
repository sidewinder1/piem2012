package money.Tracker.presentation.customviews;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportBarChartViewDetailItemView extends LinearLayout {

	public ReportBarChartViewDetailItemView(Context context, String value, String dateString) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_bar_chart_view_detail_item, this, true);
		
		TextView itemPercent = (TextView) findViewById(R.id.report_bar_chart_view_detail_item_value);
		TextView itemValue = (TextView) findViewById(R.id.report_bar_chart_view_detail_item_date);
		
		itemPercent.setText(value);
		itemValue.setText(dateString);
	}
}