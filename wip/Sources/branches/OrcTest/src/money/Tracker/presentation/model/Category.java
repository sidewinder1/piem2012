package money.Tracker.presentation.model;

public class Category {
	private int id;
	private String name, user_color;

	public Category() {
		super();
	}

	public Category(int id, String name, String user_color) {
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int compareTo(Category category) {
		if ("Others".contains(name)) {
			return 1;
		}

		if ("Others".contains(category.name)) {
			return -1;
		}

		return name.compareToIgnoreCase(category.name);
	}
}