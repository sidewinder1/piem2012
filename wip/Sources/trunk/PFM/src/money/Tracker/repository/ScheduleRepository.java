package money.Tracker.repository;

import money.Tracker.common.sql.SqlHelper;

public class ScheduleRepository implements IDataRepository {
	public ScheduleRepository instance;
	
	public ScheduleRepository() 
	{
	}

	protected void getData() {
		SqlHelper.instance.createTable("Schedule",
				new StringBuilder("id INTEGER PRIMARY KEY, Budget FLOAT,")
						.append("Start_date DATE, End_date DATE").toString());
	}
}
