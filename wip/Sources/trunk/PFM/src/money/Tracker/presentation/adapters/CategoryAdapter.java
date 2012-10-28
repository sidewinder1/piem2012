package money.Tracker.presentation.adapters;

import java.util.ArrayList;

import money.Tracker.presentation.customviews.CategoryCustomView;
import money.Tracker.presentation.model.Category;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class CategoryAdapter extends ArrayAdapter<Category> {
	public ArrayList<Category> mCategories;

	public CategoryAdapter(Context context, int textViewResourceId,
			ArrayList<Category> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		mCategories = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView, false);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView, true);
	}

	private View initView(int position, View convertView, boolean isDropDownView) {
		CategoryCustomView categoryView = (CategoryCustomView) convertView;
		if (categoryView == null) {
			categoryView = new CategoryCustomView(getContext());
		}

		Category category = mCategories.get(position);

		if (category != null) {
			categoryView.setName(category.getName());
			categoryView.setId(String.valueOf(category.getId()));
			categoryView.setBackgroundColor(category
					.getUser_color());
			if (isDropDownView)
			{
				categoryView.setBlackColor();
			}
		}

		return categoryView;
	}
}
