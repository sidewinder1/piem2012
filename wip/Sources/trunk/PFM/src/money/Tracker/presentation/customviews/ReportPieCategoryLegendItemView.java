package money.Tracker.presentation.customviews;

import java.util.Date;
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
	private long categoryID;
	private long totalExpense;
	private Date startDate;
	private Date endDate;

	public ReportPieCategoryLegendItemView(Context context, String color, String name, long value, long totalExpense, long categoryID, Date startDate, Date endDate) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_item, this, true);

		this.categoryID = categoryID;
		this.totalExpense = totalExpense;
		this.startDate = startDate;
		this.endDate = endDate;

		LinearLayout colorContent = (LinearLayout) findViewById(R.id.report_category_legend_color_item);
		TextView itemName = (TextView) findViewById(R.id.report_category_legend_item_name);
		TextView itemPercent = (TextView) findViewById(R.id.report_category_legend_item_percent);
		TextView itemValue = (TextView) findViewById(R.id.report_category_legend_item_value);
		final ImageView collapsedButton = (ImageView) findViewById(R.id.report_collapsed_button);
		subItemLinearLayout = (LinearLayout) findViewById(R.id.report_category_pie_chart_ledgend_sub_item);

		bindData();
		colorContent.setBackgroundColor(Color.parseColor(color));
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double) value / (double) totalExpense * 100), "0.00") + "%");
		itemValue.setText(Converter.toString(value));

		setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				isCollapsed = !isCollapsed;
				collapsedButton.setImageResource(isCollapsed ? R.drawable.combobox_icon: R.drawable.combobox_icon_expanded);
				subItemLinearLayout.setVisibility(isCollapsed ? View.GONE : View.VISIBLE);
			}
		});
	}

	private void bindData() {
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		if (entryExpenseCursor != null) {
			if (entryExpenseCursor.moveToFirst()) {
				do {
					long id = entryExpenseCursor.getLong(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					
					Log.d("Check spent", String.valueOf(entryDate) + " - " + String.valueOf(startDate) + " - " + String.valueOf(endDate));
					if (entryDate.compareTo(startDate) > 0 && entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0) {
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Name, sum(Money) as Total", "Entry_Id=" + id + " and Category_Id=" + categoryID + " group by Name");
						Log.d("Check spent", "Check 6");
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {
									String name = entryDetailCursor.getString(entryDetailCursor.getColumnIndex("Name"));
									long value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));
									Log.d("abc", name + value);
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
									subItemLinearLayout.addView(new ReportPieCategoryLegendSubItemView(this.getContext(), name, value, totalExpense), params);
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				} while (entryExpenseCursor.moveToNext());
			}
		}
	}
}
