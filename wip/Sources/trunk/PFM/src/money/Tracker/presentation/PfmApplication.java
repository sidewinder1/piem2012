package money.Tracker.presentation;

import android.app.Application;
import android.content.Context;

public class PfmApplication extends Application{
	 private static Context context;

	    public void onCreate(){
	        super.onCreate();
	        PfmApplication.context = getApplicationContext();
	    }

	    public static Context getAppContext() {
	        return PfmApplication.context;
	    }
}
