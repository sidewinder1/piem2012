package pfm.database;

public class InputType {
	// local variable
	String type;
	String name;
	int total;
	double interest;
	String startDate;
	String endDate;
	
	public InputType()
	{
		
	}
	
	public InputType(String _type, String _name, int _total, double _interest, String _startDate, String _endDate)
	{
		this.type = _type;
		this.name = _name;
		this.total = _total;
		this.interest = _interest;
		this.startDate = _startDate;
		this.endDate = _endDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
