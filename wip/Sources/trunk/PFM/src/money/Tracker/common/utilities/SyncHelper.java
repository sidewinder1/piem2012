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
	String NAMESPACE = "http://tempuri.org/";
	String URL = "http://10.0.2.2:1242/PFMService.asmx";
	
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
		String nameSpace = XmlParser.getInstance().getConfigContent("namespace");
		String url = XmlParser.getInstance().getConfigContent("url");
		if (nameSpace.length() != 0){
			NAMESPACE = nameSpace;
		}
		
		if (url.length() != 0){
			URL = url;
		}
		
	}

	private void getLocalLastSync() {
		String lastSync = XmlParser.getInstance().getConfigContent("lastSync");

		if (lastSync.length() != 0) {
			// Parse xml to data.
			mLocalLastSync = Converter.toDate(lastSync);
			return;
		}

		mLocalLastSync = DateTimeHelper.getDate(2012, 10, 11);
	}

	public void synchronize() {
		String LAST_SYNC_METHOD = "CheckLastSync";
		String UPDATE_FROM_SERVER = "GetData";
		String SAVE_DATA_METHOD = "SaveData";
		String LOGIN_METHOD = "Login";
		String[] SAVE_PARAMS = new String[] { "userName", "tableName", "data" };
		String[] UPDATE_PARAMS = new String[] { "userName", "tableName",
				"lastSyncTime" };

		// Check this account is existing on Server or not.
		SoapObject loginRequest = invokeServerMethod(
				LOGIN_METHOD,
				new String[] { "userName"},
				new Object[] {
						AccountProvider.getInstance().currentAccount.name });
		if (loginRequest == null) {
			return;
		}
		String results = loginRequest.getPropertyAsString(0);

		// Get data if this account exists on server.
		if (results != null && Boolean.parseBoolean(results)) {
			SoapObject syncDate = SyncHelper
					.getInstance()
					.invokeServerMethod(
							LAST_SYNC_METHOD,
							new String[] { "userName" },
							new Object[] { AccountProvider.getInstance().currentAccount.name });
			if (syncDate == null) {
				return;
			}

			Date lastDateSync = Converter.toDate(
					String.valueOf(syncDate.getProperty(0)),
					"yyyy-MM-dd hh:mm:ss");

			// Find records are modified after last date sync.
			for (String table : sTables) {
				invokeServerMethod(
						SAVE_DATA_METHOD,
						SAVE_PARAMS,
						new Object[] {
								AccountProvider.getInstance().currentAccount.name,
								table,
								getModifiedRecords(table,
										Converter.toString(mLocalLastSync)) });
			}

			if (mLocalLastSync.before(lastDateSync)) {
				// Update data from server.
				for (String table : sTables) {
					SoapObject updatedRecords = invokeServerMethod(
							UPDATE_FROM_SERVER,
							UPDATE_PARAMS,
							new Object[] {
									AccountProvider.getInstance().currentAccount.name,
									table, Converter.toString(mLocalLastSync) });

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
										.toString());

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

							SqlHelper.instance.insert(table, tableMap
									.get(table).split(","), columnValues);
						}
					}

					if (savedObject.getPropertyCount() != 0) {
						// Upload data to server.
						invokeServerMethod(
								SAVE_DATA_METHOD,
								SAVE_PARAMS,
								new Object[] {
										AccountProvider.getInstance().currentAccount.name,
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
								AccountProvider.getInstance().currentAccount.name,
								table,
								getModifiedRecords(table,
										Converter.toString(mLocalLastSync)) });
			}
		}

		markAsSynchronized();
	}

	private void markAsSynchronized() {
		invokeServerMethod(
				"MarkSynchronized",
				new String[] { "userName" },
				new Object[] { AccountProvider.getInstance().currentAccount.name });
		XmlParser.getInstance().setConfigContent("lastSync",
				Converter.toString(DateTimeHelper.now()));
	}

	private SoapObject getModifiedRecords(String table, String lastTime) {
		SoapObject records = new SoapObject();
		Cursor modifiedRecords = SqlHelper.instance.select(table,
				tableMap.get(table), new StringBuilder("ModifiedDate > '")
						.append(lastTime).append("'").toString());

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
		HttpTransportSE androidhttpTranport = new HttpTransportSE(URL);
		try {
			androidhttpTranport.call(SOAP_ACTIONS, envelope);
			SoapObject reponse = (SoapObject) envelope.bodyIn;

			return reponse;
		} catch (IOException e) {
			Logger.Log(e.getMessage(), "SyncHelper");
		} catch (XmlPullParserException e) {
			Logger.Log(e.getMessage(), "SyncHelper");
		} catch (ClassCastException e) {
			Logger.Log(e.getMessage(), "SyncHelper");
		}

		return null;
	}
}
