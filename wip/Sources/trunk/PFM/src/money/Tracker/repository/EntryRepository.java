package money.Tracker.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import android.database.Cursor;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;

public class EntryRepository implements IDataRepository {
	private static EntryRepository instance;

	public ArrayList<Entry> entries;
	public LinkedHashMap<String, ArrayList<Entry>> orderedEntries;

	public EntryRepository() {
		updateData();
	}

	public ArrayList<Entry> updateData() {
		return updateData("");
	}

	public ArrayList<Entry> updateData(String condition) {
		if (!"".equals(condition)) {
			condition = new StringBuilder(" WHERE ").append(condition)
					.toString();
		}

		Cursor entryCursor = SqlHelper.instance.query(new StringBuilder(
				"SELECT * FROM Entry ").append(condition)
				.append(" ORDER BY Date DESC").toString());
		if (entryCursor != null && entryCursor.moveToFirst()) {
			entries = new ArrayList<Entry>();
			orderedEntries = new LinkedHashMap<String, ArrayList<Entry>>();

			do {
				long id = entryCursor.getLong(entryCursor.getColumnIndex("Id"));
				String keyMonth = Converter.toString(Converter
						.toDate(entryCursor.getString(entryCursor
								.getColumnIndex("Date"))), "MM/yyyy");

				if (!orderedEntries.containsKey(keyMonth)) {
					orderedEntries.put(keyMonth, new ArrayList<Entry>());
				}
				EntryDetailRepository.getInstance().updateData(
						new StringBuilder("Entry_Id = " + id).toString(),
						"Entry_Id");
				Entry entry = new Entry(id, entryCursor.getInt(entryCursor
						.getColumnIndex("Type")), Converter.toDate(entryCursor
						.getString(entryCursor.getColumnIndex("Date"))),
						EntryDetailRepository.getInstance().entryDetails);
				orderedEntries.get(keyMonth).add(entry);
				entries.add(entry);
			} while (entryCursor.moveToNext());
		}

		sort();
		return entries;
	}

	public static EntryRepository getInstance() {
		if (instance == null) {
			instance = new EntryRepository();
		}

		return instance;
	}

	public ArrayList<IModelBase> getData(String param) {
		ArrayList<IModelBase> iEntries = new ArrayList<IModelBase>();
		if (!"".equals(param)) {
			param = new StringBuilder(" WHERE ").append(param).toString();
		}

		Cursor entryCursor = SqlHelper.instance.query(new StringBuilder(
				"SELECT * FROM Entry ").append(param)
				.append(" ORDER BY Date DESC,Id ASC").toString());
		if (entryCursor != null && entryCursor.moveToFirst()) {
			orderedEntries = new LinkedHashMap<String, ArrayList<Entry>>();

			do {
				long id = entryCursor.getLong(entryCursor.getColumnIndex("Id"));
				String keyMonth = Converter.toString(Converter
						.toDate(entryCursor.getString(entryCursor
								.getColumnIndex("Date"))), "MM/yyyy");

				if (!orderedEntries.containsKey(keyMonth)) {
					orderedEntries.put(keyMonth, new ArrayList<Entry>());
				}
				EntryDetailRepository.getInstance().updateData(
						new StringBuilder("Entry_Id = " + id).toString(),
						"Entry_Id");
				Entry entry = new Entry(id, entryCursor.getInt(entryCursor
						.getColumnIndex("Type")), Converter.toDate(entryCursor
						.getString(entryCursor.getColumnIndex("Date"))),
						EntryDetailRepository.getInstance().entryDetails);
				orderedEntries.get(keyMonth).add(entry);
				iEntries.add(entry);
			} while (entryCursor.moveToNext());
		}

		sort();
		return iEntries;
	}

	public void sort() {
		if (orderedEntries == null) {
			return;
		}

		int i, j;
		for (ArrayList<Entry> entryList : orderedEntries.values()) {
			int length = entryList.size();
			Entry t = new Entry();
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
