package money.Tracker.repository;

public class CategoryRepository {
	private static CategoryRepository instance;
	public CategoryRepository() {
		
	}
	
	public static CategoryRepository getInstance() {
		return instance == null ? new CategoryRepository() : instance;
	}
	

	
}
