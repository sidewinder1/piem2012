package money.Tracker.presentation.model;

public class EntryDetail implements IModelBase {
	private int id, category_id, entry_id;
	private String name;
	private double money;

	public EntryDetail() {
		super();
	}
	
	public EntryDetail(int category_id, String name,
			double money) {
		super();
		this.category_id = category_id;
		this.name = name;
		this.money = money;
	}

	public EntryDetail(int id, int entry_id, int category_id, String name,
			double money) {
		super();
		this.id = id;
		this.entry_id = entry_id;
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

	public int getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(int entry_id) {
		this.entry_id = entry_id;
	}

	public int compareTo(IModelBase value) {
		return this.name.compareTo(((EntryDetail) value).getName());
	}
}
