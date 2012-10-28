package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoryCustomView extends LinearLayout {
	private TextView mItemContent, mItemId;
	private LinearLayout mColor;

	public CategoryCustomView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.dropdown_list_item, this, true);

		mItemContent = (TextView) findViewById(R.id.item_content);
		mItemId = (TextView) findViewById(R.id.item_id);
		mColor = (LinearLayout) findViewById(R.id.dropdown_item_color);
	}

	public void setBackgroundColor(String color) {
		mColor.setBackgroundColor(Color.parseColor(color));
	}
	
	public void setName(String name)
	{
		mItemContent.setText(name);
	}
	
	public void setId(String id)
	{
		mItemId.setText(id);
	}
	
	public void setBlackColor()
	{
		mItemContent.setTextColor(Color.BLACK);
	}
}
