package money.Tracker.presentation.activities;

//import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
//import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BorrowLendViewDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("View Detail", "Check 00");
        setContentView(R.layout.activity_borrow_lend_view_detail);
        Log.d("View Detail", "Check 001");
        TextView personName = (TextView) findViewById(R.id.borrow_lend_detail_view_name);
        Log.d("View Detail", "Check 01");
        TextView personPhone = (TextView) findViewById(R.id.borrow_lend_detail_view_phone);
        TextView personAddress = (TextView) findViewById(R.id.borrow_lend_detail_view_address);
        TextView total = (TextView) findViewById(R.id.borrow_lend_detail_view_total);
        TextView interest = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_rate);
        TextView interestType = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_type);
        TextView startDate = (TextView) findViewById(R.id.borrow_lend_detail_view_start_date);
        TextView expriedDate = (TextView) findViewById(R.id.borrow_lend_detail_view_expired_date);
        Log.d("View Detail", "Check 02");
        Button cancelButton = (Button) findViewById(R.id.borrow_lend_detail_view_cancel_button);
        Log.d("View Detail", "Check 03");
        Button editButton = (Button) findViewById(R.id.borrow_lend_detail_view_edit_button);
        Log.d("View Detail", "Check 04");
        Button deleteButton = (Button) findViewById(R.id.borrow_lend_detail_view_delete_button);
        
        Log.d("View Detail", "Check 1");
        Bundle extras = getIntent().getExtras();
		final int borrow_lend_id = extras.getInt("borrowLendID");
		final boolean checkBorrowing = extras.getBoolean("checkBorrowing");
		
		Log.d("View Detail", "Check 2");
		final String tableName;
		if(checkBorrowing)
			tableName="Borrowing";
		else
			tableName="Lending";
		Log.d("View Detail", "Check 3");
		BorrowLendRepository bolere = new BorrowLendRepository();		
		BorrowLend values = bolere.getDetailData(tableName, "ID=" + borrow_lend_id);
		Log.d("View Detail", "Check 4");
		personName.setText(String.valueOf(values.getPersonName()));
		personPhone.setText(String.valueOf(values.getPersonPhone()));
		personAddress.setText(String.valueOf(values.getPersonAddress()));
		total.setText(String.valueOf(values.getMoney()));
		interest.setText(String.valueOf(values.getInterestRate()));
		interestType.setText(String.valueOf(values.getInterestType()));
		Log.d("View Detail", String.valueOf(values.getStartDate()));
		startDate.setText(Converter.toString(values.getStartDate(), "MMM dd, yyyy"));
		Log.d("View Detail", String.valueOf(values.getExpiredDate()));
		if (values.getExpiredDate() != null)
		{
			expriedDate.setText(Converter.toString(values.getExpiredDate(), "MMM dd, yyyy"));
		}
		else
		{
			expriedDate.setText("");
		}
		Log.d("View Detail", "Check 5");
		
		// Handle edit button
		editButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent borrowLendEdit = new Intent(BorrowLendViewDetailActivity.this, BorrowLendInsertActivity.class);
				borrowLendEdit.putExtra("checkBorrowing", checkBorrowing);
				borrowLendEdit.putExtra("borrowLendID", borrow_lend_id);
				startActivity(borrowLendEdit);
			}
		});
		
		// Handle delete button
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getApplicationContext());
				 
		        // Setting Dialog Title
		        alertDialog.setTitle("Confirm Delete...");
		 
		        // Setting Dialog Message
		        alertDialog.setMessage("Are you sure you want delete this?");
		 
		        // Setting Positive "Yes" Button
		        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog,int which) {
		 
		            // Write your code here to invoke YES event
		            	SqlHelper.instance.delete(tableName, "ID = " + borrow_lend_id);
		            }
		        });
		 
		        // Setting Negative "NO" Button
		        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            // Write your code here to invoke NO event
		            dialog.cancel();
		            }
		        });
		 
		        // Showing Alert Message
		        alertDialog.show();
				
				BorrowLendViewDetailActivity.this.finish();
			}
		});
		
		// Handle cancel button
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				BorrowLendViewDetailActivity.this.finish();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_borrow_lend_view_detail, menu);
        return true;
    }
}
