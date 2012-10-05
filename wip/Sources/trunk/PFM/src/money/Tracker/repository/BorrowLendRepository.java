package money.Tracker.repository;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;

public class BorrowLendRepository {
	private BorrowLendRepository instance;

	public BorrowLendRepository() {
		// TODO Auto-generated constructor stub
		createTable();
	}

	private void createTable() {
	}

	public ArrayList<Object> getData(String tableName) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor borrowLendData = SqlHelper.instance.select(tableName, "*", "");

		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					BorrowLend bole = new BorrowLend();

					bole.setId(borrowLendData.getInt(borrowLendData
							.getColumnIndex("ID")));
					bole.setMoney(borrowLendData.getDouble(borrowLendData
							.getColumnIndex("Money")));
					bole.setInterestType(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Interest_type")));
					bole.setInterestRate(borrowLendData.getInt(borrowLendData
							.getColumnIndex("Interest_rate")));					
					bole.setStartDate(Converter.toDate(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Start_date")).trim(), "dd/MM/yyyy"));
					if (!borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Expired_date")).trim().equals(""))
					{
						bole.setExpiredDate(Converter.toDate(borrowLendData
								.getString(borrowLendData
										.getColumnIndex("Expired_date")).trim(), "dd/MM/yyyy"));
					}
					else
					{
						bole.setExpiredDate(null);
					}
					bole.setPersonName(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_name")));
					bole.setPersonPhone(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_Phone")));
					bole.setPersonAddress(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Person_address")));
					returnValues.add(bole);
				} while (borrowLendData.moveToNext());
			}
		}
		return returnValues;
	}
	
	public BorrowLend getDetailData(String tableName, String condition) {
		BorrowLend bole = new BorrowLend();
		Cursor borrowLendData = SqlHelper.instance.select(tableName, "*", condition);
		Log.d("condition", condition);
		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					bole.setId(borrowLendData.getInt(borrowLendData
							.getColumnIndex("ID")));
					bole.setMoney(borrowLendData.getDouble(borrowLendData
							.getColumnIndex("Money")));					
					bole.setInterestType(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Interest_type")));					
					bole.setInterestRate(borrowLendData.getInt(borrowLendData
							.getColumnIndex("Interest_rate")));
					bole.setStartDate(Converter.toDate(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Start_date")).trim(), "dd/MM/yyyy"));
					Log.d("Select", "Check 1");
					if (!borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Expired_date")).trim().equals(""))
					{
						Log.d("Select", "Check 2");
						bole.setExpiredDate(Converter.toDate(borrowLendData
								.getString(borrowLendData
										.getColumnIndex("Expired_date")).trim(), "dd/MM/yyyy"));
						Log.d("Select", "Check 3");
					}
					else
					{
						Log.d("Select", "Check 4");
						bole.setExpiredDate(null);
						Log.d("Select", "Check 5");
					}
					Log.d("Select", "Check 6");
					bole.setPersonName(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_name")));
					bole.setPersonPhone(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_Phone")));
					bole.setPersonAddress(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Person_address")));					
				} while (borrowLendData.moveToNext());
			}
		}
		return bole;
	}

}
