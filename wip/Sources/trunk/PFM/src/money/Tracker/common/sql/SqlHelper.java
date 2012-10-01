package money.Tracker.common.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqlHelper {
	public static SqlHelper instance;
	private static SQLiteDatabase currentDb;
	private static SqlConnector openHelper;

	public SqlHelper(Context context) {
		openHelper = new SqlConnector(context);
		currentDb = openHelper.getWritableDatabase();
	}
	
	public boolean createTable(String tableName, String columnsInfo) {
		try {
			currentDb.execSQL(new StringBuilder("CREATE TABLE IF NOT EXISTS ")
					.append(tableName).append("(").append(columnsInfo)
					.append(")").toString());
		} catch (Exception e) {
			// to do: log exception later.
			return false;
		}

		return true;
	}

	public long insert(String tableName, String[] columnNames,
			String[] columnValues) {
		ContentValues contentValues = new ContentValues();

		for (int index = 0; index < columnNames.length; index++) {
			contentValues.put(columnNames[index], columnValues[index]);
		}

		try {
			return currentDb.insert(tableName, null, contentValues);
		} catch (Exception e) {
			// to do add to log file.
			Log.e("SQLHelper", "Exception: " + e.getMessage());
			return -1;
		}
	}

	public boolean drop(String tableName) {
		try {
			currentDb.execSQL("DROP TABLE IF EXISTS " + tableName);
			return true;
		} 
		catch (Exception e) {
			return false;
		}
	}

	public Cursor select(String tableName, String selectedColumns,
			String whereCondition) {
		if (whereCondition != null && !"".equals(whereCondition)) {
			whereCondition = new StringBuilder(" WHERE ")
					.append(whereCondition).toString();
		} else {
			whereCondition = "";
		}

		Cursor cursor = null;
		try {
			 cursor = currentDb.rawQuery(
			 new StringBuilder("SELECT ").append(selectedColumns)
			 .append(" FROM ").append(tableName)
			 .append(whereCondition).toString(), null);
		} catch (Exception e) {
			// to do add log file.
		}

		return cursor;
	}
	
	public void initializeTable() {
		// Create table for Schedule.
		createTable(
						"Schedule",
						new StringBuilder(
								"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT, Time_Id INTEGER,")
								.append("Start_date DATE, End_date DATE")
								.toString());

		// Create table for Schedule Detail.
		createTable("ScheduleDetail",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT,")
						.append("Category_Id INTEGER, Schedule_Id INTEGER")
						.toString());

		// Create table for Lending.
		createTable("Lending",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGERL,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");

		// Create table for Borrowing.
		createTable("Borrowing",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGER,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT");

		// Create table for Category.
		createTable("Category",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT,")
						.append("User_Color TEXT").toString());
		String[] names = { "Birthday", "Food", "Entertainment", "Shopping",
				"Others" };
		String[] colors = { "#FF0000", "#00FFFF", "#0000FF", "#0000A0",
				"#ADD8E6" };
		
		Cursor categoryCheck = select("Category", "*", "Name='Birthday' AND User_Color='#FF0000'");
		
		if (categoryCheck != null && categoryCheck.moveToFirst())
		{
			return;
		}
		
		for (int index = 0; index < names.length; index++) {
			SqlHelper.instance.insert("Category", new String[] { "Name",
					"User_Color" },
					new String[] { names[index], colors[index] });
		}
	}
}
