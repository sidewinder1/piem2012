package money.Tracker.presentation.model;

import java.util.Date;

public class EntryDetail {
	private int id, category_id, type;
	private String name;
	private Date date;
	private double money;

	public EntryDetail(int id, int category_id, int type, String name,
			Date date, double money) {
		super();
		this.id = id;
		this.category_id = category_id;
		this.type = type;
		this.name = name;
		this.date = date;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
