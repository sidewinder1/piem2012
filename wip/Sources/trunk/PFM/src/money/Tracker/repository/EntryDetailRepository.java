package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.EntryDetail;

public class EntryDetailRepository {
	public ArrayList<EntryDetail> entries;
	private static EntryDetailRepository instance;

	public EntryDetailRepository() {
	}

	public ArrayList<EntryDetail> updateData() {
		Cursor entryCursor = SqlHelper.instance
				.select("EntryDetail", "*", null);
		if (entryCursor != null && entryCursor.moveToFirst()) {
			entries = new ArrayList<EntryDetail>();
			do {
				entries.add(new EntryDetail(
						entryCursor.getInt(entryCursor.getColumnIndex("Id")),
						entryCursor.getInt(entryCursor.getColumnIndex("Category_Id")),
						entryCursor.getInt(entryCursor.getColumnIndex("Type")),
						entryCursor.getString(entryCursor.getColumnIndex("Name")),
						Converter.toDate(entryCursor.getString(entryCursor.getColumnIndex("Date"))),
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
}
