package money.Tracker.common.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlConnector extends SQLiteOpenHelper {
	private final static String dBName = "moneytracker.Database";

	public SqlConnector(Context context) {
		super(context, dBName, null, 1);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	private void initialConstantTable(SQLiteDatabase db)
	{
		
	}
}
