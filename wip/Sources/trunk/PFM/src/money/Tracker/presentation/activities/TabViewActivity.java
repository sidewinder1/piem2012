package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.presentation.adapters.ScheduleViewAdapter;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.customviews.EntryDayView;
import money.Tracker.presentation.customviews.EntryMonthView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import money.Tracker.repository.DataManager;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class TabViewActivity extends Activity {
	private final String sub_path = "type.tab.path.id.subtab";
	public static TabViewActivity context;

	TextView displayText;
	LinearLayout chart_legend;
	ListView list;
	LinearLayout entry_list;
	ArrayList<IModelBase> values;
	ScrollView entry_scroll;

	boolean isTabOne, isEntry;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_content_view);
		context = this;
		Bundle extras = getIntent().getExtras();
		isTabOne = extras.getString(sub_path).startsWith("1");
		isEntry = extras.getString(sub_path).endsWith("0");
		entry_list = (LinearLayout) findViewById(R.id.entry_tab_content_view_list);
		displayText = (TextView) findViewById(R.id.no_data_edit);
		entry_scroll = (ScrollView) findViewById(R.id.entry_view_scroll);

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

	EntryDayView selectedEntryItem = null;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		selectedEntryItem = (EntryDayView) v;
		if (v.getId() == R.id.tab_content_view_list
				|| selectedEntryItem != null) {
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
		int id = -1;

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if (selectedEntryItem != null) {
			id = selectedEntryItem.id;
		} else {
			if (isEntry) {
				id = ((Entry) values.get(info.position)).getId();
			} else {
				id = ((Schedule) values.get(info.position)).id;
			}
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
			final String table = isEntry ? "Entry" : "Schedule";
			Alert.getInstance().showDialog(getParent(),
					getResources().getString(R.string.delete_confirm),
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SqlHelper.instance.delete(table, new StringBuilder(
									"Id = ").append(sId).toString());
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

		DataManager.sort(values);
		hasData(true);

		if (isEntry) {
			entry_list.removeAllViews();
			for (String key : EntryRepository.getInstance().orderedEntries
					.keySet()) {
				entry_list.addView(new EntryMonthView(this, key));
			}
		} else {
			ScheduleViewAdapter scheduleAdapter = new ScheduleViewAdapter(this,
					R.layout.schedule_edit_item, values);

			scheduleAdapter.notifyDataSetChanged();
			list.setAdapter(scheduleAdapter);
		}

		bindChartLegend();
	}

	private void hasData(boolean hasData) {
		list.setVisibility(hasData && !isEntry ? View.VISIBLE : View.GONE);
		entry_scroll.setVisibility(hasData && isEntry ? View.VISIBLE
				: View.GONE);
		displayText.setVisibility(!hasData ? View.VISIBLE : View.GONE);
		chart_legend.setVisibility(hasData ? View.VISIBLE : View.GONE);
	}

	private void bindChartLegend() {
		// Bind chart legend:
		String subTable = isEntry ? "EntryDetail" : "ScheduleDetail";
		String table = isEntry ? "Entry" : "Schedule";
		Cursor category = SqlHelper.instance.query(new StringBuilder(
				"SELECT DISTINCT Category.Name, Category.User_Color ")
				.append("FROM Category ").append("INNER JOIN ")
				.append(subTable).append(" ON Category.Id=").append(subTable)
				.append(".Category_Id").append(" INNER JOIN ").append(table)
				.append(" ON ").append(subTable).append(".").append(table)
				.append("_Id=").append(table).append(".Id ").append(" WHERE ")
				.append(table).append(".Type = ").append(isTabOne ? "1" : "0")
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
					LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
					chart_legend.addView(itemView, params1);
					itemView = new LinearLayout(this);
				}

				index++;
			} while (category.moveToNext());
		}
	}
}
