package money.Tracker.presentation.model;

import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;

public class Category {
	private long id;
	private String name, user_color;

	public Category() {
		super();
	}

	public Category(long id, String name, String user_color) {
		this.id = id;
		this.name = name;
		this.user_color = user_color;
	}

	public String getUser_color() {
		return user_color;
	}

	public void setUser_color(String user_color) {
		this.user_color = user_color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int compareTo(Category category) {
		if (PfmApplication.getAppResources().getString(R.string.others)
				.contains(name)) {
			return 1;
		}

		if (PfmApplication.getAppResources().getString(R.string.others)
				.contains(category.name)) {
			return -1;
		}

		return getSimpleString(name).compareToIgnoreCase(getSimpleString(category.name));
	}
	
	private String getSimpleString(String str){
		return str.replaceAll(PfmApplication.getAppResources().getString(R.string.o_character), "o")
				.replaceAll(PfmApplication.getAppResources().getString(R.string.a_character), "a")
				.replaceAll(PfmApplication.getAppResources().getString(R.string.e_character), "e")
				.replaceAll(PfmApplication.getAppResources().getString(R.string.u_character), "u")
				.replaceAll(PfmApplication.getAppResources().getString(R.string.d_character), "d");
	} 
}
