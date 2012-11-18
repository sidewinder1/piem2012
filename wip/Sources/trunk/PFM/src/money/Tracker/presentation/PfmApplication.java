package money.Tracker.presentation;

import java.io.File;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

public class PfmApplication extends Application{
	 private static Context context;
     private String CONFIG_FILE = "Pfm/PfmConfig.xml";
 	private static String mBaseDir = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	    public void onCreate(){
	        super.onCreate();
	        Logger.Log("Start applicaton", "money.tracker.presentation");
	        PfmApplication.context = getApplicationContext();

	        // Create config file.
	        IOHelper.getInstance().createFile(mBaseDir + File.separator + CONFIG_FILE, "<config><account>testAccount</account><lastSync>2012-11-17 17:32:00</lastSync><serverConfig><namespace>http://tempuri.org/</namespace><url>http://10.0.2.2:1242/PFMService.asmx</url></serverConfig></config>");
	        
			// Create db connector.
			SqlHelper.instance = new SqlHelper(this);
			SqlHelper.instance.initializeTable();		
	    }

	    public static Context getAppContext() {
	        return PfmApplication.context;
	    }
}
