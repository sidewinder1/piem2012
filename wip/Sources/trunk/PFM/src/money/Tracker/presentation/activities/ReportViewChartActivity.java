package money.Tracker.presentation.activities;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class ReportViewChartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view_chart);
        
        Log.d("Check main chart", "Check 0");
        
        Bundle extras = getIntent().getExtras();
        Log.d("Check main chart", "Check 1");
		boolean checkMonthly = extras.getBoolean("checkMonthly");
		Log.d("Check main chart", "Check 2");
		String startDate = extras.getString("start_date");
		Log.d("Check main chart", "Check 3");
		String endDate = extras.getString("end_date");
		
		Log.d("Check main chart", "Check 4");
		
        Intent reportMainViewChart = new Intent(this, ReportMainViewChartActivity.class);
        Log.d("Check main chart", "Check 5");
        reportMainViewChart.putExtra("checkMonthly", checkMonthly);
        Log.d("Check main chart", "Check 6 " + checkMonthly);
        reportMainViewChart.putExtra("start_date", startDate);
        Log.d("Check main chart", "Check 7 " + startDate);
        reportMainViewChart.putExtra("end_date", endDate);
        Log.d("Check main chart", "Check 8 " + endDate);
        startActivity(reportMainViewChart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_report_view_chart, menu);
        return true;
    }
}
