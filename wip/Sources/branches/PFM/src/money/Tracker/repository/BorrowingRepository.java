package money.Tracker.repository;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;

public class BorrowingRepository implements IDataRepository {
	public BorrowingRepository instance;

	public BorrowingRepository() {
		// TODO Auto-generated constructor stub
		createTable();
	}

	private void createTable() {
	}

	public ArrayList<Object> getData(String param) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		return returnValues;
	}
}
