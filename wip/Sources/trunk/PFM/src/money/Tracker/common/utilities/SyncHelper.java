package money.Tracker.common.utilities;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import money.Tracker.common.sql.SqlHelper;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.database.Cursor;

public class SyncHelper {
	String NAMESPACE = "http://pfm.org/";
	String URL = "http://54.251.59.102:83/PFMService.asmx";
	private Date mLocalLastSync;

	private final static String[] sTables = { "Category", "Schedule",
			"ScheduleDetail", "Entry", "EntryDetail", "BorrowLend" };
	private final static String[] sColumns = {
			"Id, CreatedDate, ModifiedDate, Name, IsDeleted,User_Color",
			"Id, CreatedDate, ModifiedDate, Budget, Type, IsDeleted, Start_date, End_date",
			"Id, CreatedDate, ModifiedDate, Budget, IsDeleted, Category_Id, Schedule_Id",
			"Id, CreatedDate, ModifiedDate, IsDeleted,Date, Type",
			"Id, CreatedDate, ModifiedDate, Category_Id, Name, IsDeleted,Money, Entry_Id",
			"ID, CreatedDate, ModifiedDate, IsDeleted, Debt_type, Money, Interest_type, Interest_rate, Start_date, Expired_date, Person_name, Person_phone, Person_address" };
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
		String nameSpace = XmlParser.getInstance()
				.getConfigContent("namespace");
		String url = XmlParser.getInstance().getConfigContent("url");
		if (nameSpace.length() != 0) {
			NAMESPACE = nameSpace;
		}

