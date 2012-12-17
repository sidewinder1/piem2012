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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportPieCategoryLegendItemView extends LinearLayout {
	private boolean isCollapsed = true;
	private LinearLayout subItemLinearLayout;
	private long entryID;
	private long categoryID;
	private long totalExpense;

	public ReportPieCategoryLegendItemView(Context context, String color,
			String name, long value, long totalExpense, long entryID,
			long categoryID) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_item,
				this, true);

		this.entryID = entryID;
		this.categoryID = categoryID;
		this.totalExpense = totalExpense;

		LinearLayout colorContent = (LinearLayout) findViewById(R.id.report_category_legend_color_item);
		TextView itemName = (TextView) findViewById(R.id.report_category_legend_item_name);
		TextView itemPercent = (TextView) findViewById(R.id.report_category_legend_item_percent);
		TextView itemValue = (TextView) findViewById(R.id.report_category_legend_item_value);
		final ImageView collapsedButton = (ImageView) findViewById(R.id.report_collapsed_button);
		subItemLinearLayout = (LinearLayout) findViewById(R.id.report_category_pie_chart_ledgend_sub_item);

		bindData();
		colorContent.setBackgroundColor(Color.parseColor(color));
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double) value
				/ (double) totalExpense * 100))
				+ "%");
		itemValue.setText(Converter.toString(value));

		setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				isCollapsed = !isCollapsed;
				collapsedButton
						.setImageResource(isCollapsed ? R.drawable.combobox_icon
								: R.drawable.combobox_icon_expanded);
				subItemLinearLayout.setVisibility(isCollapsed ? View.GONE
						: View.VISIBLE);
			}
		});
	}

	private void bindData() {
		Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail",
				"Name, sum(Money) as Total", "Category_Id = " + categoryID
						+ " and Entry_ID = " + entryID + " group by Name");
		if (entryDetailCursor != null && entryDetailCursor.moveToFirst()) {
			subItemLinearLayout.removeAllViews();
			do {
				String name = entryDetailCursor.getString(entryDetailCursor
						.getColumnIndex("Name"));
				long value = entryDetailCursor.getLong(entryDetailCursor
						.getColumnIndex("Total"));
				Log.d("abc", name + value);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				subItemLinearLayout.addView(
						new ReportPieCategoryLegendSubItemView(this
								.getContext(), name, value, totalExpense),
						params);
			} while (entryDetailCursor.moveToNext());
		}
	}
}
