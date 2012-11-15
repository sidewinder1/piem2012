package money.Tracker.common.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.PfmApplication;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.database.Cursor;

public class SyncHelper {
	String NAMESPACE = "http://tempuri.org/";
	String URL = "http://10.0.2.2:1242/Service1.asmx";
	String CONFIG_FILE = "Fpm/FpmConfig.cnfg";
	private Date mLocalLastSync;

	private final static String[] sTables = { "Schedule", "ScheduleDetail",
			"EntryDetail", "Entry", "BorrowLend", "Category" };
	private final static String[] sColumns = {"Id, Budget, Type, CreatedDate, ModifiedDate, IsDeleted, Start_date, End_date",
		"Id, Budget, CreatedDate, ModifiedDate, IsDeleted, Category_Id, Schedule_Id",
		"Id, Category_Id, Name, CreatedDate, ModifiedDate, IsDeleted,Money, Entry_Id",
		"Id, CreatedDate, ModifiedDate, IsDeleted,Date, Type",
		"ID, CreatedDate, ModifiedDate, IsDeleted, Debt_type, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_phone, Person_address",
		"Id,Name, CreatedDate, ModifiedDate, IsDeleted,User_Color"
		};
	private HashMap<String, String[]> tableMap = new HashMap<String, String[]>();
	private static SyncHelper _instance;

	public SyncHelper() {
		getServerAddress();
		getLocalLastSync();
		createDictionary();
	}

	private void createDictionary() {
		for(int index=0; index < sTables.length; index++){
			tableMap.put(sTables[index], sColumns[index].split(","));
		}
	}

	public static SyncHelper getInstance() {
		if (_instance == null) {
			_instance = new SyncHelper();
		}

		return _instance;
	}

	private void getServerAddress() {
		String content = IOHelper.getInstance().readFile(CONFIG_FILE);

		if (content.length() != 0) {
			// Parse xml to data.
			return;
		}

		NAMESPACE = "http://tempuri.org/";
		URL = "http://10.0.2.2:1242/Service1.asmx";
	}

	private void getLocalLastSync() {
		String content = IOHelper.getInstance().readFile(CONFIG_FILE);

		if (content.length() != 0) {
			// Parse xml to data.
			return;
		}

		mLocalLastSync = DateTimeHelper.getDate(2012, 10, 11);
	}

	public void synchronize() {
		String LAST_SYNC_METHOD = "CheckLastSync";

		// Check this account is existing on Server or not.
		String results = String
				.valueOf(SyncHelper
						.getInstance()
						.invokeServerMethod(
								"Login",
								new String[] { "userName", "password" },
								new Object[] {
										AccountProvider.getInstance().currentAccount.name,
										AccountProvider
												.getInstance()
												.getPasswordByAccount(
														AccountProvider
																.getInstance().currentAccount) })
						.getProperty(0));

		// Get data if it exists.
		if (results != null) {
			boolean isExisting = Boolean.parseBoolean(results);

			if (isExisting) {
				SoapObject syncDate = SyncHelper
						.getInstance()
						.invokeServerMethod(
								LAST_SYNC_METHOD,
								new String[] { "username" },
								new Object[] { AccountProvider.getInstance().currentAccount.name });
				Date lastDateSync = Converter.toDate(
						String.valueOf(syncDate.getProperty(0)), "yyyy-MM-dd");

				if (mLocalLastSync.equals(lastDateSync)) {
					// Find records are modified after last date sync.
					for (String table : sTables) {
						getModifiedRecords(table,
								Converter.toString(mLocalLastSync));
					}
				} else {
					// Update data from server.
					//for (String table : sTables){
						SoapObject updatedRecords = invokeServerMethod("GetData", new String[] { "username",
								"tableName", "lastSyncTime" }, new Object[] {
								AccountProvider.getInstance().currentAccount.name,
								"Category", Converter.toString(mLocalLastSync) });
						
						SoapObject returnedValue = (SoapObject)updatedRecords.getProperty(0);
						if (returnedValue != null){
							for (int index = 0; index < returnedValue.getPropertyCount(); index++){
								SoapObject updatedValue = ((SoapObject)returnedValue.getProperty(index));
								String b = "";
								for (int id=0;id < updatedValue.getPropertyCount(); id++){
									String a = updatedValue.getProperty(id).toString();
									b += (a +  ", "); 
								}
								

								Alert.getInstance().show(PfmApplication.getAppContext(), b);
							}
						}				
					//}
				}
			} else {

			}
		}
	}

	private ArrayList<Object> getModifiedRecords(String table, String lastTime) {
		ArrayList<Object> records = new ArrayList<Object>();
		Cursor modifiedRecords = SqlHelper.instance.select(table, "*",
				new StringBuilder("ModifiedDate > ").append(lastTime)
						.toString());

		if (modifiedRecords != null && modifiedRecords.moveToFirst()) {
			do {
				// records.add(new )
			} while (modifiedRecords.moveToNext());
		}

		return records;
	}

	// / Invoke a function from web server.
	// / Return a value that server's method returned.
	public SoapObject invokeServerMethod(String methodName) {
		return invokeServerMethod(methodName, null, null);
	}

	public SoapObject invokeServerMethod(String methodName,
			String[] paramNames, Object[] params) {
		String SOAP_ACTIONS = new StringBuilder(NAMESPACE).append(methodName)
				.toString();

		SoapObject request = new SoapObject(NAMESPACE, methodName);

		if (paramNames != null && paramNames.length != 0) {
			for (int index = 0; index < paramNames.length; index++) {
				request.addProperty(paramNames[index], params[index]);
			}
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		HttpTransportSE androidhttpTranport = new HttpTransportSE(URL);
		try {
			androidhttpTranport.call(SOAP_ACTIONS, envelope);
			SoapObject reponse = (SoapObject) envelope.bodyIn;

			return reponse;
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "SyncHelper");
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			Logger.Log(e.getMessage(), "SyncHelper");
			e.printStackTrace();
		}

		return null;
	}
}
