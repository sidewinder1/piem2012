package money.Tracker.common.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

public class Alert {
	private static Alert instance;
	private Toast toast;

	public static Alert getInstance() {
		if (instance == null) {
			instance = new Alert();
		}

		return instance;
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
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message).setCancelable(false)
				.setPositiveButton("Yes", okAction)
				.setNegativeButton("No", cancelAction);
		AlertDialog alert = builder.create();
		alert.show();
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
