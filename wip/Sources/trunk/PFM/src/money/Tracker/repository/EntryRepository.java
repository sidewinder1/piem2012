package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;

public class EntryRepository implements IDataRepository {
	private static EntryRepository instance;

	public ArrayList<Entry> entries;

	public EntryRepository() {
		updateData();
	}

	public ArrayList<Entry> updateData() 
	{
		return updateData("");
	}
	
	public ArrayList<Entry> updateData(String condition) {
		Cursor entryCursor = SqlHelper.instance.select("Entry", "*", condition);
		if (entryCursor != null && entryCursor.moveToFirst()) {
			entries = new ArrayList<Entry>();
			do {
				int id = entryCursor.getInt(entryCursor.getColumnIndex("Id"));

				entries.add(new Entry(id, entryCursor.getInt(entryCursor
						.getColumnIndex("Type")), Converter.toDate(entryCursor
						.getString(entryCursor.getColumnIndex("Date"))),
						EntryDetailRepository.getInstance().updateData(
								new StringBuilder("Entry_Id = " + id)
										.toString())));
			} while (entryCursor.moveToNext());
		}

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
		;
		Cursor entryCursor = SqlHelper.instance.select("Entry", "*", null);
		if (entryCursor != null && entryCursor.moveToFirst()) {

			do {
				int id = entryCursor.getInt(entryCursor.getColumnIndex("Id"));

				iEntries.add(new Entry(id, entryCursor.getInt(entryCursor
						.getColumnIndex("Type")), Converter.toDate(entryCursor
						.getString(entryCursor.getColumnIndex("Date"))),
						EntryDetailRepository.getInstance().updateData(
								new StringBuilder("Entry_Id = " + id)
										.toString())));
			} while (entryCursor.moveToNext());
		}

		return iEntries;
	}
}
