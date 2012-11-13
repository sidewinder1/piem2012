package money.Tracker.common.utilities;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

public class SyncHelper {
	String NAMESPACE = "http://tempuri.org/";
	String URL = "http://10.0.2.2:1242/Service1.asmx";
	String CONFIG_FILE = "Fpm/FpmConfig.cnfg";

	private static SyncHelper _instance;
	
	public SyncHelper(){
		getServerAddress();
	}
	
	public static SyncHelper getInstance(){
		if (_instance == null){
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

	/// Invoke a function from web server.
	/// Return a value that server's method returned.
	public SoapObject invokeServerMethod(String methodName){
		return invokeServerMethod(methodName, null, null);
	}
	
	public SoapObject invokeServerMethod(String methodName, String [] paramNames, Object[] params) {
		String SOAP_ACTIONS = new StringBuilder(NAMESPACE).append(
				methodName).toString();

		SoapObject request = new SoapObject(NAMESPACE, methodName);
		
		if (paramNames != null && paramNames.length != 0){
			for (int index = 0; index < paramNames.length; index++){
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
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return null;
	}
}
