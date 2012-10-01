package money.Tracker.presentation.customviews;

import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CategoryCustomView extends LinearLayout {
	public TextView item_content, item_id;

	public CategoryCustomView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.dropdown_list_item, this, true);

		item_content = (TextView) findViewById(R.id.item_content);
		item_id = (TextView) findViewById(R.id.item_id);
	}

}
