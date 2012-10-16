package money.Tracker.repository;

import java.util.ArrayList;
import java.util.HashMap;

import money.Tracker.presentation.model.IModelBase;

public class DataManager {
	private static HashMap<String, ArrayList<IModelBase>> repository = new HashMap<String, ArrayList<IModelBase>>();

	public DataManager() {
	}

	// This method is used to get data from database with specified table name.
	public static void updateData(String tableName, String param) {
		IDataRepository dataRepository = new ScheduleRepository();

		if (tableName != null) {
			
			if (repository.containsKey(tableName)) 
			{
				repository.remove(tableName);
			}
			
			repository.put(tableName, dataRepository.getData(param));
		}
	}

	public static ArrayList<IModelBase> getObjects(String tableName, String param) {
		return repository.get(tableName);
	}
	
	
	public static void sort(ArrayList<IModelBase> values) {
		int i, j;
		int length = values.size();
		IModelBase t;
		for (i = 0; i < length; i++) {
			for (j = 1; j < (length - i); j++) {
				if ((values.get(j - 1)).compareTo(values.get(j)) < 0) {
					t = values.get(j - 1);
					values.set(j - 1, values.get(j));
					values.set(j, t);
				}
			}
		}

	}
}
