package money.Tracker.repository;

import java.util.ArrayList;
import java.util.HashMap;

public class DataManager {
	private static HashMap<String, ArrayList<Object>> repository = new HashMap<String, ArrayList<Object>>();

	public DataManager() {

	}

	// This method is used to get data from database with specified table name.
	public static void updateData(String tableName) {
		IDataRepository dataRepository = new ScheduleRepository();

		if (tableName != null) {
			if (repository.containsKey(tableName)) 
			{
				repository.remove(tableName);
			}
			
			repository.put(tableName, dataRepository.getData());
		}
	}

	public static ArrayList<Object> getObjects(String tableName) {
		updateData(tableName);
		return repository.get(tableName);
	}
}
