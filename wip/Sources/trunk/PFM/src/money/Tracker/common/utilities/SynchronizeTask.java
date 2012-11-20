package money.Tracker.common.utilities;

import money.Tracker.presentation.activities.R;
import android.os.AsyncTask;
import android.widget.Button;

public class SynchronizeTask extends AsyncTask<Void, Void, Void> {
	private static Button sButton;

	public SynchronizeTask(Button refreshButton) {
		sButton = refreshButton;
	}

	@Override
	protected Void doInBackground(Void... arg0) {
		SyncHelper.getInstance().synchronize();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		sButton.setBackgroundResource(R.drawable.unsyn_icon);
		super.onPostExecute(result);
	}

}
