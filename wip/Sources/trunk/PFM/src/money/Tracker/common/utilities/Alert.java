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
	private static Alert instance;
	private Toast toast;
	private AlertDialog alertDialog;

	public static Alert getInstance() {
		if (instance == null) {
			instance = new Alert();
		}

		return instance;
	}

	public void notify(final Class<?> activity, final String title,
			final String message, final long timeRemind,
			final boolean useDefaultRing, final Uri notifyRing) {
		new Thread(new Runnable() {
			public void run() {
				Context context = PfmApplication.getAppContext();
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.report).setAutoCancel(true)
						.setContentTitle(title).setContentText(message);

				// Creates an explicit intent for an Activity in your app.
				Intent resultIntent = new Intent(context, activity);
				TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(context);
				stackBuilder.addParentStack(HomeActivity.class);
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

				int mId = 0;
				mBuilder.setContentIntent(resultPendingIntent).setDefaults(
						Notification.DEFAULT_VIBRATE);
				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				try {
					Thread.sleep(timeRemind * 1000);
				} catch (InterruptedException e) {
					Logger.Log(e.getMessage(), "Alert");
				}

				if (useDefaultRing) {
					mBuilder.setDefaults(Notification.DEFAULT_SOUND);
				} else {
					if (notifyRing != null) {
						mBuilder.setSound(notifyRing, AudioManager.STREAM_MUSIC);
					} else {
						mBuilder.setSound(null);
					}
				}
				// mId allows you to update the notification later on.
				mNotificationManager.notify(mId, mBuilder.build());
			};
		}).start();
	}

	public void showDialog(Context context, String message,
			OnClickListener okAction) {
		showDialog(context, message, okAction,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
	}

	public void showDialog(Context context, String message,
			OnClickListener okAction, OnClickListener cancelAction) {
		if (alertDialog != null && alertDialog.isShowing()) {
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
		alertDialog = builder.create();
		alertDialog.show();
	}

	public boolean show(Context context, String message) {
		if (toast != null) {
			toast.cancel();
		}

		toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
		return true;
	}

}
