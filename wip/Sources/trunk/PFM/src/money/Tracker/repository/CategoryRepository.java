package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.Category;

public class CategoryRepository {
	private static CategoryRepository instance;
	public ArrayList<Category> categories = new ArrayList<Category>();

	public CategoryRepository() {
		updateData();
	}

	public void updateData() {
		Cursor categoryCursor = SqlHelper.instance
				.select("Category", "*", null);
		if (categoryCursor != null && categoryCursor.moveToFirst()) {
			categories = new ArrayList<Category>();

			do {
				categories.add(new Category(categoryCursor
						.getInt(categoryCursor.getColumnIndex("Id")),
						categoryCursor.getString(categoryCursor
								.getColumnIndex("Name")), categoryCursor
								.getString(categoryCursor
										.getColumnIndex("User_Color"))));
			} while (categoryCursor.moveToNext());
		}
	}

	public int getId(int index) {
		return categories.get(index).getId();
	}

	public int getId(String name) {
		for (Category category : categories) {
			if (category.getName() == name) {
				return category.getId();
			}
		}

		return 1;
	}

	public String getName(int id) {
		for (Category category : categories) {
			if (category.getId() == id) {
				return category.getName();
			}
		}

		return "Birthday";
	}

	public String getColor(int id) {
		for (Category category : categories) {
			if (category.getId() == id) {
				return category.getUser_color();
			}
		}

		return "#FF000000";
	}

	public Category getCategory(String color) {
		for (Category category : categories) {
			if (category.getUser_color().equals(color)) {
				return new Category(category.getId(), category.getName(),
						category.getUser_color());
			}
		}
		
		return null;
	}

	public int getIndex(int id) {
		for (int index = 0; index < categories.size(); index++) {
			if (categories.get(index).getId() == id) {
				return index;
			}
		}

		return 0;
	}

	public static CategoryRepository getInstance() {
		if (instance == null) {
			instance = new CategoryRepository();
		}
		return instance;
	}
}
