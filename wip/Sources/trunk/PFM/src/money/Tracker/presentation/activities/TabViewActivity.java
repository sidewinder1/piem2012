package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.presentation.adapters.EntryViewAdapter;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.EntryRepository;
import money.Tracker.repository.ScheduleRepository;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
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

public class TabViewActivity extends Activity {
	private final String sub_path = "type.tab.path.id.subtab";
	public static TabViewActivity context;

	TextView displayText;
	LinearLayout chart_legend;
	ListView list;
	ArrayList<IModelBase> values;

	boolean isTabOne, isEntry;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_content_view);
		context = this;
		Bundle extras = getIntent().getExtras();
		isTabOne = extras.getString(sub_path).startsWith("1");
		isEntry = extras.getString(sub_path).endsWith("0");
		displayText = (TextView) findViewById(R.id.no_data_edit);

		chart_legend = (LinearLayout) findViewById(R.id.chart_legend);
		list = (ListView) findViewById(R.id.tab_content_view_list);
		list.setOnItemClickListener(onListClick);

		registerForContextMenu(list);

		bindData();
	}

	private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> listView, View view,
				int position, long id) {
			int data_id = -1;
			if (isEntry) {
				Entry entry = (Entry) listView.getAdapter().getItem(position);
				data_id = entry.getId();
			} else {
				Schedule schedule = (Schedule) listView.getAdapter().getItem(
						position);
				data_id = schedule.id;
			}

			Intent scheduleDetail = new Intent(TabViewActivity.this,
					ScheduleDetailViewActivity.class);
			scheduleDetail.putExtra("schedule_id", data_id);
			startActivity(scheduleDetail);
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
		if (v.getId() == R.id.tab_content_view_list) {
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
		int id = -1;
		if (isEntry) {
			id = ((Entry) values.get(info.position)).getId();
		} else {
			id = ((Schedule) values.get(info.position)).id;
		}

		switch (menuItemIndex) {
		case 0: // Edit
			Intent edit = null;
			if (isEntry) {
				edit = new Intent(this, EntryEditActivity.class);
				edit.putExtra("entry_id", id);
			} else {
				edit = new Intent(this, ScheduleEditActivity.class);
				edit.putExtra("schedule_id", id);
			}
			startActivity(edit);
			break;
		case 1: // Delete
			final int sId = id;
			Alert.getInstance().showDialog(getParent(),
					getResources().getString(R.string.delete_confirm),
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SqlHelper.instance
									.delete("Schedule", "Id = " + sId);
							bindData();
						}
					});

			break;
		}

		return true;
	}

	private void bindData() {
		String whereCondition;
		if (isTabOne) {
			whereCondition = "Type = 1";
		} else {
			whereCondition = "Type = 0";
		}

		values = isEntry ? EntryRepository.getInstance()
				.getData(whereCondition) : ScheduleRepository.getInstance()
				.getData(whereCondition);

		if (values.size() == 0) {
			hasData(false);
			return;
		}

		sort();
		hasData(true);

		if (isEntry) {
			EntryViewAdapter entryAdapter = new EntryViewAdapter(this,
					R.layout.entry_view_month_item, values);

			entryAdapter.notifyDataSetChanged();
			list.setAdapter(entryAdapter);
		} else {
			ScheduleViewAdapter scheduleAdapter = new ScheduleViewAdapter(this,
					R.layout.schedule_edit_item, values);

			scheduleAdapter.notifyDataSetChanged();
			list.setAdapter(scheduleAdapter);
		}

		bindChartLegend();
	}

	private void hasData(boolean hasData) {
		list.setVisibility(hasData ? View.VISIBLE : View.GONE);
		displayText.setVisibility(!hasData ? View.VISIBLE : View.GONE);
		chart_legend.setVisibility(hasData ? View.VISIBLE : View.GONE);
	}

	private void bindChartLegend() {
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

	}
}
