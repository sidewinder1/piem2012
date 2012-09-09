package pfm2012.sqllite;

public class Expenditure {
	//local variable
	private int _expenditure_id;
	private String _expenditure_name;
	private int _expenditure_value;
	private String _expenditure_date;
	private String _type_name;
	
	public Expenditure(){
		
	}
	
	public Expenditure(int expenditure_id, String expenditure_name, int expenditure_value, String expenditure_date, String typre_name) {
		// TODO Auto-generated constructor stub
		this._expenditure_id = expenditure_id;
		this._expenditure_name = expenditure_name;
		this._expenditure_value = expenditure_value;
		this._expenditure_date = expenditure_date;
		this._type_name = typre_name;
	}

	public Expenditure(String expenditure_name, int expenditure_value, String expenditure_date, String typre_name) {
		// TODO Auto-generated constructor stub
		this._expenditure_name = expenditure_name;
		this._expenditure_value = expenditure_value;
		this._expenditure_date = expenditure_date;
		this._type_name = typre_name;
	}

	//get expenditure number
	public int getExpenditureId() {
		return _expenditure_id;
	}

	//set expenditure number
	public void setExpenditureId(int expenditure_id) {
		this._expenditure_id = expenditure_id;
	}

	//get expenditure name
	public String getExpenditureName() {
		return _expenditure_name;
	}

	//set expenditure name
	public void setExpenditureName(String expenditure_name) {
		this._expenditure_name = expenditure_name;
	}

	//get expenditure value
	public int getExpenditureValue() {
		return _expenditure_value;
	}

	//set expenditure value
	public void setExpenditureValue(int expenditure_value) {
		this._expenditure_value = expenditure_value;
	}

	//get expenditure date
	public String getExpenditureDate() {
		return _expenditure_date;
	}

	//set expenditure date
	public void setExpenditureDate(String expenditure_date) {
		this._expenditure_date = expenditure_date;
	}
	
	//get type name
	public String getTypeName() {
		return _type_name;
	}

	//set type name
	public void setTypeName(String type_name) {
		this._type_name = type_name;
	}
}