		if (url.length() != 0) {
			URL = url;
			// URL = "http://10.0.2.2:1242/PFMService.asmx";
		}

	}

	private void getLocalLastSync() {
		// String lastSync =
		// XmlParser.getInstance().getConfigContent("lastSync");
		Cursor lastSyncCursor = SqlHelper.instance
				.select("AppInfo", "LastSync",
						"UserName ='"
								+ AccountProvider.getInstance()
										.getCurrentAccount().name + "'", true);
		if (lastSyncCursor != null && lastSyncCursor.moveToFirst()) {
			mLocalLastSync = Converter.toDate(lastSyncCursor.getString(0));
			return;
		}

		mLocalLastSync = DateTimeHelper.getDate(1990, 0, 20);
	}

	public void synchronize() {
		getLocalLastSync();

		String LAST_SYNC_METHOD = "CheckLastSync";
		String UPDATE_FROM_SERVER = "GetData";
		String SAVE_DATA_METHOD = "SaveData";
		String LOGIN_METHOD = "Login";
		String[] SAVE_PARAMS = new String[] { "userName", "tableName", "data" };
		String[] UPDATE_PARAMS = new String[] { "userName", "tableName",
				"lastSyncTime" };
		for (String table : sTables) {
			if (!"pfm.com".equals(AccountProvider.getInstance()
					.getCurrentAccount().type)) {
				// Replace all local data to synchronized data.
				SqlHelper.instance.update(table, new String[] { "UserName" },
						new String[] { AccountProvider.getInstance()
								.getCurrentAccount().name },
						"UserName='LocalAccount'");
			}
		}
		
		Logger.Log("Login: "
				+ AccountProvider.getInstance().getCurrentAccount().name
				+ ", \r\nUrl: " + URL, "SyncHelper");

		// Check this account is existing on Server or not.
		SoapObject loginRequest = invokeServerMethod(LOGIN_METHOD,
				new String[] { "userName" }, new Object[] { AccountProvider
						.getInstance().getCurrentAccount().name });
		Logger.Log("Login success but cannot get result.", "SyncHelper");
		if (loginRequest == null) {
			return;
		}

		Logger.Log("Login success", "SyncHelper");
		String results = loginRequest.getPropertyAsString(0);

		// Get data if this account exists on server.
		if (results != null && Boolean.parseBoolean(results)) {
			SoapObject syncDate = SyncHelper.getInstance().invokeServerMethod(
					LAST_SYNC_METHOD,
					new String[] { "userName" },
					new Object[] { AccountProvider.getInstance()
							.getCurrentAccount().name });
			if (syncDate == null) {
				return;
			}

			Logger.Log(
					"Server last sync: "
							+ String.valueOf(syncDate.getProperty(0)),
					"SyncHelper");

			Date lastDateSync = Converter.toDate(
					String.valueOf(syncDate.getPropertyAsString(0).replace('T',
							' ')), "yyyy-MM-dd hh:mm:ss");

			// Find records are modified after last date sync.
			for (String table : sTables) {
				Logger.Log("Saving data to server: " + table, "SyncHelper");
				SoapObject saveDataResult = invokeServerMethod(
						SAVE_DATA_METHOD,
						SAVE_PARAMS,
						new Object[] {
								AccountProvider.getInstance()
										.getCurrentAccount().name,
								table,
								getModifiedRecords(table,
										Converter.toString(mLocalLastSync)) });
				Logger.Log("Saved data to server " + table
						+ (saveDataResult == null ? " un" : "")
						+ "successfully", "SyncHelper");
			}

			Logger.Log(
					"mLocalLastSync.before(lastDateSync): "
							+ mLocalLastSync.before(lastDateSync), "SyncHelper");
			if (mLocalLastSync.before(lastDateSync)) {
				// Update data from server.
				for (String table : sTables) {
					SoapObject updatedRecords = invokeServerMethod(
							UPDATE_FROM_SERVER, UPDATE_PARAMS, new Object[] {
									AccountProvider.getInstance()
											.getCurrentAccount().name, table,
									Converter.toString(mLocalLastSync) });
					Logger.Log("Update data from server: " + table,
							"SyncHelper");
					if (updatedRecords == null) {
						continue;
					}

					// returnedValue is a Array of array of string.
					SoapObject returnedValue = (SoapObject) updatedRecords
							.getProperty(0);
					if (returnedValue == null) {
						continue;
					}

					SoapObject savedObject = new SoapObject();
					for (int index = 0; index < returnedValue
							.getPropertyCount(); index++) {
						SoapObject updatedValue = ((SoapObject) returnedValue
								.getProperty(index));

						if (updatedValue == null) {
							continue;
						}

						Cursor checkGlobalId = SqlHelper.instance.select(table,
								tableMap.get(table), new StringBuilder("Id = ")
										.append(updatedValue.getProperty(0))
										.toString(), true);

						if (checkGlobalId != null
								&& checkGlobalId.moveToFirst()) {
							// Global Id exists, then check whether
							// update it or not.
							if (checkGlobalId.getString(2).compareTo(
									updatedValue.getProperty(2).toString()) > 0) {
								// Save data into server's db.
								SoapObject values = new SoapObject();
								for (int j = 0; j < checkGlobalId
										.getColumnCount(); j++) {
									values.addProperty(createPropertyInfo(checkGlobalId
											.getString(j)));
								}

								PropertyInfo arrayOfArrayProperty = new PropertyInfo();
								arrayOfArrayProperty.setType(values.getClass());
								arrayOfArrayProperty.setName("ArrayOfAnyType");
								arrayOfArrayProperty.setValue(values);

								savedObject.addProperty(arrayOfArrayProperty);
							} else if (checkGlobalId.getString(2).compareTo(
									updatedValue.getProperty(2).toString()) < 0) {
								// update data.
								int length = updatedValue.getPropertyCount();
								String[] columnValues = new String[length];

								for (int i = 0; i < length; i++) {
									columnValues[i] = updatedValue
											.getPropertyAsString(i);
								}

								SqlHelper.instance
										.update(table,
												tableMap.get(table).split(","),
												columnValues,
												new StringBuilder("Id = ")
														.append(updatedValue
																.getPropertyAsString(0))
														.toString());
							}
						} else {
							// This Global Id doesn't exist, then
							// insert
							// it into local database.
							int length = updatedValue.getPropertyCount();
							String[] columnValues = new String[length];
							for (int i = 0; i < length; i++) {
								columnValues[i] = updatedValue.getProperty(i)
										.toString();
							}
							Logger.Log(
									"Insert to local DB: "
											+ columnValues.toString(),
									"SyncHelper");
							SqlHelper.instance.insert(table, tableMap
									.get(table).split(","), columnValues);
						}
					}

					if (savedObject.getPropertyCount() != 0) {
						// Upload data to server.
						invokeServerMethod(SAVE_DATA_METHOD, SAVE_PARAMS,
								new Object[] {
										AccountProvider.getInstance()
												.getCurrentAccount().name,
										table, savedObject });
					}
				}
			}
		} else {
			// This account doesn't exist on server.
			// Then upload its data to server.
			// Need to call create account method.

			for (String table : sTables) {
				invokeServerMethod(
						SAVE_DATA_METHOD,
						SAVE_PARAMS,
						new Object[] {
								AccountProvider.getInstance()
										.getCurrentAccount().name,
								table,
								getModifiedRecords(table,
										Converter.toString(mLocalLastSync)) });
			}
		}

		markAsSynchronized();
		// Alert.getInstance().show(
		// PfmApplication.getAppContext(),
		// PfmApplication.getAppResources().getString(
		// R.string.sync_success));
		Logger.Log("Mark as Synchronize", "SyncHelper");
	}

	private void markAsSynchronized() {
		String lastSyncTime = Converter.toString(DateTimeHelper.now(true));
		invokeServerMethod("MarkSynchronized", new String[] { "userName",
				"syncTime" }, new Object[] {
				AccountProvider.getInstance().getCurrentAccount().name,
				lastSyncTime });
		SqlHelper.instance.update("AppInfo", new String[] { "LastSync" },
				new String[] { lastSyncTime }, "");
		// XmlParser.getInstance().setConfigContent("lastSync", lastSyncTime);
	}

	private SoapObject getModifiedRecords(String table, String lastTime) {
		SoapObject records = new SoapObject();
		Cursor modifiedRecords = SqlHelper.instance.select(table,
				tableMap.get(table), new StringBuilder("ModifiedDate > '")
						.append(lastTime).append("'").toString(), true);

		if (modifiedRecords != null && modifiedRecords.moveToFirst()) {
			do {
				SoapObject values = new SoapObject();
				for (int index = 0; index < modifiedRecords.getColumnCount(); index++) {
					values.addProperty(createPropertyInfo(modifiedRecords
							.getString(index)));
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

		return records;
	}

	/**
	 * @param modifiedRecords
	 * @param index
	 * @return
	 */
	private PropertyInfo createPropertyInfo(String value) {
		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setType(PropertyInfo.STRING_CLASS);
		propertyInfo.setName("anyType");
		propertyInfo.setValue(value);
		return propertyInfo;
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
		Logger.Log("envelope.setOutputSoapObject(request);", "SyncHelper");
		HttpTransportSE androidhttpTranport = new HttpTransportSE(URL);
		Logger.Log(
				"HttpTransportSE androidhttpTranport = new HttpTransportSE(URL);",
				"SyncHelper");
		try {
			Logger.Log("SOAP: " + SOAP_ACTIONS, "SyncHelper");
			androidhttpTranport.call(SOAP_ACTIONS, envelope);
			Logger.Log(envelope + "," + envelope.bodyIn, "SyncHelper");
			SoapObject reponse = (SoapObject) envelope.bodyIn;
			Logger.Log("return success", "SyncHelper");
			return reponse;
		} catch (IOException e) {
			Logger.Log("IOException", "SyncHelper");
		} catch (XmlPullParserException e) {
			Logger.Log("XmlPullParserException", "SyncHelper");
		} catch (ClassCastException e) {
			Logger.Log("ClassCastException", "SyncHelper");
		}

		// Alert.getInstance().show(PfmApplication.getAppContext(),
		// PfmApplication.getAppResources().getString(R.string.sync_fail) + ": "
		// + methodName);
		return null;
	}
}