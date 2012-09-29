package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;
import android.util.Log;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presnetation.model.Schedule;

public class ScheduleRepository implements IDataRepository {
	public ScheduleRepository instance;
	
	public ScheduleRepository() 
	{
		createTable();
	}

	protected void createTable() {
		SqlHelper.instance.createTable("Schedule",
				new StringBuilder("Id INTEGER PRIMARY KEY, Budget FLOAT,")
						.append("Start_date DATE, End_date DATE").toString());
	}
	
	public ArrayList<Object> getData()
	{
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor data = SqlHelper.instance.select("Schedule",
				"Budget,Start_date,End_date", null);

		if (data != null) {
			if (data.moveToFirst()) {
				do { 
					returnValues.add(new Schedule(data.getFloat(data.getColumnIndex("Budget")),
							Converter.toDate(data.getString(data.getColumnIndex("Start_date"))), 
							Converter.toDate(data.getString(data.getColumnIndex("End_date")))));
				} while (data.moveToNext());
			}
		}
		
		return returnValues;
	}
}
