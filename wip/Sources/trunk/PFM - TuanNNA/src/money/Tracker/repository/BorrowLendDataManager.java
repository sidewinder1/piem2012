package money.Tracker.repository;

import java.util.ArrayList;
import java.util.HashMap;

public class BorrowLendDataManager {
	private static HashMap<String, ArrayList<Object>> repository = new HashMap<String, ArrayList<Object>>();

	public BorrowLendDataManager() {
		// TODO Auto-generated constructor stub
	}
	
	// This method is used to get data from database with specified table name.
	public static void updateData(String tableName) {
		BorrowLendIDataRepository dataRepository = new BorrowLendRepository();

		if (tableName != null) {
			if (repository.containsKey(tableName)) 
			{
				repository.remove(tableName);
			}
			
			repository.put(tableName, dataRepository.getData(tableName));
		}
	}

	public static ArrayList<Object> getObjects(String tableName) {
		updateData(tableName);
		return repository.get(tableName);
	}
}
