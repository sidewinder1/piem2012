package money.Tracker.presentation;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class PfmApplication extends Application{
	 private static Context sContext;
     private String CONFIG_FILE = "PfmConfig.pxml";
     private static Resources sResources;
     
	    public void onCreate(){
	        super.onCreate();
	        PfmApplication.sContext = getApplicationContext();
	        PfmApplication.sResources = getResources();

	        // Create config file.
	        IOHelper.getInstance().createFile(CONFIG_FILE, "<config><account></account><lastSync>2012-11-17 17:32:00</lastSync><serverConfig><namespace>http://pfm.org/</namespace><url>http://54.251.59.102:83/PFMService.asmx</url></serverConfig></config>");
	        
			// Create db connector.
			SqlHelper.instance = new SqlHelper(this);
			SqlHelper.instance.initializeTable();
			Logger.Log("Start applicaton " + AccountProvider.getInstance().getAccounts().size(), "money.tracker.presentation");
	    }

	    public static Context getAppContext() {
	        return PfmApplication.sContext;
	    }
	    
	    public static Resources getAppResources(){
	    	return PfmApplication.sResources;
	    }
}
