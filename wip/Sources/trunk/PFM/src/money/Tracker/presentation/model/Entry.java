package money.Tracker.presentation.model;

import java.util.Date;

public class Entry {
	private int id, type;
	private Date date;

	public Entry(int id, int type, Date date) {
		super();
		this.id = id;
		this.type = type;
		this.date = date;
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
	
	public int compareTo(Entry comparedEntry)
	{
		return date.compareTo(comparedEntry.getDate());
	} 
}
