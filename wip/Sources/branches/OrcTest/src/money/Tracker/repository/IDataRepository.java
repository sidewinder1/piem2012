package money.Tracker.repository;

import java.util.ArrayList;

import money.Tracker.presentation.model.IModelBase;

public interface IDataRepository {
	// public static IDataRepository instance = null;
	public ArrayList<IModelBase> getData(String param);
}
