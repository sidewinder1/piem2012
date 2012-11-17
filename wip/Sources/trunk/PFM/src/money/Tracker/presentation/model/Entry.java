package money.Tracker.presentation.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Entry implements IModelBase {
	private long id;
	private int type;
	private Date date;
	private ArrayList<EntryDetail> entryDetails;

	public Entry(long id, int type, Date date,
			ArrayList<EntryDetail> entryDetails) {
		super();
		this.id = id;
		this.type = type;
		this.date = date;
		this.setEntryDetails(entryDetails);
	}

	public double getTotal(long category) {
		if (entryDetails == null) {
			return 0;
		}

		double total = 0;
		for (EntryDetail entryDetail : entryDetails) {
			if (entryDetail.getCategory_id() == category) {
				total += entryDetail.getMoney();
			}
		}

		return total;
	}

	public double getTotal() {
		if (entryDetails == null) {
			return 0;
		}

		double total = 0;

		for (EntryDetail entryKey : entryDetails) {
			total += entryKey.getMoney();
		}

		return total;
	}

	public Entry() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public int compareTo(IModelBase comparedEntry) {
		return date.compareTo(((Entry) comparedEntry).getDate());
	}

	public ArrayList<EntryDetail> getEntryDetails() {
		return entryDetails;
	}

	public void setEntryDetails(ArrayList<EntryDetail> entryDetails) {
		this.entryDetails = entryDetails;
	}
}
