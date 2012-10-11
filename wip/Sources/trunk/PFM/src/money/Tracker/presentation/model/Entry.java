package money.Tracker.presentation.model;

import java.util.ArrayList;
import java.util.Date;

public class Entry implements IModelBase {
	private int id, type;
	private Date date;
	private ArrayList<EntryDetail> entryDetails;
	
	public Entry(int id, int type, Date date, ArrayList<EntryDetail> entryDetails) {
		super();
		this.id = id;
		this.type = type;
		this.date = date;
		this.setEntryDetails(entryDetails); 
	}

	public double getTotal()
	{
		double total = 0;
		for (EntryDetail entryDetail : entryDetails)
		{
			total += entryDetail.getMoney();
		}
		
		return total;
	}
	
	public Entry() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public int compareTo(IModelBase comparedEntry)
	{
		return date.compareTo(((Entry)comparedEntry).getDate());
	}

	public ArrayList<EntryDetail> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(ArrayList<EntryDetail> entryDetails) {
		this.entryDetails = entryDetails;
	}
}
