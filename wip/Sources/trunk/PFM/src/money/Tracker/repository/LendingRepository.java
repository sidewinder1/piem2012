package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presnetation.model.Schedule;

public class LendingRepository implements IDataRepository {
	
	public LendingRepository instance;
	
	public LendingRepository() 
	{
		createTable();
	}

	private void createTable() {
		SqlHelper.instance.createTable("Lending",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGERL,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");
	}
	
	public ArrayList<Object> getData() {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor data = SqlHelper.instance.select("Borrowing",
				"Budget,Start_date,End_date", null);

		if (data != null) {
			if (data.moveToFirst()) {
				do {
					returnValues.add(new Schedule(data.getFloat(data
							.getColumnIndex("Budget")), Converter.toDate(data
							.getString(data.getColumnIndex("Start_date"))),
							Converter.toDate(data.getString(data
									.getColumnIndex("End_date")))));
				} while (data.moveToNext());
			}
		}

		return returnValues;
	}
}
