package money.Tracker.presnetation.model;

import java.util.ArrayList;
import java.util.Date;

public class Schedule {
	public Date start_date;
	public Date end_date;
	public double budget;
	public ArrayList<DetailSchedule> details = new ArrayList<DetailSchedule>();
	
	public Schedule(double budget,Date start_date, Date end_date)
	{
		this.budget = budget;
		this.start_date = start_date;
		this.end_date = end_date;
	}
	
	public void setDetails(ArrayList<DetailSchedule> details)
	{
		this.details = details;
	}
}
