package money.Tracker.presentation.activities;

import java.util.TooManyListenersException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class BorrowLendInsertActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_lend_insert);
        
        // Hand on Save button
        final Button saveButton = (Button) findViewById(R.id.saveButton);
        
        saveButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        
        // Hand on Cancel button
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
        
        // Hand on debt type
        final TextView debtTypeTextView = (TextView) findViewById(R.id.title_text_view);
        final ToggleButton debtTypeButton = (ToggleButton) findViewById(R.id.borrowLendType);
        
        debtTypeButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (debtTypeButton.isChecked())
				{
					debtTypeTextView.setText("New Borrowing");
				}
				else
				{
					debtTypeTextView.setText("New Lending");
				}
			}
		});
    }
    
    private void getData()
    {
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_lend_insert, menu);
        return true;
    }
}
