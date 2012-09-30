package money.Tracker.repository;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;

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
	
	public ArrayList<Object> getData(String param) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		return returnValues;
	}
}
