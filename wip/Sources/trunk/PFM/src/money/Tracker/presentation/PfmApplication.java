package money.Tracker.presentation;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.activities.SyncSettingActivity;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask.Status;
import android.view.View;

public class PfmApplication extends Application {
	private static Context sContext;
	public static Context sCurrentContext;
	private String CONFIG_FILE = "PfmConfig.pxml";
	private static Resources sResources;
	private static SynchronizeTask syncTask = new SynchronizeTask(null);

	private static Thread runBackground = new Thread(new Runnable() {
		public void run() {
			// Looper.prepare();
			try {
				while (true) {
					if ("pfm.com".equals(AccountProvider.getInstance()
							.getCurrentAccount().type)
							|| syncTask.getStatus() == Status.RUNNING
							|| !runThread
							|| SynchronizeTask.isSynchronizing()) {
						continue;
					}

					for (int index = 0; index < SyncSettingActivity.sAccountList
							.getChildCount(); index++) {
						EmailAccountCustomView email = (EmailAccountCustomView) SyncSettingActivity.sAccountList
								.getChildAt(index);
						if (email != null && email.getActive()) {
							((AnimationDrawable) email.getButton()
									.getBackground()).start();
							email.getButton().setVisibility(View.VISIBLE);
						}
					}

					syncTask = new SynchronizeTask();			
					syncTask.execute();
					Thread.sleep(1 * 3600000);
				}
			} catch (Exception e) {
				Logger.Log(e.getMessage(), "PfmApplication");
			}
			// Looper.loop();
		}
	});

	public void onCreate() {
		super.onCreate();
		PfmApplication.sContext = getApplicationContext();
		PfmApplication.sResources = getResources();

		// Create config file.
		IOHelper.getInstance()
				.createFile(
						CONFIG_FILE,
						"<config><autoSync>false</autoSync><serverConfig><namespace>http://pfm.org/</namespace><url>http://54.251.59.102:83/PFMService.asmx</url></serverConfig></config>");

		// Create db connector.
		SqlHelper.instance = new SqlHelper(this);
		SqlHelper.instance.initializeTable();
		Logger.Log("Start applicaton "
				+ AccountProvider.getInstance().getAccounts().size(),
				"money.tracker.presentation");
		runBackground.start();
		if (!Boolean.parseBoolean(XmlParser.getInstance().getConfigContent(
				"autoSync"))) {
			runThread = false;
		}
	}

	private static boolean runThread;

	public static Context getAppContext() {
		return PfmApplication.sContext;
	}

	public static Resources getAppResources() {
		return PfmApplication.sResources;
	}

	public static void startSynchronize() {
		runThread = true;
	}

	public static void stopSynchronize() {
		runThread = false;
	}
}
