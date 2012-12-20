package money.Tracker.common.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * <summary>
 * An inherited class from SQLiteOpenHelper.
 * This class is used open a database for application.
 * </summary>
 */

public class SqlConnector extends SQLiteOpenHelper {
	private final static String dBName = "moneytracker.Database";

	/*
	 * <summary>
	 * The constructor.
	 * </summary>
	 */
	
	public SqlConnector(Context context) {
		super(context, dBName, null, 1);
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	/*
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}