package money.Tracker.presentation.customviews;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.model.EntryDetail;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportDetailCategory extends LinearLayout{

	public ReportDetailCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public ReportDetailCategory(Context context, String name, double value, int entryID, int categoryID)
	{
		super(context);
		
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_detail_category, this, true);

		// Get control from .xml file.
		TextView product_name = (TextView) findViewById(R.id.report_detail_category_item_name);
		TextView product_total = (TextView) findViewById(R.id.report_detail_category_item_total);
		LinearLayout category_list = (LinearLayout) findViewById(R.id.entry_detail_category_list);
		
		// Set value to category.
		product_name.setText(name);
		product_total.setText(Converter.toString(value));
		
		//
		category_list.removeAllViews();
		Cursor entryDetailCursor2 = SqlHelper.instance.select("EntryDetail", "Name, sum(Money) as Total", "Entry_Id = " + entryID + " and Category_Id = " + categoryID + " group by Name");
		if (entryDetailCursor2 != null)
		{
			if(entryDetailCursor2.moveToFirst())
			{
				do
				{
					String nameDetail = entryDetailCursor2.getString(entryDetailCursor2.getColumnIndex("Name"));
					double valueDetail = entryDetailCursor2.getDouble(entryDetailCursor2.getColumnIndex("Total"));
					
					ReportDetailProduct item = new ReportDetailProduct(context, nameDetail, valueDetail);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					category_list.addView(item, params);
				}while(entryDetailCursor2.moveToNext());
			}
		}
	}
}
