package money.Tracker.presentation.activities;

import java.util.ArrayList;

import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.model.BorrowLend;
import money.Tracker.repository.BorrowLendRepository;

import android.os.Bundle;
import android.app.Activity;
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
        Button cancelButton = (Button) findViewById(R.id.view_detail_cancel_button);
        
        Bundle extras = getIntent().getExtras();
		int borrow_lend_id = extras.getInt("borrowLendID");
		boolean checkBorrowing = extras.getBoolean("checkBorrowing");
		
		String tableName;
		if(checkBorrowing)
			tableName="Borrowing";
		else
			tableName="Lending";
		
		Log.d("Deatil", "Check 1");
		BorrowLendRepository bolere = new BorrowLendRepository();
		Log.d("Deatil", "Check 2");
		BorrowLend values = bolere.getDetailData(tableName, "ID=" + borrow_lend_id);
		Log.d("Deatil", "Check 3");
		personName.setText(String.valueOf(values.getPersonName()));
		Log.d("Deatil", "Check 4");
		personPhone.setText(String.valueOf(values.getPersonPhone()));
		Log.d("Deatil", "Check 5");
		personAddress.setText(String.valueOf(values.getPersonAddress()));
		Log.d("Deatil", "Check 6");
		total.setText(String.valueOf(values.getMoney()));
		Log.d("Deatil", "Check 7");
		interest.setText(String.valueOf(values.getInterestRate()));
		Log.d("Deatil", "Check 8");
		interestType.setText(String.valueOf(values.getInterestType()));
		Log.d("Deatil", "Check 9");
		startDate.setText(Converter.toString(values.getStartDate(), "MMM dd, yyyy"));
		Log.d("Deatil", "Check 10");
		expriedDate.setText(Converter.toString(values.getExpiredDate(), "MMM dd, yyyy"));
		Log.d("Deatil", "Check 11");
		
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
