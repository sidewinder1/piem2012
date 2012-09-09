package pfm2012.sqllite;

import android.database.*;
import android.database.sqlite.*;
import android.content.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper{
	// Database variable
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PFMDatabase";
	
	public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
	// Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
    	String CREATE_Expenditure_TABLE = 
        		"CREATE TABLE expenditures(" +
        				"expenditure_id INTEGER PRIMARY KEY, " +
        				"expenditure_name TEXT, " +
        				"expenditure_value LONG, " +
        				"expenditure_date String, " +
        				"type_id INTEGER)";
        db.execSQL(CREATE_Expenditure_TABLE);
        
        String CREATE_Type_TABLE = 
        		"CREATE TABLE types(" +
        				"id INTEGER PRIMARY KEY, " +
        				"type_name TEXT)";
        db.execSQL(CREATE_Type_TABLE);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS expenditures");
        db.execSQL("DROP TABLE IF EXISTS types");
 
        // Create tables again
        onCreate(db);
    }
    
    // Adding new contact
    void addExpenditure(Expenditure expenditures) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("expenditure_name", expenditures.getExpenditureName()); //Expenditure Name
        values.put("expenditure_value", expenditures.getExpenditureValue()); //Expenditure Value
        values.put("expenditure_date", expenditures.getExpenditureDate()); //Expenditure Value
 
        Cursor cursor = db.query("types", new String[] {"type_id"}, "type_name=?", new String[] {expenditures.getTypeName()}, null, null, null, null);
        values.put("type_id", cursor.getString(0));
        
        // Inserting Row
        db.insert("expenditures", null, values);
        db.close(); // Closing database connection
    }
 
    // Getting single contact
    public Expenditure getExpenditure(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
 
        Cursor cursor = db.query("expenditures", new String[] { "expenditure_id",
                "expenditure_name", "expenditure_name", "expenditure_value", "type_id" }, "expenditure_id=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
 
        Cursor cursor1 = db.query("types", new String[] {"type_name"}, "type_id=?", new String[] {cursor.getString(4)}, null, null, null, null);
        
        Expenditure expenditure = new Expenditure(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), Integer.parseInt(cursor.getString(2)), cursor.getString(3), cursor1.getString(0));
        
        // return expenditure
        return expenditure;
    }
 
    // Getting All Contacts
    public List<Expenditure> getAllContacts() {
        List<Expenditure> expenditureList = new ArrayList<Expenditure>();
        // Select All Query
        String selectQuery = "SELECT  * FROM expenditures";
 
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
 
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Expenditure expenditure = new Expenditure();
                expenditure.setExpenditureId(Integer.parseInt(cursor.getString(0)));
                expenditure.setExpenditureName(cursor.getString(1));
                expenditure.setExpenditureValue(Integer.parseInt(cursor.getString(2)));
                expenditure.setExpenditureDate(cursor.getString(3));
                
                String selectQuery1 = "SELECT  * FROM types where type_id=" + Integer.parseInt(cursor.getString(4).trim());
                
                SQLiteDatabase db1 = this.getWritableDatabase();
                Cursor cursor1 = db1.rawQuery(selectQuery1, null);
                
                expenditure.setTypeName(cursor1.getString(0));
                // Adding contact to list
                expenditureList.add(expenditure);
            } while (cursor.moveToNext());
        }
 
        // return contact list
        return expenditureList;
    }
 
    // Updating single contact
    public int updateContact(Expenditure expenditure) {
        SQLiteDatabase db = this.getWritableDatabase();
 
        ContentValues values = new ContentValues();
        values.put("expenditure_name", expenditure.getExpenditureName());
        values.put("expenditure_value", expenditure.getExpenditureValue());
        values.put("expenditure_date", expenditure.getExpenditureDate());
        Cursor cursor = db.query("types", new String[] {"type_id"}, "type_name=?", new String[] {expenditure.getTypeName()}, null, null, null, null);
        values.put("type_id", cursor.getString(0));
 
        // updating row
        return db.update("expenditures", values, "expenditure_id = ?",
                new String[] { String.valueOf(expenditure.getExpenditureId()) });
    }
 
    // Deleting single contact
    public void deleteContact(Expenditure expenditure) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("expenditures", "expenditure_id = ?",
                new String[] { String.valueOf(expenditure.getExpenditureId()) });
        db.close();
    }
}
