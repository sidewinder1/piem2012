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
import money.Tracker.presentation.activities.R;
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
            final Spinner category = ((ScheduleItem) scheduleItemView).category;
            final EditText budget = ((ScheduleItem) scheduleItemView).budget;
            Button addButton = ((ScheduleItem) scheduleItemView).addBtn;
            Button removeButton = ((ScheduleItem) scheduleItemView).removeBtn;
           
            budget.setHint(livingCost.getBudget() + "");     
            
            // Create an ArrayAdapter using the string array and a default
			// spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter
					.createFromResource(context, R.array.schedule_categories,
							android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			category.setAdapter(adapter);
            // Add new schedule item.
            addButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					array.add(new ScheduleLivingCost(0, 200));
					notifyDataSetChanged();
					String value = budget.getText().toString();
					
					if (value + "" == "")
					{
						value = budget.getHint().toString();
					}
					
					if (value + "" == "")
					{
						value = "0";
					}
					
					livingCost.setBudget(Double.parseDouble(value));
					livingCost.setCategory(category.getSelectedItemPosition());
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
