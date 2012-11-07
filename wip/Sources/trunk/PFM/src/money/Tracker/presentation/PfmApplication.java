package money.Tracker.presentation;

import money.Tracker.common.sql.SqlHelper;
import android.app.Application;
import android.content.Context;

public class PfmApplication extends Application{
	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        PfmApplication.context = getApplicationContext();

			// Create db connector
			SqlHelper.instance = new SqlHelper(this);
			SqlHelper.instance.initializeTable();
	    }

	    public static Context getAppContext() {
	        return PfmApplication.context;
	    }
}
