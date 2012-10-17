package com.graph;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class GraphActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_graph, menu);
        return true;
    }
    
    public void actionClicked(View view)
    {
    	RadioButton lineRadio = (RadioButton)findViewById(R.id.lineRadio);
    	EditText dataEditText = (EditText)findViewById(R.id.dataEdit);
    	EditText nameEditText = (EditText)findViewById(R.id.nameEdit);
    	AlertDialog dialog = new AlertDialog.Builder(this).create();
    	
    	/*
    	if (dataEditText.getText()+ "" == "")
    	{
    		dialog.setTitle("Message");
    		dialog.setMessage("Data field must be not empty");
    		dialog.setButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
			});
    		dialog.show();
    		return;
    	}  	*/
    	
    	if (lineRadio.isChecked())
    	{
        	LineGraph line = new LineGraph();
        	line.setNames(nameEditText.getText().toString());
        	
        	try {
        		line.setData(dataEditText.getText().toString());
    		} catch (Exception e) {
    			// TODO: handle exception
    			return;
    		}
        	Intent lineIntent = line.getIntent(this);
        	startActivity(lineIntent);
        	return;
    	}
    	
    	PieGraph pie = new PieGraph();
    	
    	pie.setNames(nameEditText.getText().toString());
    	try {
    		pie.setData(dataEditText.getText().toString());
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
    	Intent pieIntent = pie.getIntent(this);
    	startActivity(pieIntent);
    }
}
