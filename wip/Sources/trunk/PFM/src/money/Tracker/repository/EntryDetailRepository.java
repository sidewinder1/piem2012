package money.Tracker.repository;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.EntryDetail;

public class EntryDetailRepository {
	private static EntryDetailRepository instance;

	public HashMap<String, ArrayList<EntryDetail>> entries;

	public EntryDetailRepository() {
		updateData();
	}

	public HashMap<String, ArrayList<EntryDetail>> updateData() {
		return updateData("");
	}
	
	public HashMap<String, ArrayList<EntryDetail>> updateData(String condition) {
		Cursor entryCursor = SqlHelper.instance
				.select("EntryDetail", "*", condition);
		if (entryCursor != null && entryCursor.moveToFirst()) {
			entries = new HashMap<String, ArrayList<EntryDetail>>();
			
			do {
				int category_id = entryCursor.getInt(entryCursor.getColumnIndex("Category_Id"));
				String categoryKey = String.valueOf(category_id);
				if (entries.containsKey(categoryKey))
				{
					entries.put(categoryKey, new ArrayList<EntryDetail>());
				}
				
				entries.get(categoryKey).add(new EntryDetail(
						entryCursor.getInt(entryCursor.getColumnIndex("Id")),
						entryCursor.getInt(entryCursor.getColumnIndex("Entry_Id")),
						category_id,
						entryCursor.getString(entryCursor.getColumnIndex("Name")),
						entryCursor.getDouble(entryCursor.getColumnIndex("Money"))));
			} while (entryCursor.moveToNext());
		}
		
		return entries;
	}

	public static EntryDetailRepository getInstance() {
		if (instance == null) {
			instance = new EntryDetailRepository();
		}
		return instance;
	}
	
	public void sort()
	{
		int i, j;
		for (ArrayList<EntryDetail> entryList : entries.values())
		{
			int length = entryList.size();
			EntryDetail t = new EntryDetail();
			for (i = 0; i < length; i++) {
				for (j = 1; j < (length - i); j++) {
					if (entryList.get(j - 1).compareTo(entryList.get(j)) < 0) {
						t = entryList.get(j - 1);
						entryList.set(j - 1, entryList.get(j));
						entryList.set(j, t);
					}
				}
			}
		}
	}
}
