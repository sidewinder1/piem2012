package money.Tracker.presentation;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import money.Tracker.common.sql.SqlHelper;
import money.Tracker.common.utilities.AccountProvider;
import money.Tracker.common.utilities.Alert;
import money.Tracker.common.utilities.Converter;
import money.Tracker.common.utilities.DateTimeHelper;
import money.Tracker.common.utilities.IOHelper;
import money.Tracker.common.utilities.Logger;
import money.Tracker.common.utilities.SynchronizeTask;
import money.Tracker.common.utilities.XmlParser;
import money.Tracker.presentation.activities.HomeActivity;
import money.Tracker.presentation.activities.R;
import money.Tracker.presentation.activities.SyncSettingActivity;
import money.Tracker.presentation.customviews.EmailAccountCustomView;
import money.Tracker.presentation.model.Entry;
import money.Tracker.repository.EntryRepository;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class PfmApplication extends Application {
	private static Context sContext;
	public static Context sCurrentContext;
	private String CONFIG_FILE = "PfmConfig.pxml";
	private static Locale sLocale = null;
	private static Context sBaseContext;
	private static SynchronizeTask syncTask = new SynchronizeTask(null);

	private static Thread runBackground = new Thread(new Runnable() {
		public void run() {
			// Looper.prepare();
			while (true) {
				try {
					if ("pfm.com".equals(AccountProvider.getInstance()
							.getCurrentAccount().type)
							|| syncTask.getStatus() == Status.RUNNING
							|| !runThread || SynchronizeTask.isSynchronizing()) {
						continue;
					}

					Button sync_data = null;
					if (SyncSettingActivity.sAccountList != null) {
						for (int index = 0; index < SyncSettingActivity.sAccountList
								.getChildCount(); index++) {
							EmailAccountCustomView email = (EmailAccountCustomView) SyncSettingActivity.sAccountList
									.getChildAt(index);
							if (email != null) {
								if (email.getActive()) {
									sync_data = email.getButton();
									// ((AnimationDrawable) sync_data
									// .getBackground()).start();
									sync_data.startAnimation(AnimationUtils
											.loadAnimation(sContext,
													R.anim.sync_background));
									sync_data.setVisibility(View.VISIBLE);
								} else {
//									((AnimationDrawable) email.getButton()
//											.getBackground()).stop();
									sync_data.getAnimation().cancel();
									sync_data.getAnimation().reset();
									sync_data.clearAnimation();
								}
							}
						}
					}

					syncTask = new SynchronizeTask(sync_data);
					syncTask.execute();
					Thread.sleep(1 * 60000);
				} catch (Exception e) {
					Logger.Log(e.getMessage(), "PfmApplication");
				}
			}
			// Looper.loop();
		}
	});

	public static long getTotalEntry() {
		EntryRepository.getInstance().updateData(
				new StringBuilder("Type = 1").toString());
		if (EntryRepository.getInstance().orderedEntries == null) {
			return 0;
		}

		ArrayList<Entry> entries = EntryRepository.getInstance().orderedEntries
				.get(Converter.toString(DateTimeHelper.now(false), "MM/yyyy"));
		long total_entry = 0;
		for (Entry entryItem : entries) {
			total_entry += entryItem.getTotal();
		}

		return total_entry;
	}

	public static long getTotalBudget() {
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
										currentDate.getYear() + 1900,
										currentDate.getMonth()))).append("')")
						.toString());
		if (totalBudgetCursor != null && totalBudgetCursor.moveToFirst()) {
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
									HomeActivity.class,
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

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// if (sLocale != null) {
	// newConfig.locale = sLocale;
	// Locale.setDefault(sLocale);
	// getBaseContext().getResources().updateConfiguration(newConfig,
	// getBaseContext().getResources().getDisplayMetrics());
	// }
	// }

	public void onCreate() {
		super.onCreate();
		PfmApplication.sContext = getApplicationContext();
		sBaseContext = getBaseContext();

		// Create config file.
		IOHelper.getInstance()
				.createFile(
						CONFIG_FILE,
						"<config><autoSync>false</autoSync><serverConfig><namespace>http://pfm.org/</namespace><url>http://54.251.59.102:83/PFMService.asmx</url></serverConfig></config>");

		// Create DB connector.
		SqlHelper.instance = new SqlHelper(this);

		// Create table for application configuration.
		SqlHelper.instance
				.createTable(
						"AppInfo",
						new StringBuilder(
								"Id LONG PRIMARY KEY, UserName TEXT, ")
								.append("ScheduleWarn INTEGER, ScheduleRing TEXT, ScheduleRemind LONG, ")
								.append("BorrowWarn LONG, BorrowRing TEXT, BorrowRemind LONG, ")
								.append("LastSync DATE, Status INTEGER, CreatedDate DATE, ")
								.append("ModifiedDate DATE, IsDeleted INTEGER, ")
								.append("Language TEXT").toString());

		// Cursor languageCursor = SqlHelper.instance.select(
		// "AppInfo",
		// "Language",
		// new StringBuilder("UserName = '")
		// .append(AccountProvider.getInstance()
		// .getCurrentAccount().name).append("'")
		// .toString());
		// String lang = "vn";
		// if (languageCursor != null && languageCursor.moveToFirst()
		// && languageCursor.getString(0) != null) {
		// lang = languageCursor.getString(0);
		// }
		//
		// setDefaultLanguage(lang);

		SqlHelper.instance.initializeTable();

		warningTimer.start();
		runBackground.start();
		if (!Boolean.parseBoolean(XmlParser.getInstance().getConfigContent(
				"autoSync"))) {
			runThread = false;
		} else {
			runThread = true;
		}
	}

	public static void setDefaultLanguage(String lang) {
		Configuration config = sBaseContext.getResources().getConfiguration();
		if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
			sLocale = new Locale(lang);
			Locale.setDefault(sLocale);
			config.locale = sLocale;
			sBaseContext.getResources().updateConfiguration(config,
					sBaseContext.getResources().getDisplayMetrics());

		}
	}

	private static boolean runThread;

	public static Context getAppContext() {
		return PfmApplication.sContext;
	}

	public static Resources getAppResources() {
		return sBaseContext.getResources();
	}

	public static void startSynchronize() {
		runThread = true;
	}

	public static void stopSynchronize() {
		runThread = false;
	}
}
