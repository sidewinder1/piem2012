package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.EntryDetail;

public class EntryDetailRepository {
	public ArrayList<EntryDetail> entries;
	private static EntryDetailRepository instance;

	public EntryDetailRepository() {
		updateData();
	}

	public ArrayList<EntryDetail> updateData() {
		return updateData("");
	}
	
	public ArrayList<EntryDetail> updateData(String condition) {
		Cursor entryCursor = SqlHelper.instance
				.select("EntryDetail", "*", condition);
		if (entryCursor != null && entryCursor.moveToFirst()) {
			entries = new ArrayList<EntryDetail>();
			do {
				entries.add(new EntryDetail(
						entryCursor.getInt(entryCursor.getColumnIndex("Id")),
						entryCursor.getInt(entryCursor.getColumnIndex("Category_Id")),
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
		int length = entries.size();
		EntryDetail t = new EntryDetail();
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				if (entries.get(j - 1).compareTo(entries.get(j)) < 0) {
					t = entries.get(j - 1);
					entries.set(j - 1, entries.get(j));
					entries.set(j, t);
				}
			}
		}
	}
}
