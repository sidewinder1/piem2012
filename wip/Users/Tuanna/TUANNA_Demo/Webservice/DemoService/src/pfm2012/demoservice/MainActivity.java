package pfm2012.demoservice;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static String SOAP_ACTION = "";

    private static String METHOD_NAME = "";

    private static String NAMESPACE = "http://tempuri.org/";
    private static String URL = "http://10.0.2.2:13735/VLocationService.asmx";
    TextView tv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) findViewById(R.id.scan_result);
		
		Button callHelloWorldButton = (Button) findViewById(R.id.call_hello_world);
		callHelloWorldButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				METHOD_NAME = "HelloWorld";
				SOAP_ACTION = NAMESPACE + METHOD_NAME;
				
				callHelloWorld();
			}
		});
		
		Button callGetCategoryButton = (Button) findViewById(R.id.call_get_category_id);
		final EditText numberIDEditText = (EditText) findViewById(R.id.number_id_edit_text);
		callGetCategoryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int categoryID = Integer.parseInt(numberIDEditText.getText().toString());
				
				METHOD_NAME = "GetCategory";
				SOAP_ACTION = NAMESPACE + METHOD_NAME;
				
				callGetCategory(categoryID);
			}
		});
		
		Button callInsertCategoryButton = (Button) findViewById(R.id.call_insert_category);
		final EditText nameEditText = (EditText) findViewById(R.id.name_edit_text);
		callInsertCategoryButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nameCategory = nameEditText.getText().toString();
				
				METHOD_NAME = "InsertCategory";
				SOAP_ACTION = NAMESPACE + METHOD_NAME;
				
				callInsertCategory(nameCategory);
			}
		});
		
		Button callGetCategoriesButton = (Button) findViewById(R.id.call_get_category);
		callGetCategoriesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				METHOD_NAME = "GetCategories";
				SOAP_ACTION = NAMESPACE + METHOD_NAME;
				
				callGetCategories();
			}
		});
	}
	
	private void callGetCategories()
	{
		try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            
            SoapObject respone = (SoapObject)envelope.getResponse();
            //respone = (SoapObject)respone.getProperty(1);
            ArrayList<Category> categoryList = new ArrayList<Category>();            
            String result = "";
            
            for (int i = 0; i < respone.getPropertyCount(); i++)
            {
            	SoapObject tableRow = (SoapObject) respone.getProperty(i);
            	
	            Object categoryID = tableRow.getProperty("ID");
	            Object name = tableRow.getProperty("Name");
	            Object creationDate = tableRow.getProperty("CreatedDate");
	            
	            result += categoryID.toString() + " : " + name.toString() + " : " + creationDate.toString();
            }

            tv.setText(result);
        } catch (Exception e) {
            tv.setText(e.getMessage());
            }

	}
	
	private void callInsertCategory(String nameCategory)
	{
		try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //request.addProperty("passonString", "Rajapandian");
            PropertyInfo categoryInfo = new PropertyInfo();
            categoryInfo.setName("name");
            categoryInfo.setValue(nameCategory);
            categoryInfo.setType(String.class);
            request.addProperty(categoryInfo);
            
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            
            SoapObject respone = (SoapObject)envelope.getResponse();
            Object categoryID = respone.getProperty(0);
            Object name = respone.getProperty(1);
            Object creationDate = respone.getProperty(2);

            tv.setText(categoryID.toString() + " : " + name.toString() + " : " + creationDate.toString());
        } catch (Exception e) {
            tv.setText(e.getMessage());
            }
	}
	
	private void callGetCategory(int CategoryID)
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //request.addProperty("passonString", "Rajapandian");
            PropertyInfo categoryIDInfo = new PropertyInfo();
            categoryIDInfo.setName("id");
            categoryIDInfo.setValue(CategoryID);
            categoryIDInfo.setType(int.class);
            request.addProperty(categoryIDInfo);
            
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            
            SoapObject respone = (SoapObject)envelope.getResponse();
            Object error = respone.getProperty(0);
            //Object name = respone.getProperty(1);
            //Object creationDate = respone.getProperty(2);
            if(error == null)
            	tv.setText("Insert successfully");
            else
            	tv.setText("Insert failed");
        } catch (Exception e) {
            tv.setText(e.getMessage());
            }
    }
	
	private void callHelloWorld()
    {
        try {

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //request.addProperty("passonString", "Rajapandian");

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet=true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
            androidHttpTransport.call(SOAP_ACTION, envelope);

            Object result = (Object)envelope.getResponse();

            tv.setText(result.toString());
        } catch (Exception e) {
            tv.setText(e.getMessage());
            }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
