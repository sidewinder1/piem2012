package money.Tracker.common.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlConnector extends SQLiteOpenHelper {
	private final static String dBName = "PFMDatabase";
	private boolean onCreateDB;

	public SqlConnector(Context context) {
		super(context, dBName, null, 2);
		// TODO Auto-generated constructor stub
	}

	public boolean isOnCreateDB() {
		return onCreateDB;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		onCreateDB = true;
		initializeTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	private void initializeTable(SQLiteDatabase db) {
		// Create table for Schedule.
		SqlHelper.instance
				.createTable(
						"Schedule",
						new StringBuilder(
								"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT, Time_Id INTEGER,")
								.append("Start_date DATE, End_date DATE")
								.toString());

		// Create table for Schedule Detail.
		SqlHelper.instance.createTable("ScheduleDetail",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT,")
						.append("Category_Id INTEGER, Schedule_Id INTEGER")
						.toString());

		// Create table for Lending.
		SqlHelper.instance.createTable("Lending",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGERL,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");

		// Create table for Borrowing.
		SqlHelper.instance.createTable("Borrowing",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGER,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT");

		// Create table for Category.
		SqlHelper.instance.createTable("Category",
				new StringBuilder(
						"id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT,")
						.append("User_Color TEXT").toString());
		String[] names = { "Birthday", "Food", "Entertainment", "Shopping",
				"Others" };
		String[] colors = { "#FF0000", "#00FFFF", "#0000FF", "#0000A0",
				"#ADD8E6" };

		for (int index = 0; index < names.length; index++) {
			SqlHelper.instance.insert("Category", new String[] { "Name",
					"User_Color" },
					new String[] { names[index], colors[index] });
		}

	}
}
