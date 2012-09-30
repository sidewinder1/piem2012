package money.Tracker.presentation.model;

import java.util.Date;

public class BorrowLend {
	private int id;
	private double money;
	private String interestType; 
	private int interestRate;
	private Date startDate;
	private Date expiredDate;
	private String personName;
	private String personPhone;
	private String personAddress;
	
	public BorrowLend() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getInterestType() {
		return interestType;
	}

	public void setInterestType(String interestType) {
		this.interestType = interestType;
	}

	public int getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(int interestRate) {
		this.interestRate = interestRate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate( Date date) {
		this.startDate = date;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getPersonName() {
		return personName;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}

	public String getPersonPhone() {
		return personPhone;
	}

	public void setPersonPhone(String personPhone) {
		this.personPhone = personPhone;
	}

	public String getPersonAddress() {
		return personAddress;
	}

	public void setPersonAddress(String personAddress) {
		this.personAddress = personAddress;
	}
}
