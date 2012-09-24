package pfm.demosqlite;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import pfm.database.*;

public class Home extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        final DatabaseHandler dh = new DatabaseHandler(getApplicationContext());
        final Button buttonShow = (Button) findViewById(R.id.button1);
        final Button buttonInsert = (Button) findViewById(R.id.button2);
        final Button buttonUpdate = (Button) findViewById(R.id.button3);
        final Button buttonDelete = (Button) findViewById(R.id.button4);
        final TextView textView = (TextView) findViewById(R.id.textView1);
        final EditText editText = (EditText) findViewById(R.id.editText1);
        
        ArrayList<String> arr = dh.getData();
        
        if (arr.isEmpty())
        {
        	textView.setText("No data");
        }
        else
        {
        	Object[] mStringArray = arr.toArray();
        	
        	for(int i = 0; i < mStringArray.length ; i++){
        	    textView.setText(mStringArray[i].toString());
        	}
        }
        
        buttonInsert.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String str = editText.getText().toString();
				dh.insertData(str);
			}
		});
        
        buttonShow.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<String> arr = dh.getData();
		        
		        if (arr.isEmpty())
		        {
		        	textView.setText("No data");
		        }
		        else
		        {
		        	Object[] mStringArray = arr.toArray();
		        	
		        	for(int i = 0; i < mStringArray.length ; i++){
		        	    textView.setText(mStringArray[i].toString());
		        	}
		        }
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
}
