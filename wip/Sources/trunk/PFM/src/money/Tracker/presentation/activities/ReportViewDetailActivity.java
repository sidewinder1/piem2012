package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReportViewDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view_detail);
        Log.d("report detail", "Check 1");
        bindData();
        Log.d("report detail", "Check 12");
    }

    private void bindData() {
		// TODO Auto-generated method stub
    	Log.d("report detail", "Check 2");
    	Bundle extras = getIntent().getExtras();
		int scheduleID = extras.getInt("schedule_id");
		Log.d("report detail", "Check 3 - " + scheduleID);
		ArrayList<String> title = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		Log.d("report detail", "Check 4");
		Date startDate = null;
		Date endDate = null;
		Log.d("report detail", "Check 5");
		// get budget
		Cursor scheduleCursor = SqlHelper.instance.select("Schedule", "*", "Id="+ scheduleID);
		if (scheduleCursor != null)
		{
			if (scheduleCursor.moveToFirst())
			{
				do
				{
					startDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
					Log.d("report detail", "Check 5 - " + scheduleCursor.getString(scheduleCursor.getColumnIndex("Start_date")));
					endDate = Converter.toDate(scheduleCursor.getString(scheduleCursor.getColumnIndex("End_date")));
					title.add("Budget");
					value.add(String.valueOf(scheduleCursor.getDouble(scheduleCursor.getColumnIndex("Budget"))));
				}while(scheduleCursor.moveToNext());
			}
		}
		
		Log.d("report detail", "Check 5 - " + Converter.toString(startDate, "dd/MM/yyyy"));
		Log.d("report detail", "Check 5 - " + Converter.toString(endDate, "dd/MM/yyyy"));
		
		// get entry
		double total = 0;
		Cursor entryCursor = SqlHelper.instance.select("Entry", "*", "Type=0");
		if(entryCursor != null)
		{
			if(entryCursor.moveToFirst())
			{
				do
				{
					int id = entryCursor.getInt(entryCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryCursor.getString(entryCursor.getColumnIndex("Date")));					
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {									
									total += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				}while(entryCursor.moveToNext());
			}
			
			title.add("Income");
			value.add(String.valueOf(total));
			
			if(entryCursor.moveToFirst())
			{				
				do
				{
					int id = entryCursor.getInt(entryCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryCursor.getString(entryCursor.getColumnIndex("Date")));					
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {									
									title.add(entryDetailCursor.getString(entryDetailCursor.getColumnIndex("Name")));
									value.add(String.valueOf(entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"))));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				}while(entryCursor.moveToNext());
			}
		}
		
		
		// get expense
		double totalExpense = 0;
		Cursor entryExpenseCursor = SqlHelper.instance.select("Entry", "*", "Type=1");
		Log.d("report detail", "Check 51");
		if(entryExpenseCursor != null)
		{
			Log.d("report detail", "Check 52");
			if(entryExpenseCursor.moveToFirst())
			{
				Log.d("report detail", "Check 53");
				do
				{
					Log.d("report detail", "Check 54");
					int id = entryExpenseCursor.getInt(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));
					Log.d("report detail", "Check 55 - " + Converter.toString(entryDate, "dd/MM/yyyy"));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						Log.d("report detail", "Check 56");
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "*", "Entry_Id=" + id);
						if (entryDetailCursor != null) {
							if (entryDetailCursor.moveToFirst()) {
								do {									
									totalExpense += entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Money"));
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				}while(entryExpenseCursor.moveToNext());
			}
			
			title.add("Expense");
			value.add(String.valueOf(totalExpense));
			
			if(entryExpenseCursor.moveToFirst())
			{
				Log.d("report detail", "Check 57");
				do
				{
					Log.d("report detail", "Check 58");
					int id = entryExpenseCursor.getInt(entryExpenseCursor.getColumnIndex("Id"));
					Date entryDate = Converter.toDate(entryExpenseCursor.getString(entryExpenseCursor.getColumnIndex("Date")));					
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						Log.d("report detail", "Check 59");
						Cursor entryDetailCursor = SqlHelper.instance.select("EntryDetail", "Category_Id, sum(Money) as Total", "Entry_Id=" + id + " group by Category_Id");
						Log.d("report detail", "Check 60");
						if (entryDetailCursor != null) {
							Log.d("report detail", "Check 61");
							if (entryDetailCursor.moveToFirst()) {
								Log.d("report detail", "Check 62");
								do {
									Log.d("report detail", "Check 63");
									int categoryID = entryDetailCursor.getInt(entryDetailCursor.getColumnIndex("Category_Id"));
									Cursor categoryCursor = SqlHelper.instance.select("Category", "*", "Id=" + categoryID);
									Log.d("report detail", "Check 64");
									if (categoryCursor != null)
									{
										Log.d("report detail", "Check 65");
										if (categoryCursor.moveToFirst())
										{
											Log.d("report detail", "Check 66");
											do
											{
												Log.d("report detail", "Check 67");
												title.add(categoryCursor.getString(categoryCursor.getColumnIndex("Name")));
											}while(categoryCursor.moveToNext());
										}
									}
									Log.d("report detail", "Check 68");
									//title.add(entryDetailCursor.getString(entryDetailCursor.getColumnIndex("Name")));
									value.add(String.valueOf(entryDetailCursor
											.getDouble(entryDetailCursor
													.getColumnIndex("Total"))));
									Log.d("report detail", "Check 69");
								} while (entryDetailCursor.moveToNext());
							}
						}
					}
				}while(entryCursor.moveToNext());
			}
		}
		
		// get borrow
		double totalBorrowing = 0;
		Cursor borrowingCursor = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like 'Borrowing'");		
		if(borrowingCursor != null)
		{			
			if(borrowingCursor.moveToFirst())
			{				
				do
				{					
					Date entryDate = Converter.toDate(borrowingCursor.getString(borrowingCursor.getColumnIndex("Start_date")));
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						Log.d("report detail", "Check 56");
						totalBorrowing +=  borrowingCursor.getDouble(borrowingCursor.getColumnIndex("Money"));
					}
				}while(borrowingCursor.moveToNext());
			}
			
			title.add("Borrowing");
			value.add(String.valueOf(totalBorrowing));
			
			if(borrowingCursor.moveToFirst())
			{
				Log.d("report detail", "Check 57");
				do
				{
					Log.d("report detail", "Check 58");
					Date entryDate = Converter.toDate(borrowingCursor.getString(borrowingCursor.getColumnIndex("Start_date")));					
					if (entryDate.compareTo(startDate) > 0
							&& entryDate.compareTo(endDate) < 0
							|| entryDate.compareTo(startDate) == 0
							|| entryDate.compareTo(endDate) == 0)
					{
						title.add(borrowingCursor.getString(borrowingCursor.getColumnIndex("Person_name")));
						value.add(borrowingCursor.getString(borrowingCursor.getColumnIndex("Money")));
					}
				}while(borrowingCursor.moveToNext());
			}
		}
		
		// get lending
				double totalLending = 0;
				Cursor lendingCursor = SqlHelper.instance.select("BorrowLend", "*", "Debt_type like 'Lending'");		
				if(lendingCursor != null)
				{			
					if(lendingCursor.moveToFirst())
					{				
						do
						{					
							Date entryDate = Converter.toDate(lendingCursor.getString(borrowingCursor.getColumnIndex("Start_date")));
							if (entryDate.compareTo(startDate) > 0
									&& entryDate.compareTo(endDate) < 0
									|| entryDate.compareTo(startDate) == 0
									|| entryDate.compareTo(endDate) == 0)
							{
								Log.d("report detail", "Check 56");
								totalLending +=  lendingCursor.getDouble(lendingCursor.getColumnIndex("Money"));
							}
						}while(lendingCursor.moveToNext());
					}
					
					title.add("Lending");
					value.add(String.valueOf(totalLending));
					
					if(lendingCursor.moveToFirst())
					{
						Log.d("report detail", "Check 57");
						do
						{
							Log.d("report detail", "Check 58");
							Date entryDate = Converter.toDate(lendingCursor.getString(lendingCursor.getColumnIndex("Start_date")));					
							if (entryDate.compareTo(startDate) > 0
									&& entryDate.compareTo(endDate) < 0
									|| entryDate.compareTo(startDate) == 0
									|| entryDate.compareTo(endDate) == 0)
							{
								title.add(lendingCursor.getString(lendingCursor.getColumnIndex("Person_name")));
								value.add(lendingCursor.getString(lendingCursor.getColumnIndex("Money")));
							}
						}while(lendingCursor.moveToNext());
					}
				}
		
		Log.d("report detail", "Check 6");
		ListView titleListView = (ListView) findViewById(R.id.report_detail_title_list_view);
		ListView valueListView = (ListView) findViewById(R.id.report_detail_value_list_view);
		Log.d("report detail", "Check 7");
		String[] titleString = new String[title.size()];
		titleString = title.toArray(titleString);
		
		String[] valueString = new String[value.size()];
		valueString = value.toArray(valueString);
		
		Log.d("report detail", "Check 8");
		titleListView.setAdapter(new ArrayAdapter<String>(this, R.layout.single_row_view, titleString));
		Log.d("report detail", "Check 9");
		valueListView.setAdapter(new ArrayAdapter<String>(this, R.layout.single_row_view, valueString));
		Log.d("report detail", "Check 10");
	}



	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_report_view_detail, menu);
        return true;
    }
}
