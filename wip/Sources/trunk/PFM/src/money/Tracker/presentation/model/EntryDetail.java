package money.Tracker.presentation.model;

import java.util.Date;

public class EntryDetail {
	private int id, category_id, entry_id;
	private String name;
	private double money;

	public EntryDetail() {
		super();
	}

	public EntryDetail(int id, int category_id, int type, String name,
			Date date, double money) {
		super();
		this.id = id;
		this.category_id = category_id;
		this.name = name;
		this.money = money;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	public int compareTo(EntryDetail value)
	{
		return this.name.compareTo(value.getName());
	}

	public int getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(int entry_id) {
		this.entry_id = entry_id;
	}
}
