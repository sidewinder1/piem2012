package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;

public class ScheduleRepository implements IDataRepository {
	public ScheduleRepository instance;

	public ScheduleRepository() {
		createTable();
	}

	protected void createTable() {
		SqlHelper.instance.createTable("Schedule",
				new StringBuilder("Id INTEGER PRIMARY KEY, Budget FLOAT,")
						.append("Start_date DATE, End_date DATE").toString());
		SqlHelper.instance.createTable("ScheduleDetail",
				new StringBuilder("Id INTEGER PRIMARY KEY, Budget FLOAT,")
						.append("Category_Id INTEGER, Schedule_Id INTEGER")
						.toString());
	}

	public ArrayList<Object> getData(String param) {
		ArrayList<Object> returnValues = new ArrayList<Object>();

		Cursor scheduleData = SqlHelper.instance.select("Schedule",
				"*", "");

		if (scheduleData != null) {
			if (scheduleData.moveToFirst()) {
				do {
					ArrayList<DetailSchedule> details = new ArrayList<DetailSchedule>();
					Cursor detailData = SqlHelper.instance.select(
							"ScheduleDetail",
							"*",
							"Schedule_Id = "
									+ scheduleData.getInt(scheduleData
											.getColumnIndex("Id")));
					if (detailData != null) {
						if (detailData.moveToFirst()) {

							do {
								details.add(new DetailSchedule(
										detailData.getInt(detailData
												.getColumnIndex("Category_Id")),
										detailData.getDouble(detailData
												.getColumnIndex("Budget"))));
							} while (detailData.moveToNext());
						}
					}

					returnValues.add(new Schedule(scheduleData
							.getInt(scheduleData.getColumnIndex("Id")),
							scheduleData.getFloat(scheduleData
									.getColumnIndex("Budget")), Converter
									.toDate(scheduleData.getString(scheduleData
											.getColumnIndex("Start_date"))),
							Converter.toDate(scheduleData
									.getString(scheduleData
											.getColumnIndex("End_date"))),
							/*scheduleData.getInt(scheduleData
									.getColumnIndex("for_month")),*/ details));

				} while (scheduleData.moveToNext());
			}
		}

		return returnValues;
	}
}
