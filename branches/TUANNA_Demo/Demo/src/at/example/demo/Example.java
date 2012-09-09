package at.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract.Intents.Insert;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.view.Menu;

public class Example extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      //Thiết lập giao diện lấy từ file main.xml
        setContentView(R.layout.main);
        
        //Lấy về các thành phần trong main.xml thông qua id
        final EditText edit = (EditText) findViewById(R.id.textName);
        final TextView text = (TextView) findViewById(R.id.textView);
        final Button button = (Button) findViewById(R.id.button1);
        
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(/*Home.this*/ getApplicationContext(), Hello.class);  
				startActivity(i);
				
			}
		});
        
        //Thiết lập xử lý cho sự kiện nhấn nút giữa của điện thoại
        edit.setOnKeyListener(new OnKeyListener() {            
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN 
                        && keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    text.setText("Welcome " + edit.getText().toString() + "to Android OS");
                    edit.setText("");
                    return true;                    
                }
                else {
                    return false;
                }
            }
            
        });

    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
