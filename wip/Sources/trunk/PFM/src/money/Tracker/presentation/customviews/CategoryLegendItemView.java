package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoryLegendItemView extends LinearLayout {
	private LinearLayout colorContent;
	private TextView itemName;

	public CategoryLegendItemView(Context context) {
		super(context);
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.category_chart_legend_item, this, true);

		colorContent = (LinearLayout) findViewById(R.id.category_legend_color_item);
		itemName = (TextView) findViewById(R.id.category_legend_item_name);
	}

	public void setColor(String color) {
		colorContent.setBackgroundColor(Color.parseColor(color));
	}

	public void setName(String name) {
		itemName.setText(name);
	}
}
