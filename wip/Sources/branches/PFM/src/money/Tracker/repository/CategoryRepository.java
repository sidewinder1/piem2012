package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.Category;

public class CategoryRepository {
	private static CategoryRepository instance;
	public ArrayList<Category> categories = new ArrayList<Category>();
	public CategoryRepository() {
		Cursor categoryCursor = SqlHelper.instance.select("Category", "*", null);
		if (categoryCursor != null && categoryCursor.moveToFirst())
		{
			do{
			categories.add(new Category(
					categoryCursor.getInt(categoryCursor.getColumnIndex("Id")),
					categoryCursor.getString(categoryCursor.getColumnIndex("Name")),
					categoryCursor.getString(categoryCursor.getColumnIndex("User_color"))));
			}while(categoryCursor.moveToNext());
		}
	}
	
	public static CategoryRepository getInstance() {
		return instance == null ? new CategoryRepository() : instance;
	}
	

	
}
