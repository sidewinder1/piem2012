package money.Tracker.repository;

import money.Tracker.common.sql.SqlHelper;

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
}
