package money.Tracker.presentation.customviews;

import java.util.Date;
import java.util.List;
import money.Tracker.common.utilities.Converter;
import money.Tracker.presentation.activities.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ReportCustomDialogViewItem extends LinearLayout {

	public ReportCustomDialogViewItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ReportCustomDialogViewItem(Context context, boolean checkMonthly, final Date startDate, final Date endDate, final List<Date[]> dateList, boolean check, final Button okButton) {
		// TODO Auto-generated constructor stub
		super(context);

		LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.report_view_chart_compare_custom_dialog_view_item, this, true);

		// Get control from .xml file.
		CheckBox viewItemCheckbox = (CheckBox) findViewById(R.id.report_compare_custom_dialog_view_item_checkbox);
		TextView viewItemTitle = (TextView) findViewById(R.id.report_compare_custom_dialog_view_item_title);

		// Set value to item.
		if(checkMonthly)
		{
			viewItemTitle.setText(Converter.toString(startDate, "MM/yyyy"));
		} else
		{
			viewItemTitle.setText(Converter.toString(startDate, "dd/MM/yyyy") + " - " + Converter.toString(endDate, "dd/MM/yyyy"));
		}
		
		if (check)
		{
			viewItemCheckbox.setChecked(true);
			Date[] addDate = {startDate, endDate};
			dateList.add(addDate);
		}
		
		viewItemCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					Date[] addDate = {startDate, endDate};
					dateList.add(addDate);
					okButton.setVisibility(View.VISIBLE);
				}else
				{
					for (int i = 0; i < dateList.size(); i++)
					{
						Date[] compareDate = dateList.get(i);
						if (startDate.equals(compareDate[0]) && endDate.equals(compareDate[1]))
						{
							dateList.remove(i);
						}
					}
					
					if (dateList.size() == 1)
					{
						okButton.setVisibility(View.GONE);
					}
				}
			}
		});
	}
}
