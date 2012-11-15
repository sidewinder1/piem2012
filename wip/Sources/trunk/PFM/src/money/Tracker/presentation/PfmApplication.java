package money.Tracker.presentation;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.Logger;
import android.app.Application;
import android.content.Context;

public class PfmApplication extends Application{
	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        Logger.Log("Start applicaton", "money.tracker.presentation");
	        PfmApplication.context = getApplicationContext();

	        
			// Create db connector
			SqlHelper.instance = new SqlHelper(this);
			SqlHelper.instance.initializeTable();		
	    }

	    public static Context getAppContext() {
	        return PfmApplication.context;
	    }
}
