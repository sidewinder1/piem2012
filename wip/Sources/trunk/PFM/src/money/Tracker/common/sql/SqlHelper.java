package money.Tracker.common.sql;

import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.Logger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
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

		contentValues.put("Create", Converter.toString(DateTimeHelper.now()));
		contentValues.put("ModifiedDate", Converter.toString(DateTimeHelper.now()));
		
		try {
			return currentDb.insert(tableName, null, contentValues);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return -1;
		}
	}

	public int update(String tableName, String[] columns, String[] newValues,
			String whereCondition) {

		ContentValues newValueContent = new ContentValues();

		for (int i = 0; i < columns.length; i++) {
			newValueContent.put(columns[i], newValues[i]);
		}
		
		newValueContent.put("ModifiedDate", Converter.toString(DateTimeHelper.now()));
		
		try {
			return currentDb.update(tableName, newValueContent, whereCondition,
					null);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return 0;
		}
	}

	public boolean delete(String tableName, String whereCondition) {
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName)
				.append(" WHERE ").append(whereCondition);
		try {
			currentDb.execSQL(sql.toString());
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return false;
		}
		return true;
	}

	public boolean drop(String tableName) {
		try {
			currentDb.execSQL("DROP TABLE IF EXISTS " + tableName);
			return true;
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return false;
		}
	}

	public Cursor query(String sqlStatement) {
		Cursor cursor = null;
		try {
			cursor = currentDb.rawQuery(sqlStatement, null);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
		}

		return cursor;
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
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
		}

		return cursor;
	}

	public void initializeTable() {
		// Create table for Schedule.
		createTable(
				"Schedule",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT, Type INTEGER, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,")
						.append("Start_date DATE, End_date DATE").toString());
		// Create table for Schedule Detail.
		createTable("ScheduleDetail",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT, Budget FLOAT, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,")
						.append("Category_Id INTEGER, Schedule_Id INTEGER")
						.toString());

		// Create table for Entry Detail.
		createTable(
				"EntryDetail",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT, Category_Id INTEGER, Name TEXT, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,")
						.append("Money FLOAT, Entry_Id INTEGER").toString());

		// Create table for Entry Detail.
		createTable("Entry",
				new StringBuilder("Id INTEGER PRIMARY KEY AUTOINCREMENT, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,")
						.append("Date DATE, Type INTEGER").toString());

		// Create table for Borrow and Lending.
		createTable("BorrowLend", "ID INTEGER PRIMARY KEY autoincrement, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,"
				+ "Debt_type TEXT," + "Money INTEGER," + "Interest_type TEXT,"
				+ "Interest_rate INTEGER," + "Start_date DATE,"
				+ "Expired_date DATE," + "Person_name TEXT,"
				+ "Person_phone TEXT," + "Person_address TEXT");

		// Create table for Category.
		createTable("Category",
				new StringBuilder(
						"Id INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT, Create DATE, ModefiedDate DATE, IsDeleted INTEGER,")
						.append("User_Color TEXT").toString());
		String[] names = { "Birthday", "Food", "Entertainment", "Shopping",
				"Others" };
		String[] colors = { "#99FF0000", "#9900FFFF", "#990000FF", "#9900A0A0",
				"#99ADD8E6" };

		Cursor categoryCheck = select("Category", "*",
				"Name='Birthday' AND User_Color='#99FF0000'");

		if (categoryCheck != null && !categoryCheck.moveToFirst()) {
			for (int index = 0; index < names.length; index++) {
				SqlHelper.instance.insert("Category", new String[] { "Name",
						"User_Color" }, new String[] { names[index],
						colors[index] });
			}
		}

		// Create table for Color.
		createTable("UserColor",
				new StringBuilder("Id INTEGER PRIMARY KEY AUTOINCREMENT,")
						.append("User_Color TEXT").toString());

		String[] color_codes = { "#9900FFFF", "#99ADD8E6", "#99FFA500",
				"#99800080", "#99A52A2A", "#99FFFF00", "#99800000",
				"#9900FF00", "#99008000", "#99FF00FF", "#99808000",
				"#99C0C0C0", "#9998AFC7", "#990000FF", "#99808080",
				"#990000A0", "#99000000", "#99737CA1", "#99737CA1",
				"#99F6358A", "#998BB381", "#9941A317", "#994AA02C",
				"#9999C68E", "#994CC417", "#996CC417", "#9952D017",
				"#99EAC117", "#99FDD017" };

		Cursor colorCheck = select("UserColor", "*", "User_Color='#99FF0000'");

		if (colorCheck != null && !colorCheck.moveToFirst()) {
			for (int index = 0; index < color_codes.length; index++) {
				SqlHelper.instance.insert("UserColor",
						new String[] { "User_Color" },
						new String[] { color_codes[index] });
			}
		}

	}
}
