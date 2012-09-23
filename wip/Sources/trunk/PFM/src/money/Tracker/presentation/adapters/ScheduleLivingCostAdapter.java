package money.Tracker.presentation.adapters;

import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;
import money.Tracker.presnetation.model.*;
import money.Tracker.presentation.customviews.*;

public class ScheduleLivingCostAdapter extends ArrayAdapter<ScheduleLivingCost>{
	private ArrayList<ScheduleLivingCost> array;
	private int resource;
	private Context context;
	public ScheduleLivingCostAdapter(Context context, int resource, ArrayList<ScheduleLivingCost> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.array = objects;
		this.resource = resource;
	}
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View scheduleItemView = convertView;
        
        if (scheduleItemView == null) {
            scheduleItemView = new ScheduleItem(getContext());
        }
        
        final ScheduleLivingCost livingCost = array.get(position);

        if (livingCost != null) {
            Spinner category = ((ScheduleItem) scheduleItemView).category;
            EditText budget = ((ScheduleItem) scheduleItemView).budget;
            Button addButton = ((ScheduleItem) scheduleItemView).addBtn;
            Button removeButton = ((ScheduleItem) scheduleItemView).removeBtn;
           
            budget.setHint(livingCost.getBudget() + "");     
            
            // Add new schedule item.
            addButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					array.add(new ScheduleLivingCost("Category 1", 200));
					notifyDataSetChanged();
				}
			});
            
            // Remove this schedule item.
            removeButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					array.remove(position);
					notifyDataSetChanged();
				}
			});
        }
        
        return scheduleItemView;
    }
}
