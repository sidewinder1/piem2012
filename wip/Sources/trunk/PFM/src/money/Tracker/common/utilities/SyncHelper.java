package money.Tracker.common.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import money.Tracker.common.sql.SqlHelper;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
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
	private final static String[] sColumns = {
			"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, Start_date, End_date",
			"Id, CreatedDate, ModifiedDate, Budget, IsDeleted, Category_Id, Schedule_Id",
			"Id, CreatedDate, ModifiedDate, Category_Id, Name, IsDeleted,Money, Entry_Id",
			"Id, CreatedDate, ModifiedDate, IsDeleted,Date, Type",
			"ID, CreatedDate, ModifiedDate, IsDeleted, Debt_type, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_phone, Person_address",
			"Id, CreatedDate, ModifiedDate, Name, IsDeleted,User_Color" };
	private HashMap<String, String> tableMap = new HashMap<String, String>();
	private static SyncHelper _instance;

	public SyncHelper() {
		getServerAddress();
		getLocalLastSync();
		createDictionary();
	}

	private void createDictionary() {
		for (int index = 0; index < sTables.length; index++) {
			tableMap.put(sTables[index], sColumns[index]);
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
		String UPDATE_FROM_SERVER = "GetData";
		// TODO: hardcode table
		String CATEGORY_TABLE = "Category";
		// Check this account is existing on Server or not.
		String results = String
				.valueOf(invokeServerMethod(
						"Login",
						new String[] { "userName", "password" },
						new Object[] {
								AccountProvider.getInstance().currentAccount.name,
								AccountProvider
										.getInstance()
										.getPasswordByAccount(
												AccountProvider.getInstance().currentAccount) })
						.getProperty(0));

		// Get data if this account exists on server.
		if (results != null && Boolean.parseBoolean(results)) {
			SoapObject syncDate = SyncHelper
					.getInstance()
					.invokeServerMethod(
							LAST_SYNC_METHOD,
							new String[] { "userName" },
							new Object[] { AccountProvider.getInstance().currentAccount.name });
			Date lastDateSync = Converter.toDate(
					String.valueOf(syncDate.getProperty(0)),
					"yyyy-MM-dd hh:mm:ss");

			// Find records are modified after last date sync.
			for (String table : sTables) {
				invokeServerMethod(
						"SaveData",
						new String[] { "userName", "tableName", "data" },
						new Object[] {
								AccountProvider.getInstance().currentAccount.name,
								CATEGORY_TABLE,
								getModifiedRecords(CATEGORY_TABLE,
										Converter.toString(mLocalLastSync)) });
			}

			if (mLocalLastSync.before(lastDateSync)) {
				// Update data from server.
				for (String table : sTables) {
					SoapObject updatedRecords = invokeServerMethod(
							UPDATE_FROM_SERVER,
							new String[] { "userName", "tableName",
									"lastSyncTime" },
							new Object[] {
									AccountProvider.getInstance().currentAccount.name,
									CATEGORY_TABLE,
									Converter.toString(mLocalLastSync) });
					// returnedValue is a Array of array of string.
					SoapObject returnedValue = (SoapObject) updatedRecords
							.getProperty(0);
					if (returnedValue != null) {
						for (int index = 0; index < returnedValue
								.getPropertyCount(); index++) {
							SoapObject updatedValue = ((SoapObject) returnedValue
									.getProperty(index));
							if (updatedValue != null) {
								Cursor checkGlobalId = SqlHelper.instance
										.select(CATEGORY_TABLE,
												tableMap.get(CATEGORY_TABLE),
												new StringBuilder("Id = ")
														.append(updatedValue
																.getProperty(0))
														.toString());

								if (checkGlobalId != null
										&& checkGlobalId.moveToFirst()) {
									// Global Id exists, then check whether
									// update it or not.

								} else {
									// This Global Id doesn't exist, then
									// insert
									// it into local database.
									int length = updatedValue
											.getPropertyCount();
									String[] columnValues = new String[length];
									for (int i = 0; i < length; i++) {
										columnValues[i] = updatedValue
												.getProperty(i).toString();
									}

									SqlHelper.instance.insert(
											CATEGORY_TABLE,
											tableMap.get(CATEGORY_TABLE).split(
													","), columnValues);
								}
							}
						}
					}
				}
			}
		} else {
			// This account doesn't exist on server.
			// Then upload its data to server.

		}

		markAsSynchronized();
	}

	private void markAsSynchronized() {
		// TODO Auto-generated method stub

	}

	private SoapObject getModifiedRecords(String table, String lastTime) {
		//ArrayOfArraySerializer records = new ArrayOfArraySerializer();
		SoapObject records = new SoapObject();
		// ArrayList<ArrayList<String>> records = new
		// ArrayList<ArrayList<String>>();
		Cursor modifiedRecords = SqlHelper.instance.select(table,
				tableMap.get(table), new StringBuilder("ModifiedDate > '")
						.append(lastTime).append("'").toString());

		if (modifiedRecords != null && modifiedRecords.moveToFirst()) {
			do {
				SoapObject values = new SoapObject();
				for (int index = 0; index < modifiedRecords.getColumnCount(); index++) {
					PropertyInfo propertyInfo = new PropertyInfo();
					propertyInfo.setType(PropertyInfo.STRING_CLASS);
					propertyInfo.setName("anyType");
					propertyInfo.setValue(modifiedRecords.getString(index));
					values.addProperty(propertyInfo);
				}
				// StringArraySerializer values = new StringArraySerializer();
				PropertyInfo arrayOfArrayProperty = new PropertyInfo();
				arrayOfArrayProperty.setType(values.getClass());
				arrayOfArrayProperty.setName("ArrayOfAnyType");
				arrayOfArrayProperty.setValue(values);
				
				records.addProperty(arrayOfArrayProperty);
				// return propertyInfo;
			} while (modifiedRecords.moveToNext());
		}

//		PropertyInfo returnedPropertyInfo = new PropertyInfo();
//		returnedPropertyInfo.setType(records.getClass());
//		returnedPropertyInfo.setValue(records);
//		returnedPropertyInfo.setName("ArrayOfArray");
//		returnedPropertyInfo.setNamespace(NAMESPACE);
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

class StringArraySerializer extends Vector<String> implements KvmSerializable {

	String NAMESPACE = "http://tempuri.org/";

	public Object getProperty(int arg0) {
		return this.get(arg0);
	}

	public int getPropertyCount() {
		return this.size();
	}

	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		arg2.setName("string");
		arg2.setType(PropertyInfo.STRING_CLASS);
		arg2.setNamespace(NAMESPACE);
	}

	public void setProperty(int arg0, Object arg1) {
		this.add(arg1.toString());
	}
}

class ArrayOfArraySerializer extends Vector<PropertyInfo> implements
		KvmSerializable {
	String NAMESPACE = "http://tempuri.org/";

	public Object getProperty(int arg0) {
		return this.get(arg0);
	}

	public int getPropertyCount() {
		return this.size();
	}

	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		arg2.setName("ArrayOfString");
		arg2.setType(PropertyInfo.VECTOR_CLASS);
		arg2.setNamespace(NAMESPACE);
	}

	public void setProperty(int arg0, Object arg1) {
		this.add((PropertyInfo) arg1);
	}

}