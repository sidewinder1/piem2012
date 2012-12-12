package money.Tracker.common.sql;

import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
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

	/*
	 * This method is used to create a table with specified name and column
	 * information if this table doesn’t exist.
	 */
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

		long id = DateTimeHelper.nowInMillis();

		if (!contentValues.containsKey("Id") && !"AppInfo".equals(tableName)) {
			contentValues.put("Id", id);
		}

		if (!contentValues.containsKey("UserName")) {
			contentValues.put("UserName", AccountProvider.getInstance()
					.getCurrentAccount().name);
		}

		if (!contentValues.containsKey("CreatedDate")) {
			contentValues.put("CreatedDate",
					Converter.toString(DateTimeHelper.now(true)));
		}

		if (!contentValues.containsKey("IsDeleted")) {
			contentValues.put("IsDeleted", "0");
		}
		if (!contentValues.containsKey("ModifiedDate")) {
			contentValues.put("ModifiedDate",
					Converter.toString(DateTimeHelper.now(true)));
		}

		try {
			currentDb.insert(tableName, null, contentValues);
			return id;
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return -1;
		}
	}

	public int update(String tableName, String[] columns, String[] newValues,
			String whereCondition) {
		String whereForAppInfo = "1=1";
		if (!"AppInfo".equals(tableName)
				&& !whereCondition.contains("UserName")) {
			whereForAppInfo = new StringBuilder("UserName ='")
					.append(AccountProvider.getInstance().getCurrentAccount().name)
					.append("'").toString();
		}

		if (whereCondition != null && !"".equals(whereCondition)) {
			whereCondition = new StringBuilder(whereForAppInfo).append(" AND ")
					.append(whereCondition).toString();
		} else {
			whereCondition = new StringBuilder(whereForAppInfo).toString();
		}

		ContentValues newValueContent = new ContentValues();

		for (int i = 0; i < columns.length; i++) {
			newValueContent.put(columns[i], newValues[i]);
		}

		if (!newValueContent.containsKey("UserName")) {
			newValueContent.put("ModifiedDate",
					Converter.toString(DateTimeHelper.now(true)));
		}

		try {
			return currentDb.update(tableName, newValueContent, whereCondition,
					null);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return 0;
		}
	}

	public boolean delete(String tableName, String whereCondition) {
		ContentValues newValueContent = new ContentValues();
		String whereForAppInfo = "";
		if (!"AppInfo".equals(tableName)) {
			whereForAppInfo = new StringBuilder(" AND UserName ='")
					.append("UserColor".equals(tableName) ? "GlobalAccount"
							: AccountProvider.getInstance().getCurrentAccount().name)
					.append("'").toString();
		}

		if (whereCondition != null && !"".equals(whereCondition)) {
			whereCondition = new StringBuilder("IsDeleted = 0 ")
					.append(whereForAppInfo).append(" AND ")
					.append(whereCondition).toString();
		} else {
			whereCondition = new StringBuilder("IsDeleted = 0 ").append(
					whereForAppInfo).toString();
		}

		newValueContent.put("ModifiedDate",
				Converter.toString(DateTimeHelper.now(true)));
		if (!newValueContent.containsKey("IsDeleted")) {
			newValueContent.put("IsDeleted", "1");
		}

		try {
			currentDb.update(tableName, newValueContent, whereCondition, null);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
			return false;
		}
		return true;
	}

	public boolean deepDelete(String tableName, String whereCondition) {
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
		int whereIndex = sqlStatement.toLowerCase().indexOf(" where ") + 6;

		int fromIndex = sqlStatement.toLowerCase().indexOf(" from ") + 5;
		String tableName = sqlStatement.substring(fromIndex).trim().split(" ")[0];

		if (whereIndex >= 6) {
			String addedWhere = new StringBuilder(" ")
					.append(tableName)
					.append(".IsDeleted=0 AND (")
					.append(tableName)
					.append(".UserName='GlobalAccount' OR ")
					.append(tableName)
					.append(".UserName='")
					.append(AccountProvider.getInstance().getCurrentAccount().name)
					.append("') AND ").toString();
			sqlStatement = new StringBuilder(sqlStatement).insert(whereIndex,
					addedWhere).toString();
		} else {

			int tableNameIndex = sqlStatement.substring(fromIndex).indexOf(
					tableName)
					+ tableName.length() + 1;

			sqlStatement = new StringBuilder(sqlStatement)
					.insert(fromIndex + tableNameIndex,
							" WHERE IsDeleted=0 AND (UserName='GlobalAccount' OR UserName='"
									+ AccountProvider.getInstance()
											.getCurrentAccount().name + "') ")
					.toString();
		}
		try {
			cursor = currentDb.rawQuery(sqlStatement, null);
		} catch (Exception e) {
			Logger.Log("Exception: " + e.getMessage(), "SQLHelper");
		}

		return cursor;
	}

	public Cursor select(String tableName, String selectedColumns,
			String whereCondition) {
		return select(tableName, selectedColumns, whereCondition, false);
	}

	public Cursor select(String tableName, String selectedColumns,
			String whereCondition, boolean includeDeletedRecords) {
		String whereForAppInfo = "";
		if (!"AppInfo".equals(tableName)) {
			whereForAppInfo = new StringBuilder(
					" AND (UserName='GlobalAccount' OR UserName ='")
					.append(AccountProvider.getInstance().getCurrentAccount().name)
					.append("')").toString();
		}

		String isDeleted = includeDeletedRecords ? "0" : "IsDeleted";
		if (whereCondition != null && !"".equals(whereCondition)) {
			whereCondition = new StringBuilder(" WHERE ").append(isDeleted)
					.append(" = 0 ").append(whereForAppInfo).append(" AND ")
					.append(whereCondition).toString();
		} else {
			whereCondition = new StringBuilder(" WHERE ").append(isDeleted)
					.append(" = 0 ").append(whereForAppInfo).toString();
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

	public void dropAllTables() {
		drop("AppInfo");
		drop("Schedule");
		drop("ScheduleDetail");
		drop("EntryDetail");
		drop("Entry");
		drop("BorrowLend");
		drop("Category");
		drop("UserColor");
	}

	public void initializeTable() {
		// Create table for application configuration.
		createTable(
				"AppInfo",
				new StringBuilder("Id LONG PRIMARY KEY, UserName TEXT, ")
						.append("ScheduleWarn INTEGER, ScheduleRing TEXT, ScheduleRemind LONG, ")
						.append("BorrowWarn LONG, BorrowRing TEXT, BorrowRemind LONG, ")
						.append("LastSync DATE, Status INTEGER, CreatedDate DATE, ")
						.append("ModifiedDate DATE, IsDeleted INTEGER")
						.toString());

		// Create table for Schedule.
		createTable(
				"Schedule",
				new StringBuilder(
						"Id LONG PRIMARY KEY, Budget LONG, Type INTEGER, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER, UserName TEXT,")
						.append("Start_date DATE, End_date DATE").toString());
		// Create table for Schedule Detail.
		createTable(
				"ScheduleDetail",
				new StringBuilder(
						"Id LONG PRIMARY KEY, Budget LONG, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER,  UserName TEXT,")
						.append("Category_Id INTEGER, Schedule_Id INTEGER")
						.toString());

		// Create table for Entry Detail.
		createTable(
				"EntryDetail",
				new StringBuilder(
						"Id LONG PRIMARY KEY, Category_Id INTEGER, Name TEXT, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER,  UserName TEXT,")
						.append("Money LONG, Entry_Id INTEGER").toString());

		// Create table for Entry Detail.
		createTable(
				"Entry",
				new StringBuilder(
						"Id LONG PRIMARY KEY, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER, UserName TEXT,")
						.append("Date DATE, Type INTEGER").toString());

		// Create table for Borrow and Lending.
		createTable(
				"BorrowLend",
				"Id LONG PRIMARY KEY, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER,  UserName TEXT,"
						+ "Debt_type TEXT,"
						+ "Money LONG,"
						+ "Interest_type TEXT,"
						+ "Interest_rate INTEGER,"
						+ "Start_date DATE,"
						+ "Expired_date DATE,"
						+ "Person_name TEXT,"
						+ "Person_phone TEXT,"
						+ "Person_address TEXT");

		// Create table for Category.
		createTable(
				"Category",
				new StringBuilder(
						"Id LONG PRIMARY KEY,Name TEXT, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER,  UserName TEXT,")
						.append("User_Color TEXT").toString());
		String[] names = PfmApplication.getAppResources().getStringArray(
				R.array.category_name);
		String[] colors = PfmApplication.getAppResources().getStringArray(
				R.array.category_color);

		Cursor categoryCheck = select("Category", "*", "Name='" + names[0]
				+ "' AND User_Color='" + colors[0] + "'");

		if (categoryCheck != null && !categoryCheck.moveToFirst()) {
			for (int index = 0; index < names.length; index++) {
				SqlHelper.instance.insert("Category", new String[] { "Id",
						"Name", "User_Color", "UserName" }, new String[] {
						String.valueOf(index), names[index], colors[index],
						"GlobalAccount" });
			}
		}

		// Create table for Color.
		createTable(
				"UserColor",
				new StringBuilder(
						"Id LONG PRIMARY KEY, CreatedDate DATE, ModifiedDate DATE, IsDeleted INTEGER, UserName TEXT,")
						.append("User_Color TEXT").toString());

		String[] color_codes = PfmApplication.getAppResources().getStringArray(
				R.array.color_list);

		Cursor colorCheck = select("UserColor", "*", "User_Color='"
				+ color_codes[0] + "'");

		if (colorCheck != null && !colorCheck.moveToFirst()) {
			for (int index = 0; index < color_codes.length; index++) {
				SqlHelper.instance.insert("UserColor", new String[] {
						"User_Color", "UserName" }, new String[] {
						color_codes[index], "GlobalAccount" });
			}
		}

	}

}
