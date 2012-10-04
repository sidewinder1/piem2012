package money.Tracker.repository;

import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.model.DetailSchedule;

public class DetailScheduleRepository {
	private static DetailScheduleRepository instance;
	ArrayList<DetailSchedule> detailSchedules;

	public DetailScheduleRepository() {
	}

	public static DetailScheduleRepository getInstance() {
		return instance == null ? new DetailScheduleRepository() : instance;
	}

	public ArrayList<DetailSchedule> getData(String whereCondition) {
		Cursor detail = SqlHelper.instance.select("ScheduleDetail", "*",
				whereCondition);

		if (detail != null && detail.moveToFirst()) {
			detailSchedules = new ArrayList<DetailSchedule>();
			do {
				detailSchedules.add(new DetailSchedule(detail.getInt(detail
						.getColumnIndex("Id")), detail.getInt(detail
						.getColumnIndex("Category_Id")), detail
						.getDouble(detail.getColumnIndex("Budget")), detail
						.getInt(detail.getColumnIndex("Schedule_Id"))));
			} while (detail.moveToNext());
		}

		return detailSchedules;
	}
}
