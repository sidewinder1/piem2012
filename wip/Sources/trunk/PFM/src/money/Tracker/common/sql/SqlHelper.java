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
			currentDb.execSQL(new StringBuilder("CREATE TABLE ")
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
		} catch (Exception e) {
			return false;
		}
	}

	public Cursor select(String tableName, String selectedColumns,
			String whereCondition) {
		if (!"".equals(whereCondition)) {
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
}
