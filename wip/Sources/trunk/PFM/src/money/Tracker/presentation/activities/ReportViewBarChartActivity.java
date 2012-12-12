package money.Tracker.presentation.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Chart;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.customviews.ReportCustomDialogViewItem;
import money.Tracker.presentation.customviews.ReportViewItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ReportViewBarChartActivity extends Activity {

	private Date _startDate;
	private Date _endDate;
	private boolean checkMonth;
	private LinearLayout barChart;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report_view_bar_chart);

		Bundle extras = getIntent().getExtras();
		final boolean checkMonthly = extras.getBoolean("checkMonthly");
		checkMonth = checkMonthly;
		_startDate = Converter.toDate(extras.getString("start_date"));
		_endDate = Converter.toDate(extras.getString("end_date"));

		TextView buttonBarChartCompare = (TextView) findViewById(R.id.report_bar_chart_button_compare_title);
		LinearLayout barChartButton = (LinearLayout) findViewById(R.id.report_bar_chart_button_compare);
		barChart = (LinearLayout) findViewById(R.id.report_bar_chart);

		if (checkMonthly) {
			buttonBarChartCompare.setText("So sánh giữa các tháng");
		} else {
			buttonBarChartCompare.setText("So sánh giữa các tuần");
		}

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		Chart chart = new Chart();
		barChart.addView(chart.getBarIntent(this, checkMonthly, _startDate, _endDate), params);
	}

	/*
	private void bindCompareChart() {
		if (dateList.size() > 1)
		{
			Chart chart = new Chart();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			barChart.removeAllViews();
			barChart.addView(chart.getBarCompareIntent(this, checkMonth, dateList), params);
		}
	}
	*/

}
