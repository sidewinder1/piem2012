package money.Tracker.presentation.model;

import java.util.ArrayList;
import java.util.Date;

public class Schedule {
	public int id;
	public Date start_date;
	public Date end_date;
	public double budget;
	public int time_id;
	
	public ArrayList<DetailSchedule> details = new ArrayList<DetailSchedule>();
	
	public Schedule(int id, double budget,Date start_date,
			Date end_date, int time_id,
			ArrayList<DetailSchedule> details)
	{
		this.id = id;
		this.budget = budget;
		 this.time_id = time_id;
		this.start_date = start_date;
		this.end_date = end_date;
		this.details = details;
	}
	
	public Schedule(float budget, Date start_date, Date end_date) {
		// TODO Auto-generated constructor stub
		this.budget = budget;
		this.start_date = start_date;
		this.end_date = end_date;
	}

	public void setDetails(ArrayList<DetailSchedule> details)
	{
		this.details = details;
	}
}
