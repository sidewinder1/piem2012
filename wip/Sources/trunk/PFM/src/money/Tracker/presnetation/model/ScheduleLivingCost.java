package money.Tracker.presnetation.model;

public class ScheduleLivingCost {
	private String categoryType;
	private double budget;

	public ScheduleLivingCost(String ipCategoryType, double ipBudget) {
		categoryType = ipCategoryType;
		budget = ipBudget;
	}
	
	public String getCategory() {
		return categoryType;
	}
	
	public double getBudget() {
		return budget;	
	}
	
	public void setCategory(String category)
	{
		categoryType = category;
	}
	
	public void setBudget(double budget)
	{
		this.budget = budget;
	}
}
