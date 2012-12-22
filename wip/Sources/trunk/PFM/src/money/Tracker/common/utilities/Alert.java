package money.Tracker.common.utilities;

import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.HomeActivity;
import money.Tracker.presentation.activities.R;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

public class Alert {
	private static Alert sInstance;
	private Toast mToast;
	private AlertDialog mAlertDialog;
	private boolean mIsRunRemind;

	public static Alert getInstance() {
		if (sInstance == null) {
			sInstance = new Alert();
		}

		return sInstance;
	}

	/**
	 * Notify a message in Status bar when user is not in this application.
	 * @param activity
	 * The parent class that will be displayed when user clicks message.
	 * @param title
	 * The title of message.
	 * @param message
	 * The content of message.
	 * @param timeRemindInSeconds
	 * The time that message will notify again if user doesn't click on message.
	 * @param useDefaultRing
	 * Check whether user uses default ring or not.
	 * @param notifyRing
	 * The ring that message will use to notify.
	 * @param notifyId
	 * Id of message.
	 */
	public void notify(final Class<?> activity, final String title,
			final String message, final long timeRemindInSeconds,
			final boolean useDefaultRing, final Uri notifyRing, final int notifyId) {
		mIsRunRemind = true;
		new Thread(new Runnable() {
			public void run() {
				while (mIsRunRemind) {
					Context context = PfmApplication.getAppContext();
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							context).setSmallIcon(R.drawable.report)
							.setAutoCancel(true).setContentTitle(title)
							.setContentText(message);

					// Creates an explicit intent for an Activity in your app.
					Intent resultIntent = new Intent(context, activity);
					TaskStackBuilder stackBuilder = TaskStackBuilder
							.create(context);
					stackBuilder.addParentStack(HomeActivity.class);
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent = stackBuilder
							.getPendingIntent(0,
									PendingIntent.FLAG_UPDATE_CURRENT);

					mBuilder.setContentIntent(resultPendingIntent).setDefaults(
							Notification.DEFAULT_VIBRATE);
					NotificationManager mNotificationManager = (NotificationManager) context
							.getSystemService(Context.NOTIFICATION_SERVICE);

					if (useDefaultRing) {
						mBuilder.setDefaults(Notification.DEFAULT_SOUND);
					} else {
						if (notifyRing != null) {
							mBuilder.setSound(notifyRing,
									AudioManager.STREAM_MUSIC);
						} else {
							mBuilder.setSound(null);
						}
					}
					// mId allows you to update the notification later on.
					mNotificationManager.notify(notifyId, mBuilder.build());

					try {
						Thread.sleep(timeRemindInSeconds * 1000);
					} catch (InterruptedException e) {
						Logger.Log(e.getMessage(), "Alert");
					}
				}
			};
		}).start();
	}
	
	/**
	 * Stop reminding of message.
	 */
	public void stopNotify(){
		mIsRunRemind  = false;
	}

	/**
	 * Show dialog in application with specified message.
	 * @param context
	 * Parent data context.
	 * @param message
	 * The content of message.
	 * @param okAction
	 * The action when user clicks on OK button.
	 */
	public void showDialog(Context context, String message,
			OnClickListener okAction) {
		showDialog(context, message, okAction,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
	}

	/**
	 * Show dialog in application with specified message.
	 * @param context
	 * Parent data context.
	 * @param message
	 * The content of message.
	 * @param okAction
	 * The action when user clicks on OK button.
	 * @param cancelAction
	 * The action when user clicks on Cancel button.
	 */
	public void showDialog(Context context, String message,
			OnClickListener okAction, OnClickListener cancelAction) {
		if (mAlertDialog != null && mAlertDialog.isShowing()) {
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(
						PfmApplication.getAppResources()
								.getString(R.string.yes), okAction)
				.setNegativeButton(
						PfmApplication.getAppResources().getString(R.string.no),
						cancelAction);
		mAlertDialog = builder.create();
		mAlertDialog.show();
	}

	/**
	 * Show a message in application in short time.
	 * @param context
	 * The parent context.
	 * @param message
	 * The content of message.
	 * @return
	 * Result of showing message.
	 */
	public boolean show(Context context, String message) {
		if (mToast != null) {
			mToast.cancel();
		}

		mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		mToast.show();
		return true;
	}
}
