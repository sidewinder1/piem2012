package money.Tracker.presentation.customviews;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.database.Cursor;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportDetailCategory extends LinearLayout {

	private LinearLayout category_list;
	private boolean changedState = false;
	private String checkName = "";
	private long entryID = 0;
	private long categoryID = 0;
	
	public ReportDetailCategory(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ReportDetailCategory(Context context, String name, long value, long entryID, long categoryID) {
		super(context);

		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.activity_report_view_detail_category, this, true);

		// Get control from .xml file.
		TextView product_name = (TextView) findViewById(R.id.report_detail_category_item_name);
		TextView product_total = (TextView) findViewById(R.id.report_detail_category_item_total);
		category_list = (LinearLayout) findViewById(R.id.report_detail_category_list);
		Button collapsedButton = (Button) findViewById(R.id.report_collapsed_button);
		category_list.removeAllViews();
		
		this.entryID = entryID;
		this.categoryID = categoryID;
		
		// Set value to category.
		product_name.setText(name);
		product_total.setText(Converter.toString(value));
		
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
					category_list.addView(new ReportDetailProduct(this.getContext(), name, value), params);
				}while(entryDetailCursor.moveToNext());
				}
			}
			
			changedState = true;
		} else
		{
			category_list.removeAllViews();
			changedState = false;
		}
	}
}
