package pfm.capstone.project;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import pfm.database.*;

public class Input extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        Button cancelButton = (Button) findViewById(R.id.buttonCancel);
        
        final Spinner typeSpinner = (Spinner) findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.BLType, android.R.layout.simple_spinner_dropdown_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        typeSpinner.setAdapter(adapter);
        
        final EditText nameExitText = (EditText) findViewById(R.id.editTextName);
        final EditText totalEditText = (EditText) findViewById(R.id.editTextTotal);
        final EditText interestEditText = (EditText) findViewById(R.id.editTextInterest);
        final EditText startDateEditText = (EditText) findViewById(R.id.editTextStartDate);
        final EditText endDateEditText = (EditText) findViewById(R.id.editTextEndDate);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int total = Integer.parseInt(totalEditText.getText().toString());
				int interest = Integer.parseInt(interestEditText.getText().toString());
				String name = nameExitText.getText().toString();
				String startDate = startDateEditText.getText().toString();
				String endDate = endDateEditText.getText().toString();
				
				InputType it = new InputType("", name, total, interest, startDate, endDate);
				
				DatabaseHandler dh = new DatabaseHandler(Input.this);
				dh.insertData(it);
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_input, menu);
        return true;
    }
}
