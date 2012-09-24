package pfm.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	static final String dbName = "PFMDB";
	static final String nameTable = "Name";
	static final String colID = "ID";
	static final String colName = "Name";

	public DatabaseHandler(Context context) {
		super(context, dbName, null, 33);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{			  
		db.execSQL("CREATE TABLE " + nameTable + 
			       "(" + colName + " TEXT)");
	}
	
	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + nameTable);
	}
	
	public void insertData (String str)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("INSERT INTO " + nameTable + " values (" + str + ") ");
	}
	
	public ArrayList<String> getData ()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		ArrayList<String> results = new ArrayList<String>();
		
		Cursor c = db.rawQuery("SELECT * FROM " + nameTable, null);
    	
    	if (c != null ) {
    		if  (c.moveToFirst()) {
    			do {
    				String name = c.getString(c.getColumnIndex(colName));
    				results.add(name);
    			}while (c.moveToNext());
    		} 
    	}
		
		return results;
	}
}
