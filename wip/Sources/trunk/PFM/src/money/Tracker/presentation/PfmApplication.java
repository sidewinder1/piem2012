package money.Tracker.presentation;

import java.util.ArrayList;
import java.util.Date;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.activities.BorrowLendMainViewActivity;
import money.Tracker.presentation.activities.SyncSettingActivity;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.repository.EntryRepository;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.view.View;
import android.widget.Button;

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
							|| !runThread || SynchronizeTask.isSynchronizing()) {
						continue;
					}

					Button sync_data = null;
					for (int index = 0; index < SyncSettingActivity.sAccountList
							.getChildCount(); index++) {
						EmailAccountCustomView email = (EmailAccountCustomView) SyncSettingActivity.sAccountList
								.getChildAt(index);
						if (email != null && email.getActive()) {
							sync_data = email.getButton();
						}
					}

					if (((AnimationDrawable) sync_data.getBackground())
							.isRunning()) {
						continue;
					}

					((AnimationDrawable) sync_data.getBackground()).start();
					sync_data.setVisibility(View.VISIBLE);
					syncTask = new SynchronizeTask(sync_data);
					syncTask.execute();
					Thread.sleep(1 * 3600000);
				}
			} catch (Exception e) {
				Logger.Log(e.getMessage(), "PfmApplication");
			}
			// Looper.loop();
		}
	});

	public static long getTotalEntry() {
		EntryRepository.getInstance().updateData(
				new StringBuilder("Type = 1").toString());
		ArrayList<Entry> entries = EntryRepository.getInstance().orderedEntries
				.get(Converter.toString(DateTimeHelper.now(false), "MMMM, yyyy"));
		long total_entry = 0;
		for (Entry entryItem : entries) {
			total_entry += entryItem.getTotal();
		}

		return total_entry;
	}

	public static long getTotalBudget(){
		Date currentDate = DateTimeHelper.now(false);
		Cursor totalBudgetCursor = SqlHelper.instance.select(
				"Schedule",
				"Budget, Type",
				new StringBuilder("Type = 1 AND (End_date = '")
						.append(Converter.toString(DateTimeHelper
								.getLastDayOfWeek(currentDate)))
						.append("' OR End_date = '")
						.append(Converter.toString(DateTimeHelper
								.getLastDateOfMonth(
										currentDate.getYear() + 1900, currentDate.getMonth())))
						.append("')").toString());
		if (totalBudgetCursor != null && totalBudgetCursor.moveToFirst()){
			return totalBudgetCursor.getLong(0);
		}
		
		return 0;
	}

	private static Thread warningTimer = new Thread(new Runnable() {
		public void run() {
			// Looper.prepare();
			try {
				while (true) {
					// if ("pfm.com".equals(AccountProvider.getInstance()
					// .getCurrentAccount().type)
					// || syncTask.getStatus() == Status.RUNNING
					// || !runThread || SynchronizeTask.isSynchronizing()) {
					// continue;
					// }

					Cursor time = SqlHelper.instance.select(
							"AppInfo",
							"BorrowWarn, BorrowRing",
							new StringBuilder("UserName='")
									.append(AccountProvider.getInstance()
											.getCurrentAccount().name)
									.append("'").toString());
					if (time != null && time.moveToFirst()) {
						int longTime = Integer.parseInt(time.getString(0));

						Cursor checkBorrow = SqlHelper.instance.select(
								"BorrowLend",
								"Expired_date, Person_name",
								"Expired_date='"
										+ Converter.toString(DateTimeHelper
												.addHours(DateTimeHelper
														.now(false), longTime))
										+ "'");
						if (checkBorrow != null && checkBorrow.moveToFirst()) {
							Alert.getInstance().notify(
									BorrowLendMainViewActivity.class,
									"Expired date",
									checkBorrow.getString(1),
									0,
									"#DEFAULT".equals(time.getString(1)),
									time.getString(1).startsWith("#") ? null
											: Uri.parse(time.getString(1)));
						}
					}
					Thread.sleep(1 * 1000);
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
		warningTimer.start();
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
