package money.Tracker.presentation.model;

public class DetailSchedule {
	private int category_id;
	private double budget;
	private int schedule_id;
	
	public DetailSchedule(int category_id, double budget) {
		this.category_id = category_id;
		this.budget = budget;
	}
	
	public int getCategory() {
		return category_id;
	}

	public void setCategory(int category) {
		this.category_id = category;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double detailBudget) {
		this.budget = detailBudget;
	}

	public int getSchedule() {
		return schedule_id;
	}

	public void setSchedule(int schedule_id) {
		this.schedule_id = schedule_id;
	}
}
