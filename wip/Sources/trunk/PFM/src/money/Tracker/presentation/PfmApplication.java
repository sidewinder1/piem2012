package money.Tracker.presentation;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import android.app.Application;
import android.content.Context;

public class PfmApplication extends Application{
	 private static Context context;
     private String CONFIG_FILE = "PfmConfig.pxml";

	    public void onCreate(){
	        super.onCreate();
	        PfmApplication.context = getApplicationContext();

	        // Create config file.
	        IOHelper.getInstance().createFile(CONFIG_FILE, "<config><account></account><lastSync>2012-11-17 17:32:00</lastSync><serverConfig><namespace>http://pfm.org/</namespace><url>http://54.251.59.102:83/PFMService.asmx</url></serverConfig></config>");
	        
			// Create db connector.
			SqlHelper.instance = new SqlHelper(this);
			SqlHelper.instance.initializeTable();
			Logger.Log("Start applicaton " + AccountProvider.getInstance().getAccounts().size(), "money.tracker.presentation");
	    }

	    public static Context getAppContext() {
	        return PfmApplication.context;
	    }
}
