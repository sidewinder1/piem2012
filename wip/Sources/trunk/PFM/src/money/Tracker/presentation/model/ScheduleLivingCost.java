package money.Tracker.presentation.model;

public class ScheduleLivingCost {
	private int categoryType;
	private double budget;

	public ScheduleLivingCost(int ipCategoryType, double ipBudget) {
		categoryType = ipCategoryType;
		budget = ipBudget;
	}
	
	public int getCategory() {
		return categoryType;
	}
	
	public double getBudget() {
		return budget;	
	}
	
	public void setCategory(int category)
	{
		categoryType = category;
	}
	
	public void setBudget(double budget)
	{
		this.budget = budget;
	}
}
