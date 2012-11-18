package money.Tracker.presentation.model;

public class DetailSchedule {
	private long category_id;
	private long budget;
	private long schedule_id;
	private long id;

	public DetailSchedule(long id, long category_id, long budget,
			long schedule_id) {
		this.category_id = category_id;
		this.budget = budget;
		this.setId(id);
		this.schedule_id = schedule_id;
	}

	public DetailSchedule(long id, long category_id, long budget) {
		// TODO Auto-generated constructor stub
		this.category_id = category_id;
		this.budget = budget;
		this.setId(id);
	}

	public long getCategory() {
		return category_id;
	}

	public void setCategory(long category) {
		this.category_id = category;
	}

	public long getBudget() {
		return budget;
	}

	public void setBudget(long detailBudget) {
		this.budget = detailBudget;
	}

	public long getSchedule() {
		return schedule_id;
	}

	public void setSchedule(long schedule_id) {
		this.schedule_id = schedule_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}