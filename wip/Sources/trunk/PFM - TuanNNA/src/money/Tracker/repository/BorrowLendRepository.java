package money.Tracker.repository;

import java.sql.Date;
import java.util.ArrayList;

import android.database.Cursor;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.presentation.model.DetailSchedule;
import money.Tracker.presentation.model.Schedule;

public class BorrowLendRepository implements BorrowLendIDataRepository {

	public BorrowLendRepository() {
		// TODO Auto-generated constructor stub
		createTable();
	}

	private void createTable() {
		SqlHelper.instance.createTable("Borrowing",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGER,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");

		SqlHelper.instance.createTable("Lending",
				"ID INTEGER PRIMARY KEY autoincrement," + "Money INTEGER,"
						+ "Interest_type TEXT," + "Interest_rate INTEGERL,"
						+ "Start_date TEXT," + "Expired_date TEXT,"
						+ "Person_name TEXT," + "Person_Phone TEXT,"
						+ "Person_address TEXT);");
	}

	public ArrayList<Object> getData(String tablename) {
		ArrayList<Object> returnValues = new ArrayList<Object>();
		Cursor borrowLendData;

		if (tablename.equals("Borrowing")) {
			borrowLendData = SqlHelper.instance
					.select("Borrowing",
							"ID, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_Phone, Person_address",
							null);
		} else {
			borrowLendData = SqlHelper.instance
					.select("Lending",
							"ID, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_Phone, Person_address",
							null);
		}

		if (borrowLendData != null) {
			if (borrowLendData.moveToFirst()) {
				BorrowLend bl = new BorrowLend();

				bl.setId(Integer.parseInt(String.valueOf(borrowLendData
						.getColumnIndex("ID"))));
				bl.setMoney(Double.parseDouble(String.valueOf(borrowLendData
						.getColumnIndex("Money"))));
				bl.setInterestType(borrowLendData.getString(borrowLendData
						.getColumnIndex("Interest_type")));
				bl.setInterestRate(Integer.parseInt(String
						.valueOf(borrowLendData.getColumnIndex("Interest_type"))));
				bl.setStartDate(Converter.toDate(String.valueOf(borrowLendData
						.getColumnIndex("Start_date")), "DD-MM-YYYY"));
				bl.setExpiredDate(Converter.toDate(
						String.valueOf(borrowLendData
								.getColumnIndex("Expired_date")), "DD-MM-YYYY"));
				bl.setPersonName(borrowLendData.getString(borrowLendData
						.getColumnIndex("Person_name")));
				bl.setPersonPhone(borrowLendData.getString(borrowLendData
						.getColumnIndex("Person_Phone")));
				bl.setPersonAddress(borrowLendData.getString(borrowLendData
						.getColumnIndex("Person_address")));

				returnValues.add(bl);
			}

		}
		while (borrowLendData.moveToNext())
			;

		return returnValues;
	}
}
