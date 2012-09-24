package pfm.database;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	static final String dbName = "PFMDB";
	static final String borrowingTable = "Borrowing";
	static final String colID = "ID";
	static final String colMoney = "Money";
	static final String colInterest = "Interest";
	static final String colName = "Name";
	static final String colStartDate = "Start_date";
	static final String colExpiredDate = "Expired_date";

	public DatabaseHandler(Context context) {
		super(context, dbName, null, 33);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{			  
		db.execSQL("CREATE TABLE " + borrowingTable + 
			       "(" + colID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			             colMoney + " INTEGER, " + 
			             colInterest + " Integer, " + 
			             colName + " TEXT, " +
			             colStartDate + " TEXT, " +
			             colExpiredDate + " TEXT)");
	}
	
	@Override
	public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + borrowingTable);
	}
	
	public void insertData (InputType inputType)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		
		db.execSQL("INSERT INTO " + borrowingTable + 
				   " values (" + inputType.getTotal() + ", " + 
				                 inputType.getInterest() + ", " + 
				                 inputType.getName() + ", " + 
				                 inputType.getStartDate() + ", " + 
				                 inputType.getEndDate() + ")");
	}
	
	public ArrayList<String> getData ()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		ArrayList<String> results = new ArrayList<String>();
		
		Cursor c = db.rawQuery("SELECT * FROM " + borrowingTable, null);
    	
    	if (c != null ) {
    		if  (c.moveToFirst()) {
    			do {
    				int id = c.getInt(c.getColumnIndex(colID));
    				int total = c.getInt(c.getColumnIndex(colMoney));
    				int interest = c.getInt(c.getColumnIndex(colInterest));
    				String name = c.getString(c.getColumnIndex(colName));
    				String startDate = c.getString(c.getColumnIndex(colStartDate));
    				String endDate = c.getString(c.getColumnIndex(colExpiredDate));
    				results.add(id + ". " + name + " " + total + " (" + endDate + ")");
    			}while (c.moveToNext());
    		} 
    	}
		
		return results;
	}
}
