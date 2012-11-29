package money.Tracker.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.EntryDetail;

public class EntryDetailRepository {
	private static EntryDetailRepository instance;
	public ArrayList<EntryDetail> entryDetails;
	public LinkedHashMap<String, ArrayList<EntryDetail>> entries;

	public EntryDetailRepository() {
		updateData();
	}

	public LinkedHashMap<String, ArrayList<EntryDetail>> updateData() {
		return updateData("", "Category_Id");
	}

	public LinkedHashMap<String, ArrayList<EntryDetail>> updateData(String condition,
			String columnKey) {
		entries = new LinkedHashMap<String, ArrayList<EntryDetail>>();
		entryDetails = new ArrayList<EntryDetail>();

		Cursor entryCursor = SqlHelper.instance.select("EntryDetail", "*",
				condition);
		if (entryCursor != null && entryCursor.moveToFirst()) {
			do {
				String categoryKey = entryCursor.getString(entryCursor
						.getColumnIndex(columnKey));
				if (!entries.containsKey(categoryKey)) {
					entries.put(categoryKey, new ArrayList<EntryDetail>());
				}
				EntryDetail entryDetail = new EntryDetail(
						entryCursor.getLong(entryCursor.getColumnIndex("Id")),
						entryCursor.getLong(entryCursor
								.getColumnIndex("Entry_Id")),
						entryCursor.getLong(entryCursor
								.getColumnIndex("Category_Id")),
						entryCursor.getString(entryCursor
								.getColumnIndex("Name")),
						entryCursor.getLong(entryCursor
								.getColumnIndex("Money")));

				entries.get(categoryKey).add(entryDetail);
				entryDetails.add(entryDetail);
			} while (entryCursor.moveToNext());
		}

		sort();
		return entries;
	}

	public static EntryDetailRepository getInstance() {
		if (instance == null) {
			instance = new EntryDetailRepository();
		}
		return instance;
	}

	public void sort() {
		if (entries == null) {
			return;
		}

		int i, j;
		for (ArrayList<EntryDetail> entryList : entries.values()) {
			int length = entryList.size();
			EntryDetail t = new EntryDetail();
			for (i = 0; i < length; i++) {
				for (j = 1; j < (length - i); j++) {
					if (entryList.get(j - 1).compareTo(entryList.get(j)) > 0) {
						t = entryList.get(j - 1);
						entryList.set(j - 1, entryList.get(j));
						entryList.set(j, t);
					}
				}
			}
		}
	}
}
