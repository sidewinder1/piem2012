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

	public ArrayList<Object> getData(String condition) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*",
				condition);

		if (borrowLendData != null) {
			Log.d("Select", "Check 1");
			if (borrowLendData.moveToFirst()) {
				Log.d("Select", "Check 2");
				do {
					Log.d("Select", "Check 3");
					BorrowLend bole = new BorrowLend();
					Log.d("Select", "Check 4");
					bole.setId(borrowLendData.getInt(borrowLendData
							.getColumnIndex("ID")));
					Log.d("Select", "Check 5");
					bole.setDebtType(borrowLendData.getString(borrowLendData
							.getColumnIndex("Debt_type")));
					Log.d("Select", "Check 6");
					bole.setMoney(borrowLendData.getDouble(borrowLendData
							.getColumnIndex("Money")));
					Log.d("Select", "Check 7");
					bole.setInterestType(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Interest_type")));
					Log.d("Select", "Check 8");
					bole.setInterestRate(borrowLendData.getInt(borrowLendData
							.getColumnIndex("Interest_rate")));
					Log.d("Select", "Check 9");
					bole.setStartDate(Converter.toDate(
							borrowLendData
									.getString(
											borrowLendData
													.getColumnIndex("Start_date"))
									.trim(), "dd/MM/yyyy"));
					Log.d("Select", "Check 10");
					if (!borrowLendData
							.getString(
									borrowLendData
											.getColumnIndex("Expired_date"))
							.trim().equals("")) {
						bole.setExpiredDate(Converter.toDate(
								borrowLendData
										.getString(
												borrowLendData
														.getColumnIndex("Expired_date"))
										.trim(), "dd/MM/yyyy"));
						Log.d("Select", "Check 11");
					} else {
						bole.setExpiredDate(null);
						Log.d("Select", "Check 12");
					}
					bole.setPersonName(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_name")));
					Log.d("Select", "Check 13");
					bole.setPersonPhone(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_phone")));
					Log.d("Select", "Check 14");
					bole.setPersonAddress(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Person_address")));
					Log.d("Select", "Check 15");
					returnValues.add(bole);
					Log.d("Select", "Check 16");
				} while (borrowLendData.moveToNext());
			}
		}
		Log.d("Select", "Check 17");
		return returnValues;
	}

	public BorrowLend getDetailData(String condition) {
		BorrowLend bole = new BorrowLend();
		Cursor borrowLendData = SqlHelper.instance.select("BorrowLend", "*",
				condition);
		Log.d("condition", condition);
		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				do {
					bole.setId(borrowLendData.getInt(borrowLendData
							.getColumnIndex("ID")));
					bole.setDebtType(borrowLendData.getString(borrowLendData
							.getColumnIndex("Debt_type")));
					bole.setMoney(borrowLendData.getDouble(borrowLendData
							.getColumnIndex("Money")));
					bole.setInterestType(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Interest_type")));
					bole.setInterestRate(borrowLendData.getInt(borrowLendData
							.getColumnIndex("Interest_rate")));
					bole.setStartDate(Converter.toDate(
							borrowLendData
									.getString(
											borrowLendData
													.getColumnIndex("Start_date"))
									.trim(), "dd/MM/yyyy"));
					if (!borrowLendData
							.getString(
									borrowLendData
											.getColumnIndex("Expired_date"))
							.trim().equals("")) {
						bole.setExpiredDate(Converter.toDate(
								borrowLendData
										.getString(
												borrowLendData
														.getColumnIndex("Expired_date"))
										.trim(), "dd/MM/yyyy"));
					} else {
						bole.setExpiredDate(null);
					}
					bole.setPersonName(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_name")));
					bole.setPersonPhone(borrowLendData.getString(borrowLendData
							.getColumnIndex("Person_phone")));
					bole.setPersonAddress(borrowLendData
							.getString(borrowLendData
									.getColumnIndex("Person_address")));
				} while (borrowLendData.moveToNext());
			}
		}
		return bole;
	}

}
