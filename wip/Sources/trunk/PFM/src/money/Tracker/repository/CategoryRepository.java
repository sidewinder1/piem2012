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
					categoryCursor.getString(categoryCursor.getColumnIndex("User_Color"))));
			}while(categoryCursor.moveToNext());
		}
	}
	
	public int getId(String name)
	{
		for (Category category : categories)
		{
			if (category.getName() == name)
			{
				return category.getId();
			}
		}
		
		return 1;
	}
	
	public String getName(int id)
	{
		for (Category category : categories)
		{
			if (category.getId() == id)
			{
				return category.getName();
			}
		}
		
		return "Birthday";
	}
	
	public String getColor(int id)
	{
		for (Category category : categories)
		{
			if (category.getId() == id)
			{
				return category.getUser_color();
			}
		}
		
		return "#FFFFFFFF";
	}
	
	public static CategoryRepository getInstance() {
		return instance == null ? new CategoryRepository() : instance;
	}
}
