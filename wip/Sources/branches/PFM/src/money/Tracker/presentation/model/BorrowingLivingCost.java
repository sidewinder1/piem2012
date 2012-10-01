package money.Tracker.presentation.model;

public class BorrowingLivingCost {
	private int money;
	private String interestType;
	private int interest;
	private String startDate;
	private String expiredDate;
	private String name;
	private String address;
	private String phone;
	
	public BorrowingLivingCost(int _money, String _interestType, int _interest, String _startDate, String _expiredDate, String _name, String _address, String _phone)
	{
		this.money = _money;
		this.interestType = _interestType;
		this.interest = _interest;
		this.startDate = _startDate;
		this.expiredDate = _expiredDate;
		this.name = _name;
		this.address = _address;
		this.phone = _phone;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public int getInterest() {
		return interest;
	}

	public void setInterest(int interest) {
		this.interest = interest;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
