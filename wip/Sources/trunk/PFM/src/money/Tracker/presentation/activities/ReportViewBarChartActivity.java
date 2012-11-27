package money.Tracker.presentation.activities;

import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewBarChartActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view_bar_chart);
        
        Bundle extras = getIntent().getExtras();
        final boolean checkMonthly = extras.getBoolean("checkMonthly");
		String startDate = extras.getString("start_date");
		String endDate = extras.getString("end_date");
		
		TextView buttonBarChartCompare = (TextView) findViewById(R.id.report_bar_chart_button_compare_title);
		LinearLayout barChartButton = (LinearLayout) findViewById(R.id.report_bar_chart_button_compare);
		LinearLayout barChart = (LinearLayout) findViewById(R.id.report_bar_chart);
		
		if (checkMonthly)
		{
			buttonBarChartCompare.setText("So sánh giữa các tháng");
		} else
		{
			buttonBarChartCompare.setText("So sánh giữa các tuần");
		}
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Chart chart = new Chart(checkMonthly, Converter.toDate(startDate), Converter.toDate(endDate));
		barChart.addView(chart.getBarIntent(this), params);
		
		barChartButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(getApplicationContext());
				
				dialog.setContentView(R.layout.report_view_chart_custom_dialog);
				if(checkMonthly)
					dialog.setTitle("Chọn tháng so sánh");
				else
					dialog.setTitle("Chọn tuần so sánh");
				
				
				
				dialog.show();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_report_view_bar_chart, menu);
        return true;
    }
}
