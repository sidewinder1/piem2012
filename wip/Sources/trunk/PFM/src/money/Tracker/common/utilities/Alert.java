package money.Tracker.common.utilities;

import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.R;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
			final String message, final long delayInSeconds) {
		new Thread(new Runnable() {
			public void run() {
				Context context = PfmApplication.getAppContext();
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context).setSmallIcon(R.drawable.report_icon)
						.setContentTitle(title).setContentText(message);
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(context, activity);

				// The stack builder object will contain an artificial back
				// stack for
				// the
				// started Activity.
				// This ensures that navigating backward from the Activity leads
				// out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder
						.create(context);
				// Adds the back stack for the Intent (but not the Intent
				// itself)
//				stackBuilder.addParentStack(activity);
				// Adds the Intent that starts the Activity to the top of the
				// stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent = stackBuilder
						.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

				int mId = 0;
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);

				try {
					Thread.sleep(delayInSeconds * 1000);
				} catch (InterruptedException e) {
					Logger.Log(e.getMessage(), "Alert");
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
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton(PfmApplication.getAppResources().getString(R.string.yes), okAction)
				.setNegativeButton(PfmApplication.getAppResources().getString(R.string.no), cancelAction);
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
