package money.Tracker.presentation.customviews;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.ScheduleDetailViewActivity;
import money.Tracker.presentation.model.IModelBase;
import money.Tracker.presentation.model.Schedule;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author Kaminari.hp
 * Custom view is used to display Schedule item on Schedule tab.
 */
public class ScheduleViewItem extends LinearLayout {
	public TextView mScheduleItemTitle, mTotalBudget;
	public LinearLayout mStackedBarChart;
	public long mId;

	/**
	 * Constructor of custom view
	 * @param context
	 * Parent data context.
	 */
	public ScheduleViewItem(Context context) {
		super(context);
		((Activity) context).registerForContextMenu(this);
		initializeComponent(null);
	}
	
	/**
	 * Constructor of custom view
	 * @param context
	 * Parent data context.
	 */
	public ScheduleViewItem(Context context, IModelBase schedule) {
		super(context);
		((Activity) context).registerForContextMenu(this);
		initializeComponent((Schedule)schedule);
	}

	/**
	 * Initialize component of custom view.
	 */
	private void initializeComponent(Schedule schedule) {
		LayoutInflater layoutInflater = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.schedule_view_item, this, true);

		mScheduleItemTitle = (TextView) findViewById(R.id.schedule_item_name);
		mTotalBudget = (TextView) findViewById(R.id.schedule_total_budget);
		mStackedBarChart = (LinearLayout) findViewById(R.id.stacked_bar_chart);
		
		setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent detail = new Intent(getContext(),
						ScheduleDetailViewActivity.class);
				detail.putExtra("schedule_id", ((ScheduleViewItem) v).mId);
				getContext().startActivity(detail);
			}
		});
		
		if (schedule != null) {
			mId = schedule.id;
			
			// Set content to item title:
			if (schedule.type == 1) {
				mScheduleItemTitle.setText(DateFormat.format("MM/yyyy",
						schedule.end_date));
			} else {
				mScheduleItemTitle.setText(new StringBuilder(DateFormat.format("dd/MM",
						schedule.start_date))
						.append("-")
						.append(DateFormat.format("dd/MM/yyyy",
								schedule.end_date)).toString());
			}

			// Set content to budget
			mTotalBudget.setText(Converter.toString(schedule.budget));

			mStackedBarChart.removeAllViews();

			// Prepare and display stacked bar chart:
			for (int i = 0; i < schedule.details.size(); i++) {
				View stackItem = new View(getContext());
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						0, LayoutParams.FILL_PARENT,
						Float.parseFloat(schedule.details.get(i).getBudget()
								+ ""));
				Cursor categoryCursor = SqlHelper.instance.select("Category",
						"Id, User_Color", "Id = "
								+ schedule.details.get(i).getCategory());

				if (categoryCursor != null && categoryCursor.moveToFirst()) {
					stackItem.setBackgroundColor(Color
							.parseColor(categoryCursor.getString(1)));
				}

				mStackedBarChart.addView(stackItem, params);
			}
		}
	}
}
