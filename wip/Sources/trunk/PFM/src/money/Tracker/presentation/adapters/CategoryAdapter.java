package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.presentation.customviews.CategoryCustomView;
import money.Tracker.presentation.model.Category;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CategoryAdapter extends ArrayAdapter<Category> {
	public ArrayList<Category> categories;

	public CategoryAdapter(Context context, int textViewResourceId,
			ArrayList<Category> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		categories = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView);
	}

	private View initView(int position, View convertView) {
		CategoryCustomView categoryView = (CategoryCustomView) convertView;
		if (categoryView == null) {
			categoryView = new CategoryCustomView(getContext());
		}

		Category category = categories.get(position);

		if (category != null) {
			categoryView.item_content.setText(category.getName());
			categoryView.item_id.setText(String.valueOf(category.getId()));
			categoryView.setBackgroundColor(Color.parseColor(category
					.getUser_color()));
		}

		return categoryView;
	}
}
