package money.Tracker.common.utilities;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
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

	public boolean show(Context context, String message) {
		if (toast != null) {
			toast.cancel();
		}

		toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
		return true;
	}

}
