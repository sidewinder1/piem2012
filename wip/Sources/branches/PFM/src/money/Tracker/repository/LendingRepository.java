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
	}
	
	public ArrayList<Object> getData(String param) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		return returnValues;
	}
}
