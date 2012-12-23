package money.Tracker.repository;

import java.util.ArrayList;
import java.util.Locale;

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
						.getLong(categoryCursor.getColumnIndex("Id")),
						categoryCursor.getString(categoryCursor
								.getColumnIndex("Name")), categoryCursor
								.getString(categoryCursor
										.getColumnIndex("User_Color"))));
			} while (categoryCursor.moveToNext());
			
			categoryCursor.close();
			sort();
		}
	}

	public long getId(int index) {
		return categories.get(index).getId();
	}

	public long getId(String name) {
		for (Category category : categories) {
			if (category.getName().toLowerCase(Locale.US).equals(name.toLowerCase(Locale.US))) {
				return category.getId();
			}
		}

		return 1;
	}

	public boolean isExisted(String nameOfCategory) {
		for (Category category : categories) {
			if (nameOfCategory.toLowerCase(Locale.US).equals(
					category.getName().toLowerCase(Locale.US))) {
				return true;
			}
		}

		return false;
	}

	public String getName(long id) {
		for (Category category : categories) {
			if (category.getId() == id) {
				return category.getName();
			}
		}

		return "Birthday";
	}

	public String getColor(long id) {
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

	public int getIndex(long id) {
		for (int index = 0; index < categories.size(); index++) {
			if (categories.get(index).getId() == id) {
				return index;
			}
		}

		return 0;
	}

	public int getIndex(String name) {
		for (int index = 0; index < categories.size(); index++) {
			if (name.toLowerCase(Locale.US).equals(
					categories.get(index).getName().toLowerCase(Locale.US))) {
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

	public void sort() {
		int i, j;

		int length = categories.size();
		Category t = new Category();
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				if (categories.get(j - 1).compareTo(categories.get(j)) > 0) {
					t = categories.get(j - 1);
					categories.set(j - 1, categories.get(j));
					categories.set(j, t);
				}
			}
		}

	}
}
