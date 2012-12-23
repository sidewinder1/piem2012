package money.Tracker.presentation.activities;

import java.util.ArrayList;
import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Alert;
import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.customviews.CategoryLegendItemView;
import money.Tracker.presentation.customviews.EntryDayView;
import money.Tracker.presentation.customviews.EntryMonthView;
import money.Tracker.presentation.customviews.ScheduleViewItem;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class TabViewActivity extends Activity {
	private final String sub_path = "type.tab.path.id.subtab";
	public static TabViewActivity mContext;

	TextView mDisplayText, mNote;
	LinearLayout mChartLegend;
	LinearLayout mEntryList;
	ArrayList<IModelBase> mValues;
	ScrollView mEntryScroll;

	boolean mIsTabOne, mIsEntry;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_content_view);
		mContext = this;
		Bundle extras = getIntent().getExtras();
		mIsTabOne = extras.getString(sub_path).startsWith("1");
		mIsEntry = extras.getString(sub_path).endsWith("0");
		mEntryList = (LinearLayout) findViewById(R.id.entry_tab_content_view_list);
		mDisplayText = (TextView) findViewById(R.id.no_data_edit);
		mEntryScroll = (ScrollView) findViewById(R.id.entry_view_scroll);
		mNote = (TextView) findViewById(R.id.tab_content_view_note);
		mChartLegend = (LinearLayout) findViewById(R.id.chart_legend);

	}

	@Override
	protected void onResume() {
		super.onResume();
		PfmApplication.sCurrentContext = this;
		bindData();
	}

	EntryDayView selectedEntryItem = null;
	ScheduleViewItem selectedScheduleItem = null;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getClass() == EntryDayView.class) {
			selectedEntryItem = (EntryDayView) v;
		} else {
			selectedEntryItem = null;
		}
		
		if (v.getClass() == ScheduleViewItem.class) {
			selectedScheduleItem = (ScheduleViewItem) v;
		} else {
			selectedScheduleItem = null;
		}

		if (selectedScheduleItem != null || selectedEntryItem != null) {
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
		long id = -1;

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();

		if (selectedEntryItem != null) {
			id = selectedEntryItem.id;
		}
		else if(selectedScheduleItem != null){
			id = selectedScheduleItem.mId;
		}
		else {
			if (mIsEntry) {
				id = ((Entry) mValues.get(info.position)).getId();
			} else {
				id = ((Schedule) mValues.get(info.position)).id;
			}
		}

		switch (menuItemIndex) {
		case 0: // Edit
			Intent edit = null;
			if (mIsEntry) {
				edit = new Intent(this, EntryEditActivity.class);
				edit.putExtra("entry_id", id);
			} else {
				edit = new Intent(this, ScheduleEditActivity.class);
				edit.putExtra("schedule_id", id);
			}
			startActivity(edit);
			break;
		case 1: // Delete
			final long sId = id;
			final String table = mIsEntry ? "Entry" : "Schedule";
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

	public void bindData() {
		String whereCondition;
		if (mIsTabOne) {
			whereCondition = "Type = 1";
		} else {
			whereCondition = "Type = 0";
		}

		mValues = mIsEntry ? EntryRepository.getInstance().getData(
				whereCondition) : ScheduleRepository.getInstance().getData(
				whereCondition);

		if (mValues.size() == 0) {
			hasData(false);
			return;
		}

		DataManager.sort(mValues);
		hasData(true);

		if (mIsEntry) {
			mEntryList.removeAllViews();
			for (String key : EntryRepository.getInstance().orderedEntries
					.keySet()) {
				mEntryList.addView(new EntryMonthView(this, key));
			}
		} else {
			mEntryList.removeAllViews();
			for (IModelBase schedule : mValues) {
				mEntryList.addView(new ScheduleViewItem(this, schedule));
			}
		}

		bindChartLegend();
	}

	private void hasData(boolean hasData) {
		mEntryScroll.setVisibility(hasData ? View.VISIBLE
				: View.GONE);
		mNote.setVisibility(hasData ? View.VISIBLE : View.GONE);
		mDisplayText.setVisibility(!hasData ? View.VISIBLE : View.GONE);
		mChartLegend.setVisibility(hasData ? View.VISIBLE : View.GONE);
	}

	private void bindChartLegend() {
		// Bind chart legend:
		String subTable = mIsEntry ? "EntryDetail" : "ScheduleDetail";
		String table = mIsEntry ? "Entry" : "Schedule";
		Cursor category = SqlHelper.instance.query(new StringBuilder(
				"SELECT DISTINCT Category.Name, Category.User_Color ")
				.append("FROM Category ").append("INNER JOIN ")
				.append(subTable).append(" ON Category.Id=").append(subTable)
				.append(".Category_Id").append(" INNER JOIN ").append(table)
				.append(" ON ").append(subTable).append(".").append(table)
				.append("_Id=").append(table).append(".Id ").append(" WHERE ")
				.append(table).append(".Type = ").append(mIsTabOne ? "1" : "0")
				.toString());

		if (category != null && category.moveToFirst()) {
			int index = 1;
			mChartLegend.removeAllViews();
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
					mChartLegend.addView(itemView, params1);
					itemView = new LinearLayout(this);
				}

				index++;
			} while (category.moveToNext());
		} else {
			mNote.setVisibility(View.GONE);
			mChartLegend.setVisibility(View.GONE);
		}
	}
}
