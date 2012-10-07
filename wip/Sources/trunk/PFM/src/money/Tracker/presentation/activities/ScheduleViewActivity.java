package money.Tracker.presentation.activities;

import java.util.ArrayList;

import org.apache.http.conn.routing.RouteInfo.LayerType;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.DataManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleViewActivity extends Activity {
	TextView displayText;
	LinearLayout chart_legend;
	ListView list;

	public static ScheduleViewActivity context;
	ArrayList<Object> values;
	private ScheduleViewAdapter scheduleAdapter;

	boolean isMonthly;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_view);
		context = this;
		Bundle extras = getIntent().getExtras();
		isMonthly = extras.getBoolean("Monthly");

		displayText = (TextView) findViewById(R.id.no_data_edit);

		chart_legend = (LinearLayout) findViewById(R.id.chart_legend);
		list = (ListView) findViewById(R.id.schedule_view_list);
		list.setOnItemClickListener(onListClick);

		registerForContextMenu(list);

		bindData();
	}

	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			Schedule schedule = (Schedule) listView.getAdapter().getItem(
					position);
			if (schedule != null) {
				Intent scheduleDetail = new Intent(ScheduleViewActivity.this,
						ScheduleDetailViewActivity.class);
				scheduleDetail.putExtra("schedule_id", schedule.id);
				startActivity(scheduleDetail);
			}
		}
	};

	@Override
	protected void onRestart() {
		super.onRestart();
		bindData();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.schedule_view_list) {
			menu.setHeaderTitle(getResources().getString(
					R.string.schedule_menu_title));
			String[] menuItems = getResources().getStringArray(
					R.array.schedule_context_menu_item);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();
		final int schedule_id = ((Schedule) values.get(info.position)).id;
		switch (menuItemIndex) {
		case 0: // Edit
			Intent edit = new Intent(this, ScheduleEditActivity.class);
			edit.putExtra("schedule_id", schedule_id);
			startActivity(edit);
			break;
		case 1: // Delete
			Alert.getInstance().showDialog(getParent(),
					"Delete selected schedule?", new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SqlHelper.instance.delete("Schedule", "Id = "
									+ schedule_id);
							bindData();
						}
					});

			break;
		}

		return true;
	}

	private void bindData() {
		String whereCondition;
		if (isMonthly) {
			whereCondition = "Time_Id = 1";
		} else {
			whereCondition = "Time_Id = 0";
		}

		values = DataManager.getObjects("Schedule", whereCondition);
		if (values.size() == 0) {
			chart_legend.setVisibility(View.GONE);
			displayText.setVisibility(View.VISIBLE);
			list.setVisibility(View.GONE);
			return;
		}

		sort();
		list.setVisibility(View.VISIBLE);
		displayText.setVisibility(View.GONE);
		chart_legend.setVisibility(View.VISIBLE);
		scheduleAdapter = new ScheduleViewAdapter(this,
				R.layout.schedule_edit_item, values);

		scheduleAdapter.notifyDataSetChanged();
		list.setAdapter(scheduleAdapter);

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
	}

	private void sort() {
		int i, j;
		int length = values.size();
		Schedule t = new Schedule();
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				if (((Schedule) values.get(j - 1)).end_date
						.compareTo(((Schedule) values.get(j)).end_date) < 0) {
					t.setValue((Schedule) values.get(j - 1));
					((Schedule) values.get(j - 1)).setValue((Schedule) values
							.get(j));
					((Schedule) values.get(j)).setValue(t);
				}
			}
		}

	}
}
