package money.Tracker.common.utilities;

import money.Tracker.presentation.PfmApplication;
import money.Tracker.presentation.activities.TabViewActivity;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

public class SynchronizeTask extends AsyncTask<Void, Void, Void> {
	public static Button sButton;
	public static boolean isRunning;

	public SynchronizeTask(Button refreshButton) {
		sButton = refreshButton;
	}

	public SynchronizeTask() {
		super();
	}

	public static boolean isSynchronizing(){
		return isRunning;
	} 
	@Override
	protected Void doInBackground(Void... arg0) {
		if (isRunning){
			return null;
		}
		
		isRunning = true;
		SyncHelper.getInstance().synchronize();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		isRunning = false;
		if (sButton != null) {
			((AnimationDrawable) sButton.getBackground()).stop();
			sButton.setVisibility(Boolean.parseBoolean(String.valueOf(sButton
					.getTag())) ? View.VISIBLE : View.GONE);
		}

		if (PfmApplication.sCurrentContext.getClass() == TabViewActivity.class){
			((TabViewActivity)PfmApplication.sCurrentContext).bindData();
		}
		
		super.onPostExecute(result);
	}
}
