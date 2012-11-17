package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;

public class ScheduleRepository implements IDataRepository {
	public static ScheduleRepository instance;

	public ScheduleRepository() {
	}

	public static ScheduleRepository getInstance() {
		if (instance == null) {
			instance = new ScheduleRepository();
		}
		return instance;
	}

	public ArrayList<IModelBase> getData(String param) {
		ArrayList<IModelBase> returnValues = new ArrayList<IModelBase>();

		Cursor scheduleData = SqlHelper.instance.select("Schedule", "*", param);

		if (scheduleData != null) {
			if (scheduleData.moveToFirst()) {
				do {
					ArrayList<DetailSchedule> details = new ArrayList<DetailSchedule>();
					Cursor detailData = SqlHelper.instance.select(
							"ScheduleDetail",
							"*",
							"Schedule_Id = "
									+ scheduleData.getLong(scheduleData
											.getColumnIndex("Id")));
					if (detailData != null) {
						if (detailData.moveToFirst()) {

							do {
								details.add(new DetailSchedule(
										detailData.getLong(detailData
												.getColumnIndex("Id")),
										detailData.getLong(detailData
												.getColumnIndex("Category_Id")),
										detailData.getDouble(detailData
												.getColumnIndex("Budget")),
										detailData.getLong(detailData
												.getColumnIndex("Schedule_Id"))));
							} while (detailData.moveToNext());
						}
					}

					returnValues.add(new Schedule(scheduleData
							.getLong(scheduleData.getColumnIndex("Id")),
							scheduleData.getFloat(scheduleData
									.getColumnIndex("Budget")), Converter
									.toDate(scheduleData.getString(scheduleData
											.getColumnIndex("Start_date"))),
							Converter.toDate(scheduleData
									.getString(scheduleData
											.getColumnIndex("End_date"))),
							scheduleData.getInt(scheduleData
									.getColumnIndex("Type")), details));

				} while (scheduleData.moveToNext());
			}
		}

		return returnValues;
	}
}
