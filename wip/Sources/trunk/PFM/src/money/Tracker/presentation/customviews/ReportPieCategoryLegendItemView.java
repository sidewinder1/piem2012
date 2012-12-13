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

public class ReportPieCategoryLegendItemView extends LinearLayout {
	private boolean changedState = false;
	private LinearLayout subItemLinearLayout; 
	private long entryID;
	private long categoryID;
	private long totalExpense;
	
	public ReportPieCategoryLegendItemView(Context context, String color, String name , long value, long totalExpense, long entryID, long categoryID) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_category_chart_legend_item, this, true);
		
		this.entryID = entryID;
		this.categoryID = categoryID;
		this.totalExpense = totalExpense;

		LinearLayout colorContent = (LinearLayout) findViewById(R.id.report_category_legend_color_item);
		TextView itemName = (TextView) findViewById(R.id.report_category_legend_item_name);
		TextView itemPercent = (TextView) findViewById(R.id.report_category_legend_item_percent);
		TextView itemValue = (TextView) findViewById(R.id.report_category_legend_item_value);
		Button collapsedButton = (Button) findViewById(R.id.report_collapsed_button);
		subItemLinearLayout = (LinearLayout) findViewById(R.id.report_category_pie_chart_ledgend_sub_item);
		
		colorContent.setBackgroundColor(Color.parseColor(color));
		itemName.setText(name);
		itemPercent.setText(Converter.toString(((double)value / (double)totalExpense * 100)) + "%");
		itemValue.setText(Converter.toString(value));
		
		collapsedButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bindData();
			}
		});
	}
	
	private void bindData()
	{
		if (changedState == false)
		{
			Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Name, sum(Money) as Total", "Category_Id = " + categoryID + " and Entry_ID = " + entryID + " group by Name" );
			if (entryDetailCursor != null)
			{
				if (entryDetailCursor.moveToFirst())
				{
				do
				{
					String name = entryDetailCursor.getString(entryDetailCursor.getColumnIndex("Name"));
					long value = entryDetailCursor.getLong(entryDetailCursor.getColumnIndex("Total"));
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					subItemLinearLayout.addView(new ReportPieCategoryLegendSubItemView(this.getContext(), name, value, totalExpense), params);
				}while(entryDetailCursor.moveToNext());
				}
			}
			
			changedState = true;
		} else
		{
			subItemLinearLayout.removeAllViews();
			changedState = false;
		}
	}
}
