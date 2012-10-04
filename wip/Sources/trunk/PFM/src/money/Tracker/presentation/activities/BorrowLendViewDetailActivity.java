package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BorrowLendViewDetailActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_lend_view_detail);
        
        TextView personName = (TextView) findViewById(R.id.borrow_lend_detail_view_name);
        TextView personPhone = (TextView) findViewById(R.id.borrow_lend_detail_view_phone);
        TextView personAddress = (TextView) findViewById(R.id.borrow_lend_detail_view_address);
        TextView total = (TextView) findViewById(R.id.borrow_lend_detail_view_total);
        TextView interest = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_rate);
        TextView interestType = (TextView) findViewById(R.id.borrow_lend_detail_view_interest_type);
        TextView startDate = (TextView) findViewById(R.id.borrow_lend_detail_view_start_date);
        TextView expriedDate = (TextView) findViewById(R.id.borrow_lend_detail_view_expired_date);
        Button cancelButton = (Button) findViewById(R.id.borrow_lend_detail_view_cancel_button);
        Button editButton = (Button) findViewById(R.id.borrow_lend_detail_view_edit_button);
        Button deleteButton = (Button) findViewById(R.id.borrow_lend_detail_view_delete_button);
        
        Bundle extras = getIntent().getExtras();
		final int borrow_lend_id = extras.getInt("borrowLendID");
		final boolean checkBorrowing = extras.getBoolean("checkBorrowing");
		
		final String tableName;
		if(checkBorrowing)
			tableName="Borrowing";
		else
			tableName="Lending";
				
		BorrowLendRepository bolere = new BorrowLendRepository();		
		BorrowLend values = bolere.getDetailData(tableName, "ID=" + borrow_lend_id);
		personName.setText(String.valueOf(values.getPersonName()));
		personPhone.setText(String.valueOf(values.getPersonPhone()));
		personAddress.setText(String.valueOf(values.getPersonAddress()));
		total.setText(String.valueOf(values.getMoney()));
		interest.setText(String.valueOf(values.getInterestRate()));
		interestType.setText(String.valueOf(values.getInterestType()));
		startDate.setText(Converter.toString(values.getStartDate(), "MMM dd, yyyy"));
		expriedDate.setText(Converter.toString(values.getExpiredDate(), "MMM dd, yyyy"));
		
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
				Alert.getInstance().showDialog(getParent(),
						"Delete selected schedule?", new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								SqlHelper.instance.delete(tableName, "ID = " + borrow_lend_id);
							}
						});
				
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
