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
		SqlHelper.instance.createTable("Borrowing",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGER,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");
	}

	public ArrayList<Object> getData(String param) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		return returnValues;
	}
}
