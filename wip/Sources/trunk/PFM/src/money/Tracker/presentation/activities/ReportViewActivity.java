package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.presentation.adapters.BorrowLendAdapter;
import money.Tracker.presentation.adapters.ReportAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.BorrowLendRepository;
import money.Tracker.repository.EntryRepository;
import money.Tracker.repository.ScheduleRepository;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ReportViewActivity extends Activity {

	private boolean checkMonthly;
	private TextView displayNoReportDataText;
	private ListView reportList;
	private ArrayList<IModelBase> scheduleValues;
	private ArrayList<IModelBase> entryValues;
	private ReportAdapter reportAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);
        
        Bundle extras = getIntent().getExtras();
        checkMonthly = extras.getBoolean("Monthly");
		displayNoReportDataText = (TextView) findViewById(R.id.no_report_data);
		
		reportList = (ListView) findViewById(R.id.report_list_view);
		reportList.setOnItemClickListener(onListClick);

		registerForContextMenu(reportList);

		bindData();

    }
    
    @Override
    protected void onRestart() {
    	bindData();
    };
    
    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view,
				int position, long id) {
			int data_id = -1;
			Schedule schedule = (Schedule) listView.getAdapter().getItem(
						position);
			data_id = schedule.id;

			Intent reportDetail = new Intent(ReportViewActivity.this,
					ReportViewDetailActivity.class);
			reportDetail.putExtra("schedule_id", data_id);
			startActivity(reportDetail);
		}
	};
	
	private void bindData() {
		String whereCondition;
		if (checkMonthly) {
			whereCondition = "Type = 1";
		} else {
			whereCondition = "Type = 0";
		}

		ScheduleRepository scre = new ScheduleRepository();
		scheduleValues = scre.getData(whereCondition);

		if (scheduleValues.size() == 0) {
			displayNoReportDataText.setVisibility(View.VISIBLE);
		}
		else
		{
			displayNoReportDataText.setVisibility(View.GONE);
		}
		
		//sort();
		Log.d("Check display", "Check 1");
		reportAdapter = new ReportAdapter(this, R.layout.activity_report_view_item, scheduleValues);
		Log.d("Check display", "Check 2");
		reportList.setVisibility(View.VISIBLE);
		Log.d("Check display", "Check 3");
		reportAdapter.notifyDataSetChanged();
		Log.d("Check display", "Check 4");
		reportList.setAdapter(reportAdapter);
		Log.d("Check display", "Check 5");
	}

	/*private void bindChartLegend() {
		// Bind chart legend:
		Cursor category = SqlHelper.instance
				.query(new StringBuilder(
						"SELECT DISTINCT Category.Name, Category.User_Color ")
						.append("FROM Category ")
						.append("INNER JOIN ScheduleDetail ")
						.append("ON Category.Id=ScheduleDetail.Category_Id")
						.toString());

		if (category != null && category.moveToFirst()) {
			int index = 1;
			chart_legend.removeAllViews();
			LinearLayout itemView = new LinearLayout(this);
			do {
				CategoryLegendItemView item = new CategoryLegendItemView(this);
				item.setName(category.getString(0));
				item.setColor(category.getString(1));
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT, 1);
				itemView.addView(item, params);

				if ((index % 2 == 0) || index == category.getCount()) {
					chart_legend.addView(itemView);
					itemView = new LinearLayout(this);
				}

				index++;
			} while (category.moveToNext());
		}
	}*/

	/*private void sort() {
		int i, j;
		int length = values.size();
		IModelBase t;
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				if ((values.get(j - 1)).compareTo(values.get(j)) < 0) {
					t = values.get(j - 1);
					values.set(j - 1, values.get(j));
					values.set(j, t);
				}
			}
		}

	}*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_report_view, menu);
        return true;
    }
}
