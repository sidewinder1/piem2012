package money.Tracker.presentation.model;

public class EntryDetail implements IModelBase {
	private long id, category_id, entry_id;
	private String name;
	private double money;

	public EntryDetail() {
		super();
		name = "";
	}
	
	public EntryDetail(long category_id, String name,
			double money) {
		super();
		this.category_id = category_id;
		this.name = name;
		this.money = money;
	}

	public EntryDetail(long id, long entry_id, long category_id, String name,
			double money) {
		super();
		this.id = id;
		this.entry_id = entry_id;
		this.category_id = category_id;
		this.name = name;
		this.money = money;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCategory_id() {
		return category_id;
	}

	public void setCategory_id(long category_id) {
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

	public long getEntry_id() {
		return entry_id;
	}

	public void setEntry_id(long entry_id) {
		this.entry_id = entry_id;
	}

	public int compareTo(IModelBase value) {
		return this.name.compareTo(((EntryDetail) value).getName());
	}
}
