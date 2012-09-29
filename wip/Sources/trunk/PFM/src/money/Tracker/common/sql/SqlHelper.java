package money.Tracker.common.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlHelper {
	public static SqlHelper instance;
	private static SQLiteDatabase currentDb;
	public SqlHelper(Context context) {
		SqlConnector openHelper = new SqlConnector(context);
		currentDb = openHelper.getWritableDatabase();
	}

	public boolean createTable(String tableName, String columnsInfo)
	{
		try
		{
			currentDb.execSQL(new StringBuilder("CREATE TABLE ").append(tableName).
					append("(").append(columnsInfo).append(")").toString());
		}
		catch(Exception e)
		{
			//to do: log exception later.
			return false;
		}
		
		return true;
	}
	
	public long insert(String tableName, String [] columnNames, String [] columnValues)
	{
		ContentValues contentValues = new ContentValues();
		
		for (int index = 0; index < columnNames.length; index++)
		{
			contentValues.put(columnNames[index], columnValues[index++]);	
		}

		try
		{
			return currentDb.insert(tableName, null, contentValues);
		}
		catch(Exception e)
		{
			// to do add to log file.
			return -1;
		}
	}

	public Cursor select(String tableName, String selectedColumns, String whereCondition)
	{
		if (whereCondition != null)
		{
			whereCondition = new StringBuilder(" WHERE ").append(whereCondition).toString();
		}
		else
		{
			whereCondition = "";
		}
		
		Cursor cursor = null;
		try
		{
			cursor = currentDb.rawQuery(new StringBuilder("SELECT ").append(selectedColumns).
					append(" FROM ").append(tableName).
					append(whereCondition).toString(), null);
		}
		catch(Exception e)
		{
			// to do add log file.
		}
		
		return cursor;
	}
}

