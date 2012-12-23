package money.Tracker.repository;

import java.util.ArrayList;
import android.database.Cursor;
import android.util.Log;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;

public class BorrowLendRepository {
	public BorrowLendRepository() {
	}

	public ArrayList<Object> getData(String condition) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*", condition);

		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					BorrowLend bole = new BorrowLend();
					bole.setId(borrowLendData.getLong(borrowLendData.getColumnIndex("Id")));					
					bole.setDebtType(borrowLendData.getString(borrowLendData.getColumnIndex("Debt_type")));
					bole.setMoney(borrowLendData.getLong(borrowLendData.getColumnIndex("Money")));
					bole.setInterestType(borrowLendData.getString(borrowLendData.getColumnIndex("Interest_type")));
					bole.setInterestRate(borrowLendData.getInt(borrowLendData.getColumnIndex("Interest_rate")));
					bole.setStartDate(Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Start_date")).trim()));
					if (!borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim().equals("")) {
						Log.d("Check expired date", "Check 1");
						bole.setExpiredDate(Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim()));
					} else {
						Log.d("Check expired date", "Check 2");
						bole.setExpiredDate(null);
					}
					Log.d("Check expired date", String.valueOf(bole.getExpiredDate()));
					bole.setPersonName(borrowLendData.getString(borrowLendData.getColumnIndex("Person_name")));
					bole.setPersonPhone(borrowLendData.getString(borrowLendData.getColumnIndex("Person_phone")));
					bole.setPersonAddress(borrowLendData.getString(borrowLendData.getColumnIndex("Person_address")));
					returnValues.add(bole);
				} while (borrowLendData.moveToNext());
			}
		}
		
		borrowLendData.close();
		
		return returnValues;
	}
	
	public BorrowLend getDetailData(String condition) {
		BorrowLend bole = new BorrowLend();
		
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*", condition);
		
		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					bole.setId(borrowLendData.getLong(borrowLendData.getColumnIndex("Id")));
					bole.setDebtType(borrowLendData.getString(borrowLendData.getColumnIndex("Debt_type")));
					bole.setMoney(borrowLendData.getLong(borrowLendData.getColumnIndex("Money")));
					bole.setInterestType(borrowLendData.getString(borrowLendData.getColumnIndex("Interest_type")));
					bole.setInterestRate(borrowLendData.getInt(borrowLendData.getColumnIndex("Interest_rate")));
					bole.setStartDate(Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Start_date")).trim()));
					if (!borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim().equals("")) {
						bole.setExpiredDate(Converter.toDate(borrowLendData.getString(borrowLendData.getColumnIndex("Expired_date")).trim()));
					} else {
						bole.setExpiredDate(null);
					}
					bole.setPersonName(borrowLendData.getString(borrowLendData.getColumnIndex("Person_name")));
					bole.setPersonPhone(borrowLendData.getString(borrowLendData.getColumnIndex("Person_phone")).trim());
					bole.setPersonAddress(borrowLendData.getString(borrowLendData.getColumnIndex("Person_address")).trim());
				} while (borrowLendData.moveToNext());
			}
		}
		
		borrowLendData.close();
		return bole;
	}
}
