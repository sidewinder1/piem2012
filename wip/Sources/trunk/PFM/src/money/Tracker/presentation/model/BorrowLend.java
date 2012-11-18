package money.Tracker.presentation.model;

import java.util.Date;

public class BorrowLend {
	private long id;
	private String debtType;
	private long money;
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

	public void setValue(BorrowLend newBorrowLend) {
		this.id = newBorrowLend.getId();
		this.debtType = newBorrowLend.getDebtType();
		this.money = newBorrowLend.getMoney();
		this.interestType = newBorrowLend.getInterestType();
		this.interestRate = newBorrowLend.getInterestRate();
		this.startDate = newBorrowLend.getStartDate();
		this.expiredDate = newBorrowLend.getExpiredDate();
		this.personName = newBorrowLend.getPersonName();
		this.personPhone = newBorrowLend.getPersonPhone();
		this.personAddress = newBorrowLend.getPersonAddress();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDebtType() {
		return debtType;
	}

	public void setDebtType(String debtType) {
		this.debtType = debtType;
	}

	public long getMoney() {
		return money;
	}

	public void setMoney(long money) {
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

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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
